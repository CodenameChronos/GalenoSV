package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;

/**
 * DAO de ConsultaProcedimientoPaso.
 *
 * findRange/buscar (heredados de DefaultDAO) usan joinFetchClause() con la
 * cadena completa de padres: ConsultaProcedimiento (y, a traves de el,
 * Procedimiento y toda la cadena hasta Persona), el PersonaRol propio del
 * paso, y ahora tambien ProcedimientoPaso (el paso de la plantilla que este
 * registro instancia), agregado para poder mostrar su nombre en pantalla.
 */
@Stateless
public class ConsultaProcedimientoPasoDAO extends DefaultDAO<ConsultaProcedimientoPaso> {

    @PersistenceContext
    private EntityManager em;

    public ConsultaProcedimientoPasoDAO() {
        super(ConsultaProcedimientoPaso.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    protected String joinFetchClause() {
        return "LEFT JOIN FETCH e.idConsultaProcedimiento cp "
                + "LEFT JOIN FETCH cp.idProcedimiento "
                + "LEFT JOIN FETCH cp.idConsulta c "
                + "LEFT JOIN FETCH c.idPersonaRol prc "
                + "LEFT JOIN FETCH prc.idPersona "
                + "LEFT JOIN FETCH e.idPersonaRol pr "
                + "LEFT JOIN FETCH pr.idPersona "
                + "LEFT JOIN FETCH pr.idRol "
                + "LEFT JOIN FETCH e.idProcedimientoPaso";
    }

    /**
     * Busca los pasos de un ConsultaProcedimiento dado, opcionalmente
     * filtrados por nombre o apellido de la persona responsable del paso
     * (via su propio PersonaRol). Con patron nulo devuelve todos los pasos
     * de ese ConsultaProcedimiento. Usado por el paso 3 del wizard de
     * OrdenExamen.
     *
     * @param cp el ConsultaProcedimiento cuyos pasos se buscan.
     * @param texto fragmento de nombre o apellido; vacío o nulo trae todos
     * los pasos de ese ConsultaProcedimiento.
     * @param max maximo de resultados a devolver.
     * @return los pasos que coinciden, ordenados por fecha de inicio.
     */
    public List<ConsultaProcedimientoPaso> buscarPorConsultaProcedimiento(
            ConsultaProcedimiento cp, String texto, int max) {
        String patron = (texto == null || texto.isBlank()) ? null : "%" + texto.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT cpp FROM ConsultaProcedimientoPaso cpp "
                + "LEFT JOIN FETCH cpp.idPersonaRol pr "
                + "LEFT JOIN FETCH pr.idPersona "
                + "LEFT JOIN FETCH cpp.idProcedimientoPaso "
                + "WHERE cpp.idConsultaProcedimiento = :consultaProcedimiento "
                + "AND (:patron IS NULL OR LOWER(pr.idPersona.nombres) LIKE :patron OR LOWER(pr.idPersona.apellidos) LIKE :patron) "
                + "ORDER BY cpp.fechaInicio")
                .setParameter("consultaProcedimiento", cp)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Lista todos los pasos de un ConsultaProcedimiento, con el paso de
     * plantilla y la persona responsable precargados, para construir la
     * columna "Pasos" de ConsultaProcedimiento.xhtml (ej. "Recepcion
     * (CREADO) - Milena Mayorga (Atención al cliente)").
     *
     * Requiere la NamedQuery "ConsultaProcedimientoPaso.listarPorConsultaProcedimiento"
     * declarada en ConsultaProcedimientoPaso.java.
     *
     * @param idConsultaProcedimiento el ConsultaProcedimiento cuyos pasos
     * se listan.
     * @return los pasos de ese ConsultaProcedimiento, en orden de inicio.
     */
    public List<ConsultaProcedimientoPaso> listarPorConsultaProcedimiento(UUID idConsultaProcedimiento) {
        return em.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                        ConsultaProcedimientoPaso.class)
                .setParameter("idConsultaProcedimiento", idConsultaProcedimiento)
                .getResultList();
    }

}