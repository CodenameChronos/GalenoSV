package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoSecuenciaService extends ParentService<ProcedimientoPasoSecuencia> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoSecuenciaService() {
        super(ProcedimientoPasoSecuencia.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ProcedimientoPasoSecuencia> listarPorProcedimiento(UUID idProcedimiento) {
        return em.createNamedQuery("ProcedimientoPasoSecuencia.findByProcedimiento",
                        ProcedimientoPasoSecuencia.class)
                .setParameter("idProcedimiento", idProcedimiento)
                .getResultList();
    }
    
}
