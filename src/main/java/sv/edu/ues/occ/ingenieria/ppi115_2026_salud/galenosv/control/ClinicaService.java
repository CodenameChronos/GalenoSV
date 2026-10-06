package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import java.util.List;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class ClinicaService extends ParentService<Clinica> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ClinicaService() {
        super(Clinica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Clinica> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Clinica.findActiveByNombre", Clinica.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }
}
