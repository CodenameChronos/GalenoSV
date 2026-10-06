package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoService extends ParentService<ProcedimientoPaso> {

    private static final String TIPO_SECUENCIA_PENDIENTE = "SECUENCIAL";

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoService() {
        super(ProcedimientoPaso.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ProcedimientoPaso> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("ProcedimientoPaso.findActiveByNombre", ProcedimientoPaso.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Lista todos los pasos de un procedimiento, con su Rol precargado para
     * mostrarlo en el árbol sin LazyInitializationException.
     *
     * @param idProcedimiento el procedimiento cuyos pasos se listan.
     * @return los pasos de ese procedimiento, sin ningún orden particular
     */
    public List<ProcedimientoPaso> listarPorProcedimiento(UUID idProcedimiento) {
        return em.createNamedQuery("ProcedimientoPaso.findByProcedimiento",
                        ProcedimientoPaso.class)
                .setParameter("idProcedimiento", idProcedimiento)
                .getResultList();
    }

    public void crearPasoInicial(ProcedimientoPaso nuevo, Procedimiento procedimiento,
                                 List<Examen> examenes) {
        long existentes = contarPasosSinPadre(procedimiento.getIdProcedimiento());
        if (existentes > 0) {
            throw new ReglaNegocioException(
                    "Este procedimiento ya tiene un paso inicial; no puede tener otro.");
        }
        nuevo.setIdProcedimiento(procedimiento);
        em.persist(nuevo);
        sincronizarExamenes(nuevo, examenes);
    }

    /**
     * Crea un paso hijo de un paso ya existente. En la misma transacción
     * registra el borde de ProcedimientoPasoSecuencia que los conecta
     * y los exámenes del paso nuevo.
     *
     * @param nuevo el paso hijo a crear, con nombre, rol e indicaFin ya
     * asignados. Su procedimiento se toma del padre.
     * @param padre el paso que será el padre del nuevo.
     * @param examenes los exámenes que tendrá el paso nuevo; puede ser vacía
     * o null
     * @throws ReglaNegocioException si el padre ya indica fin de rama
     * (indicaFin = true), o si el padre ya tiene un hijo (cada paso admite
     * como máximo uno).
     */
    public void crearPasoHijo(ProcedimientoPaso nuevo, ProcedimientoPaso padre,
                              List<Examen> examenes) {
        if (Boolean.TRUE.equals(padre.getIndicaFin())) {
            throw new ReglaNegocioException(
                    "El paso seleccionado ya indica fin de rama; no se le pueden agregar pasos siguientes.");
        }
        if (contarHijos(padre.getIdProcedimientoPaso()) > 0) {
            throw new ReglaNegocioException(
                    "El paso seleccionado ya tiene un paso siguiente; cada paso solo puede tener uno.");
        }
        nuevo.setIdProcedimiento(padre.getIdProcedimiento());
        em.persist(nuevo);
        ProcedimientoPasoSecuencia borde = new ProcedimientoPasoSecuencia();
        borde.setIdProcedimientoPaso(nuevo);
        borde.setIdProcedimientoPasoReferencia(padre.getIdProcedimientoPaso());
        borde.setTipoSecuencia(TIPO_SECUENCIA_PENDIENTE);
        em.persist(borde);

        sincronizarExamenes(nuevo, examenes);
    }

    /**
     * Actualiza un paso ya guardado y deja sus exámenes exactamente como
     * indica la lista recibida (agrega los nuevos y quita los que sobran),
     * todo en una transacción. Las llaves
     * foráneas (rol y procedimiento) no se modifican una vez guardado el
     * registro, aunque el objeto recibido traiga otro valor
     *
     * @param paso el paso con nombre e indicaFin ya modificados.
     * @param examenes la lista final de exámenes del paso; puede ser vacía o
     * null (en ese caso el paso queda sin exámenes).
     * @throws ReglaNegocioException si el paso ya no existe en la base
     */
    public void actualizarPaso(ProcedimientoPaso paso, List<Examen> examenes) {
        ProcedimientoPaso gestionado = em.find(ProcedimientoPaso.class, paso.getIdProcedimientoPaso());
        if (gestionado == null) {
            throw new ReglaNegocioException("El paso que intenta modificar ya no existe.");
        }
        gestionado.setNombre(paso.getNombre());
        gestionado.setIndicaFin(paso.getIndicaFin());
        sincronizarExamenes(gestionado, examenes);
    }

    /**
     * Elimina un paso junto con sus exámenes y su borde hacia el padre, en una
     * sola transacción. Solo se puede eliminar el último paso de la cadena
     * (el que no tiene hijos); para quitar uno intermedio hay que borrar antes
     * los que le siguen.
     *
     * @param idPaso el identificador del paso a eliminar.
     * @throws ReglaNegocioException si el paso todavía tiene un paso siguiente.
     */
    public void eliminarPaso(UUID idPaso) {
        if (contarHijos(idPaso) > 0) {
            throw new ReglaNegocioException(
                    "No se puede eliminar un paso que tiene pasos siguientes; elimine primero los siguientes.");
        }
        em.createQuery("DELETE FROM ProcedimientoPasoExamen pe "
                        + "WHERE pe.idProcedimientoPaso.idProcedimientoPaso = :idPaso")
                .setParameter("idPaso", idPaso)
                .executeUpdate();
        em.createQuery("DELETE FROM ProcedimientoPasoSecuencia s "
                        + "WHERE s.idProcedimientoPaso.idProcedimientoPaso = :idPaso")
                .setParameter("idPaso", idPaso)
                .executeUpdate();
        em.createQuery("DELETE FROM ProcedimientoPaso p WHERE p.idProcedimientoPaso = :idPaso")
                .setParameter("idPaso", idPaso)
                .executeUpdate();
    }

    /**
     * Deja los exámenes de un paso exactamente como la lista deseada: crea las
     * asignaciones que faltan, borra las que sobran y no toca las que ya
     * existen. Los repetidos de la lista se ignoran.
     *
     * @param paso el paso (ya persistido o gestionado) dueño de los exámenes.
     * @param deseados la lista final de exámenes; puede ser {@code null}.
     */
    private void sincronizarExamenes(ProcedimientoPaso paso, List<Examen> deseados) {
        Map<UUID, Examen> deseadosPorId = new LinkedHashMap<>();
        if (deseados != null) {
            for (Examen e : deseados) {
                deseadosPorId.putIfAbsent(e.getIdExamen(), e);
            }
        }

        List<ProcedimientoPasoExamen> actuales = em.createQuery(
                        "SELECT pe FROM ProcedimientoPasoExamen pe "
                                + "WHERE pe.idProcedimientoPaso.idProcedimientoPaso = :idPaso",
                        ProcedimientoPasoExamen.class)
                .setParameter("idPaso", paso.getIdProcedimientoPaso())
                .getResultList();
        Set<UUID> yaExisten = new HashSet<>();
        for (ProcedimientoPasoExamen pe : actuales) {
            UUID idExamen = pe.getIdExamen().getIdExamen();
            if (deseadosPorId.containsKey(idExamen)) {
                yaExisten.add(idExamen);
            } else {
                em.remove(pe);
            }
        }
        for (UUID idExamen : deseadosPorId.keySet()) {
            if (!yaExisten.contains(idExamen)) {
                ProcedimientoPasoExamen nuevo = new ProcedimientoPasoExamen();
                nuevo.setIdProcedimientoPaso(paso);
                nuevo.setIdExamen(em.getReference(Examen.class, idExamen));
                em.persist(nuevo);
            }
        }
    }

    public ProcedimientoPaso obtenerPasoInicial(UUID idProcedimiento) {
        List<ProcedimientoPaso> candidatos = em.createQuery(
                        "SELECT p FROM ProcedimientoPaso p WHERE "
                                + "p.idProcedimiento.idProcedimiento = :idProcedimiento AND "
                                + "p NOT IN (SELECT pps.idProcedimientoPaso FROM ProcedimientoPasoSecuencia pps "
                                + "WHERE pps.idProcedimientoPaso.idProcedimiento.idProcedimiento = :idProcedimiento)",
                        ProcedimientoPaso.class)
                .setParameter("idProcedimiento", idProcedimiento)
                .setMaxResults(1)
                .getResultList();
        return candidatos.isEmpty() ? null : candidatos.get(0);
    }

    public long contarHijos(UUID idPaso) {
        return em.createNamedQuery("ProcedimientoPaso.countHijos",
                        Long.class)
                .setParameter("idPaso", idPaso)
                .getSingleResult();
    }

    /**
     * Cuenta los pasos de un procedimiento que no son hijos de nadie (no
     * tienen borde que los señale como hijos). Son los candidatos a ser la
     * raíz; en un procedimiento sano debe haber exactamente uno.
     *
     * @param idProcedimiento el identificador del procedimiento a revisar.
     * @return la cantidad de pasos sin padre.
     * @throws IllegalStateException si falla la consulta a la base.
     */
    public long contarPasosSinPadre(UUID idProcedimiento) {
        try {
            return em.createNamedQuery("ProcedimientoPaso.countPasoSinPadre",
                            Long.class)
                    .setParameter("idProcedimiento", idProcedimiento)
                    .getSingleResult();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }
}
