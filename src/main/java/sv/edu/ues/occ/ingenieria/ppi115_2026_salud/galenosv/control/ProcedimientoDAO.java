package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class ProcedimientoDAO extends DefaultDAO<Procedimiento> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoDAO() {
        super(Procedimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Procedimiento> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Procedimiento.findActiveByNombre", Procedimiento.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }
}
