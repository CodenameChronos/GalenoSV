package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import java.util.List;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class PersonaService extends ParentService<Persona> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public PersonaService() {
        super(Persona.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Persona> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Persona.findByNombreCompleto", Persona.class)
                .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

}
