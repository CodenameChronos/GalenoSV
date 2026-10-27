package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ExamenResultadoService extends ParentService<ExamenResultado> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenResultadoService() {
        super(ExamenResultado.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public List<ExamenResultado> findRangeByOrdenExamen (UUID idOrdenExamen, int first, int max) {
        return em.createNamedQuery(
                "ExamenResultado.findRangeByOrdenExamen",
                ExamenResultado.class)
                .setParameter("orden", idOrdenExamen)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
    
    public int contarByOrdenExamen (UUID idOrdenExamen) {
        return em.createNamedQuery(
                "ExamenResultado.countByOrdenExamen"
                , Long.class)
                .setParameter("orden", idOrdenExamen)
                .getSingleResult()
                .intValue();
    }
    
}
