package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class ExamenTipoExamenDAO extends DefaultDAO<ExamenTipoExamen> implements Serializable {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenTipoExamenDAO() {
        super(ExamenTipoExamen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ExamenTipoExamen> findByIdExamen(final UUID uuid, int first, int max) {
        try {
            TypedQuery<ExamenTipoExamen> tq = em.createNamedQuery("ExamenTipoExamen.findByIdTipoExamen", ExamenTipoExamen.class);
            return tq.getResultList();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<ExamenTipoExamen> buscarPorNombre(String nombre, int maxResultados) {
        return null;
    }

    @Override
    public List<ExamenTipoExamen> findRange(int first, int max) {
        if (first < 0 || max < 0) {
            throw new IllegalArgumentException("first debe ser >= 0 y max debe ser >= 0");
        }
        if (max == 0) {
            return List.of();
        }
        try {
            return getEntityManager()
                    .createNamedQuery("ExamenTipoExamen.findRangePadresHijos", ExamenTipoExamen.class)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object buscar(Object uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Se requiere un UUID válido para realizar la búsqueda");
        }
        try {
            List<ExamenTipoExamen> r = getEntityManager()
                    .createNamedQuery("ExamenTipoExamen.buscarPadresHijos", ExamenTipoExamen.class)
                    .setParameter("id", uuid)
                    .getResultList();
            return r.isEmpty() ? null : r.get(0);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

}
