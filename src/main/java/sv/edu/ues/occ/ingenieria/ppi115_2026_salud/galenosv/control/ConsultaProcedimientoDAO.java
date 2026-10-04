package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

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

    /**
     * Lista una página de los procedimientos aplicados a una consulta, con el
     * Procedimiento precargado para mostrar su nombre ("Tipo") en la tabla.
     * Los pasos NO se traen aquí: paginar junto a una colección con JOIN FETCH
     * recorta filas y no páginas; se piden aparte con
     * ConsultaProcedimientoPasoDAO.listarPorConsultaProcedimiento.
     *
     * @param consulta la consulta dueña de los procedimientos.
     * @param first posición del primer registro de la página.
     * @param max tamaño de la página.
     * @return los procedimientos de la consulta, los más recientes primero.
     */
    public List<ConsultaProcedimiento> listarPorConsulta(Consulta consulta, int first, int max) {
        return getEntityManager().createQuery(
                        "SELECT cp FROM ConsultaProcedimiento cp "
                                + "JOIN FETCH cp.idProcedimiento "
                                + "WHERE cp.idConsulta = :consulta "
                                + "ORDER BY cp.fechaInicio DESC",
                        ConsultaProcedimiento.class)
                .setParameter("consulta", consulta)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Cuenta los procedimientos aplicados a una consulta.
     *
     * @param consulta la consulta dueña de los procedimientos.
     * @return la cantidad de procedimientos.
     */
    public long contarPorConsulta(Consulta consulta) {
        return getEntityManager().createQuery(
                        "SELECT COUNT(cp) FROM ConsultaProcedimiento cp WHERE cp.idConsulta = :consulta",
                        Long.class)
                .setParameter("consulta", consulta)
                .getSingleResult();
    }

    /**
     * Busca procedimientos ACTIVOS del catálogo cuyo nombre contenga el texto;
     * alimenta el selector "Seleccionar Procedimiento".
     *
     * @param texto fragmento de nombre escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return los procedimientos activos que coinciden, ordenados por nombre.
     */
    public List<Procedimiento> buscarProcedimientosActivos(String texto, int max) {
        return getEntityManager().createQuery(
                        "SELECT p FROM Procedimiento p "
                                + "WHERE p.activo = true AND LOWER(p.nombre) LIKE :texto "
                                + "ORDER BY p.nombre",
                        Procedimiento.class)
                .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }
}