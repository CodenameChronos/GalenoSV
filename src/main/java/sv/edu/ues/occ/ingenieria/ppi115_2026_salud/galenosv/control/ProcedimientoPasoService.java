package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

@Stateless
public class ProcedimientoPasoService {

    private static final String TIPO_SECUENCIA_PENDIENTE = "SECUENCIAL";

    @PersistenceContext
    private EntityManager em;

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

    public void actualizarPaso(ProcedimientoPaso paso, List<Examen> examenes) {
        ProcedimientoPaso gestionado = em.find(ProcedimientoPaso.class, paso.getIdProcedimientoPaso());
        if (gestionado == null) {
            throw new ReglaNegocioException("El paso que intenta modificar ya no existe.");
        }
        gestionado.setNombre(paso.getNombre());
        gestionado.setIndicaFin(paso.getIndicaFin());
        sincronizarExamenes(gestionado, examenes);
    }

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

    public long contarHijos(UUID idPaso) {
        return em.createNamedQuery("ProcedimientoPaso.countHijos",
                        Long.class)
                .setParameter("idPaso", idPaso)
                .getSingleResult();
    }

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