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

    public List<ConsultaProcedimientoPaso> listarPorConsultaProcedimiento(UUID idConsultaProcedimiento, int first, int max) {
        return em.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                        ConsultaProcedimientoPaso.class)
                .setParameter("idConsultaProcedimiento", idConsultaProcedimiento)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
    
    public int contarPorConsultaProcedimiento(UUID idConsp) {
        return em.createNamedQuery("ConsultaProcedimientoPaso.contarPorConsultaProcedimiento", Long.class)
                .setParameter("idConsp", idConsp)
                .getSingleResult()
                .intValue();
    }

}