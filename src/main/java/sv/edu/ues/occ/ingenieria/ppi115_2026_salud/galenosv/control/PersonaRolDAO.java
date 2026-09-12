package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class PersonaRolDAO extends DefaultDAO<PersonaRol>{
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public PersonaRolDAO() {
        super(PersonaRol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
}
