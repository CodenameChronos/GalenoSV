package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoSecuenciaDAO extends DefaultDAO<ProcedimientoPasoSecuencia> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoSecuenciaDAO() {
        super(ProcedimientoPasoSecuencia.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

     public List<ProcedimientoPasoSecuencia> buscarPorNombrePersona(String texto, int max) {
       return getEntityManager()
               .createNamedQuery("ProcedimientoPasoSecuencia.findByNombre", ProcedimientoPasoSecuencia.class)
               .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
               .setMaxResults(max)
               .getResultList();
   }
    
}
