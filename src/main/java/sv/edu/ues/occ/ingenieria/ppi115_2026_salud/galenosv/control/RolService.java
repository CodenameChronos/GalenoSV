package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;
import java.util.List;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class RolService extends ParentService<Rol> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public RolService() {
        super(Rol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Rol> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Rol.findActiveByNombre", Rol.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

    public List<Rol> findAllActive() {
        return getEntityManager()
                .createNamedQuery("Rol.findByActivo", Rol.class)
                .setParameter("activo", Boolean.TRUE)
                .getResultList();
    }

}
