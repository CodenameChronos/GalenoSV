package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ConsultaProcedimientoDAO extends DefaultDAO<ConsultaProcedimiento> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ConsultaProcedimientoDAO() {
        super(ConsultaProcedimiento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    /**
    * Busca consultas cuya persona asociada (via PersonaRol) coincide con el
    * texto en nombres o apellidos. Trae PersonaRol y Persona con JOIN FETCH
    * para que el autoComplete pueda mostrar el nombre completo sin disparar
    * LazyInitializationException.
    *
    * @param texto fragmento de nombre o apellido escrito por el usuario.
    * @param max maximo de sugerencias a devolver.
    * @return las consultas que coinciden, mas recientes primero.
    */
   public List<ConsultaProcedimiento> buscarPorNombreProcedimiento(String texto, int max) {
       return getEntityManager()
               .createNamedQuery("ConsultaProcedimiento.findByNombreProcedimiento", ConsultaProcedimiento.class)
               .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
               .setMaxResults(max)
               .getResultList();
   }
    
}
