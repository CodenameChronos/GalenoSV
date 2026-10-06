package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;

@Stateless
public class ConsultaProcedimientoPasoService extends ParentService<ConsultaProcedimientoPaso> {

    @PersistenceContext
    private EntityManager em;

    public ConsultaProcedimientoPasoService() {
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

    public List<ConsultaProcedimientoPaso> buscarPorConsultaProcedimiento(
            ConsultaProcedimiento consp, String texto, int max) {
        String patron = (texto == null || texto.isBlank()) ? null : "%" + texto.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT cpp FROM ConsultaProcedimientoPaso cpp "
                + "LEFT JOIN FETCH cpp.idPersonaRol pr "
                + "LEFT JOIN FETCH pr.idPersona "
                + "LEFT JOIN FETCH cpp.idProcedimientoPaso "
                + "WHERE cpp.idConsultaProcedimiento = :consultaProcedimiento "
                + "AND (:patron IS NULL OR LOWER(pr.idPersona.nombres) LIKE :patron OR LOWER(pr.idPersona.apellidos) LIKE :patron) "
                + "ORDER BY cpp.fechaInicio")
                .setParameter("consultaProcedimiento", consp)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }

    public List<ConsultaProcedimientoPaso> listarPorConsultaProcedimiento(UUID idConsultaProcedimiento) {
        return em.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                        ConsultaProcedimientoPaso.class)
                .setParameter("idConsultaProcedimiento", idConsultaProcedimiento)
                .getResultList();
    }

}