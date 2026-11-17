package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ConsultaService extends ParentService<Consulta> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ConsultaService() {
        super(Consulta.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
   
   public List<Consulta> buscarPorClinicaYFechas(Clinica clinica, Date desde, Date hasta,
                                                  int first, int max) {
        return getEntityManager().createNamedQuery(
                        "Consulta.buscarPorClinicaYFechas",
                        Consulta.class)
                .setParameter("clinica", clinica)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public long contarPorClinicaYFechas(Clinica clinica, Date desde, Date hasta) {
        return getEntityManager().createNamedQuery(
                        "Consulta.contarPorClinicaYFechas",
                        Long.class)
                .setParameter("clinica", clinica)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult();
    }
    
    public List<Consulta> findRangeConProcedimientos(PersonaRol personaRol, int first, int max) {
        return getEntityManager().createQuery(
                "SELECT DISTINCT c FROM Consulta c "
                + "LEFT JOIN FETCH c.consultaProcedimientoList cp "
                + "LEFT JOIN FETCH cp.idProcedimiento "
                + "WHERE c.idPersonaRol = :personaRol "
                + "ORDER BY c.fechaInicio DESC", Consulta.class)
                .setParameter("personaRol", personaRol)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }
    
}
