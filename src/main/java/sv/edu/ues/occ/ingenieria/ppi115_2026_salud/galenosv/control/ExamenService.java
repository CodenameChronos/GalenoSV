package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class ExamenService extends ParentService<Examen> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenService() {
        super(Examen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Examen> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Examen.findActiveByNombre", Examen.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

}
