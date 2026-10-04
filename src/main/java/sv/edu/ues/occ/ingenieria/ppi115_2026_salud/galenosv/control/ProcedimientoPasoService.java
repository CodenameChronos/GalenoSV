package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

/**
 * Servicio que orquesta las operaciones sobre el árbol de pasos de un
 * {@link Procedimiento}.
 *
 * <p><b>¿Por qué existe, si ya tenemos DAO?</b> Los DAO conocen una sola
 * entidad cada uno. Pero guardar un paso toca varias tablas a la vez: el paso
 * ({@link ProcedimientoPaso}), su borde hacia el padre
 * ({@link ProcedimientoPasoSecuencia}) y sus exámenes
 * ({@link ProcedimientoPasoExamen}). Todo eso tiene que pasar junto o no
 * pasar: como cada método público de este EJB corre en <i>una</i> transacción,
 * si algo falla a la mitad se revierte todo, y nunca queda, por ejemplo, un
 * paso guardado sin su borde (que el árbol leería como una segunda raíz).</p>
 *
 * <p><b>Modelo de datos en una línea:</b> los pasos de un procedimiento forman
 * una cadena. Cada paso (menos el inicial) tiene un borde que apunta a su
 * padre, y cada paso puede tener como máximo un hijo.</p>
 *
 * <p>Las reglas de negocio se lanzan como {@link ReglaNegocioException}
 * ({@code @ApplicationException}), para que el contenedor no la envuelva en
 * una {@code EJBException} y la capa JSF pueda mostrar el mensaje tal cual.</p>
 *
 * <p>Pendiente: {@code tipoSecuencia} no se usa todavía en la pantalla, pero
 * la entidad lo exige {@code @NotBlank}; por eso se manda el valor fijo
 * {@link #TIPO_SECUENCIA_PENDIENTE}. Hay que revisar qué significa de verdad
 * y reemplazarlo.</p>
 */
@Stateless
public class ProcedimientoPasoService {

    /** Valor temporal para {@code tipoSecuencia} (ver nota de la clase). */
    private static final String TIPO_SECUENCIA_PENDIENTE = "SECUENCIAL";

    @PersistenceContext
    private EntityManager em;

    /**
     * Crea el paso inicial (la raíz) de un procedimiento. La raíz es el único
     * paso que no tiene borde de secuencia, y por eso aquí no se crea ninguno.
     *
     * @param nuevo el paso a crear, con nombre, rol e indicaFin ya asignados.
     * @param procedimiento el procedimiento al que pertenecerá el paso.
     * @param examenes los exámenes que tendrá el paso; puede ser vacía o
     * {@code null}.
     * @throws ReglaNegocioException si el procedimiento ya tiene un paso
     * inicial (solo puede haber uno).
     */
    public void crearPasoInicial(ProcedimientoPaso nuevo, Procedimiento procedimiento,
                                 List<Examen> examenes) {
        // "Raíz" = paso sin padre. Si ya existe una, no se permite otra.
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
     * registra el borde de {@link ProcedimientoPasoSecuencia} que los conecta
     * y los exámenes del paso nuevo.
     *
     * @param nuevo el paso hijo a crear, con nombre, rol e indicaFin ya
     * asignados. Su procedimiento se toma del padre.
     * @param padre el paso que será el padre del nuevo.
     * @param examenes los exámenes que tendrá el paso nuevo; puede ser vacía
     * o {@code null}.
     * @throws ReglaNegocioException si el padre ya indica fin de rama
     * (indicaFin = true), o si el padre ya tiene un hijo (cada paso admite
     * como máximo uno).
     */
    public void crearPasoHijo(ProcedimientoPaso nuevo, ProcedimientoPaso padre,
                              List<Examen> examenes) {
        // La pantalla ya oculta el botón "+" en estos casos, pero aquí se
        // vuelve a comprobar: otra pestaña u otro usuario pudo cambiar el
        // árbol después de que se dibujó.
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

        // El borde guarda el UUID del padre como dato crudo (no hay FK real
        // hacia el padre en la base); el árbol lo resuelve luego en memoria.
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
     * todo en una transacción.
     *
     * <p>Solo se copian los campos editables: nombre e indicaFin. Las llaves
     * foráneas (rol y procedimiento) no se modifican una vez guardado el
     * registro, aunque el objeto recibido traiga otro valor.</p>
     *
     * @param paso el paso con nombre e indicaFin ya modificados.
     * @param examenes la lista final de exámenes del paso; puede ser vacía o
     * {@code null} (en ese caso el paso queda sin exámenes).
     * @throws ReglaNegocioException si el paso ya no existe en la base (por
     * ejemplo, otro usuario lo eliminó mientras se editaba).
     */
    public void actualizarPaso(ProcedimientoPaso paso, List<Examen> examenes) {
        // Se busca la versión guardada en vez de hacer merge del objeto que
        // llega: así no se cuela ningún cambio en campos que no deben tocarse.
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
        // El orden importa por las llaves foráneas: primero lo que depende del
        // paso (exámenes y borde) y al final el paso mismo.
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
     * <p>Es privado a propósito: siempre se llama desde un método público de
     * este servicio, para que corra dentro de la misma transacción que el
     * paso.</p>
     *
     * @param paso el paso (ya persistido o gestionado) dueño de los exámenes.
     * @param deseados la lista final de exámenes; puede ser {@code null}.
     */
    private void sincronizarExamenes(ProcedimientoPaso paso, List<Examen> deseados) {
        // Se indexa por id (y se descartan repetidos) para comparar rápido
        // contra lo que ya hay en la base.
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

        // Primera pasada: lo que ya existe se queda (se anota) y lo que ya no
        // está en la lista deseada se borra.
        Set<UUID> yaExisten = new HashSet<>();
        for (ProcedimientoPasoExamen pe : actuales) {
            UUID idExamen = pe.getIdExamen().getIdExamen();
            if (deseadosPorId.containsKey(idExamen)) {
                yaExisten.add(idExamen);
            } else {
                em.remove(pe);
            }
        }

        // Segunda pasada: lo deseado que todavía no existía se crea.
        // getReference evita cargar el examen completo solo para enlazarlo.
        for (UUID idExamen : deseadosPorId.keySet()) {
            if (!yaExisten.contains(idExamen)) {
                ProcedimientoPasoExamen nuevo = new ProcedimientoPasoExamen();
                nuevo.setIdProcedimientoPaso(paso);
                nuevo.setIdExamen(em.getReference(Examen.class, idExamen));
                em.persist(nuevo);
            }
        }
    }

    /**
     * Devuelve el paso inicial (la raíz) de un procedimiento: el que no es
     * hijo de nadie. Lo usa Consultas para saber con qué paso arrancar un
     * procedimiento aplicado a una consulta.
     *
     * @param idProcedimiento el procedimiento (plantilla) a consultar.
     * @return el paso inicial, o {@code null} si el procedimiento aún no tiene pasos.
     */
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

    /**
     * Cuenta los hijos directos de un paso, es decir, los bordes cuya
     * referencia apunta a él. Como cada paso admite como máximo un hijo, el
     * resultado esperado es 0 o 1.
     *
     * @param idPaso el identificador del paso padre a consultar.
     * @return cuántos pasos tienen a este paso como padre.
     */
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