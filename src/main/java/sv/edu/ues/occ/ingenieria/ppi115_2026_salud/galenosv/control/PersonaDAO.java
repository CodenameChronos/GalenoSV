package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class PersonaDAO extends DefaultDAO<Persona> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public PersonaDAO() {
        super(Persona.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Persona> buscarPorNombre(String texto, int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
