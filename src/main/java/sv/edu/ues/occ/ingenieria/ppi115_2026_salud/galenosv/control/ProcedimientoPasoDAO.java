package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoDAO extends DefaultDAO<ProcedimientoPaso> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoDAO() {
        super(ProcedimientoPaso.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
}
