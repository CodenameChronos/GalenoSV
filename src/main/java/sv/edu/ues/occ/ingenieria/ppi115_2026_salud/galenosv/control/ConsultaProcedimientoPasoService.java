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
        return "LEFT JOIN FETCH e.idConsultaProcedimiento consp "
                + "LEFT JOIN FETCH consp.idProcedimiento "
                + "LEFT JOIN FETCH consp.idConsulta c "
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
        return em.createNamedQuery("ConsultaProcedimientoPaso.buscarPorConsultaProcedimiento", ConsultaProcedimientoPaso.class)
                .setParameter("consultaProcedimiento", consp)
                .setParameter("patron", patron)
                .setMaxResults(max)
                .getResultList();
    }

    public List<ConsultaProcedimientoPaso> listarPorConsultaProcedimiento(UUID idConsultaProcedimiento, int first, int max) {
        return em.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                        ConsultaProcedimientoPaso.class)
                .setParameter("idConsultaProcedimiento", idConsultaProcedimiento)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
    
    public int contarPorConsultaProcedimiento(UUID idConsp) {
        return em.createNamedQuery("ConsultaProcedimintoPaso.contarPorConsultaProcedimiento", Long.class)
                .setParameter("idConsp", idConsp)
                .getSingleResult()
                .intValue();
    }

}