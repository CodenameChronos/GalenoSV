package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

/**
 * Consultas de búsqueda que alimentan los tres autocompletar de la pantalla de
 * inicio de sesión (clínica, rol y persona).
 *
 * <p>Son solo lecturas, por eso no extiende de DefaultDAO. Si más adelante
 * prefieren seguir la convención de un DAO por entidad, cada método se puede
 * mover tal cual a ClinicaDAO, RolDAO y PersonaRolDAO.</p>
 */
@Stateless
@LocalBean
public class SesionDAO {

    @PersistenceContext(unitName = "Galeno-PU")
    private EntityManager em;

    /**
     * Busca clínicas cuyo nombre contenga el texto (sin distinguir mayúsculas).
     *
     * @param texto fragmento de nombre escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return las clínicas que coinciden, ordenadas por nombre.
     */
    public List<Clinica> buscarClinicas(String texto, int max) {
        return em.createQuery(
                        "SELECT c FROM Clinica c "
                                + "WHERE LOWER(c.nombre) LIKE :texto "
                                + "ORDER BY c.nombre",
                        Clinica.class)
                .setParameter("texto", patron(texto))
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Busca roles cuyo nombre contenga el texto (sin distinguir mayúsculas).
     * Devuelve cualquier rol existente: que alguien lo tenga en la clínica
     * elegida se comprueba después, al buscar la persona.
     *
     * @param texto fragmento de nombre escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return los roles que coinciden, ordenados por nombre.
     */
    public List<Rol> buscarRoles(String texto, int max) {
        return em.createQuery(
                        "SELECT r FROM Rol r "
                                + "WHERE LOWER(r.nombre) LIKE :texto "
                                + "ORDER BY r.nombre",
                        Rol.class)
                .setParameter("texto", patron(texto))
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Busca las asignaciones (persona + rol + clínica) de las personas que
     * tienen ese rol en esa clínica y cuyo nombre completo contiene el texto.
     * Se devuelve el PersonaRol y no solo la Persona porque es justo lo que
     * necesita {@code Sesion.establecerSesion}, y porque una misma persona
     * puede tener varios roles o trabajar en varias clínicas.
     *
     * @param clinica la clínica elegida en el primer campo.
     * @param rol el rol elegido en el segundo campo.
     * @param texto fragmento del nombre y apellido escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return las asignaciones que coinciden (vacía si nadie tiene ese rol allí).
     */
    public List<PersonaRol> buscarPersonaRol(Clinica clinica, Rol rol, String texto, int max) {
        return em.createQuery(
                        "SELECT pr FROM PersonaRol pr "
                                + "JOIN FETCH pr.idPersona p "
                                + "JOIN FETCH pr.idClinica "
                                + "JOIN FETCH pr.idRol "
                                + "WHERE pr.idClinica = :clinica AND pr.idRol = :rol "
                                + "AND LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :texto "
                                + "ORDER BY p.apellidos, p.nombres",
                        PersonaRol.class)
                .setParameter("clinica", clinica)
                .setParameter("rol", rol)
                .setParameter("texto", patron(texto))
                .setMaxResults(max)
                .getResultList();
    }

    /** Convierte lo escrito en un patrón LIKE "contiene", en minúsculas. */
    private String patron(String texto) {
        return "%" + texto.trim().toLowerCase() + "%";
    }

}