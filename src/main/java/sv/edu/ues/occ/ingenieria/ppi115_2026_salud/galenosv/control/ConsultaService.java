package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;

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

   public List<Consulta> buscarPorNombrePersona(String texto, int max) {
       return getEntityManager()
               .createNamedQuery("Consulta.findByNombre", Consulta.class)
               .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
               .setMaxResults(max)
               .getResultList();
   }
   
   public List<Consulta> buscarPorClinicaYFechas(Clinica clinica, Date desde, Date hasta,
                                                  int first, int max) {
        return getEntityManager().createQuery(
                        "SELECT c FROM Consulta c "
                                + "JOIN FETCH c.idPersonaRol pr JOIN FETCH pr.idPersona "
                                + "WHERE pr.idClinica = :clinica "
                                + "AND c.fechaInicio >= :desde AND c.fechaInicio <= :hasta "
                                + "ORDER BY c.fechaInicio DESC",
                        Consulta.class)
                .setParameter("clinica", clinica)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public long contarPorClinicaYFechas(Clinica clinica, Date desde, Date hasta) {
        return getEntityManager().createQuery(
                        "SELECT COUNT(c) FROM Consulta c "
                                + "WHERE c.idPersonaRol.idClinica = :clinica "
                                + "AND c.fechaInicio >= :desde AND c.fechaInicio <= :hasta",
                        Long.class)
                .setParameter("clinica", clinica)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult();
    }

    
}
