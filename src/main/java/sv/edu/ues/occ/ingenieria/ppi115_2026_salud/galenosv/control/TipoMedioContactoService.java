package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class TipoMedioContactoService extends ParentService<TipoMedioContacto> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;
    
    public TipoMedioContactoService() {
        super(TipoMedioContacto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public List<TipoMedioContacto> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("TipoMedioContacto.findActiveByNombre", TipoMedioContacto.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }
}
