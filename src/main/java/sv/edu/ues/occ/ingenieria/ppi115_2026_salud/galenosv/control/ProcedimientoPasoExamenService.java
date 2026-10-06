package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoExamenService extends ParentService<ProcedimientoPasoExamen> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoExamenService() {
        super(ProcedimientoPasoExamen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Examen> listarExamenesDePaso(UUID idPaso) {
        return em.createNamedQuery("ProcedimientoPasoExamen.findExamenByProcedimientoPaso",
                        Examen.class)
                .setParameter("idPaso", idPaso)
                .getResultList();
    }


}
