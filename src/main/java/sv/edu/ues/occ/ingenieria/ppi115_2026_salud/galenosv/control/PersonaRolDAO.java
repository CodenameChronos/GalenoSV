package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class PersonaRolDAO extends DefaultDAO<PersonaRol> {

    /** Nombre del rol que identifica a un paciente (se compara sin distinguir mayúsculas). */
    public static final String ROL_PACIENTE = "Paciente";

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public PersonaRolDAO() {
        super(PersonaRol.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca personas cuyos nombres o apellidos contengan el texto indicado. Se
     * usa como completeMethod de los p:autoComplete que seleccionan una Persona
     * como padre de otra entidad
     *
     * @param texto fragmento de nombre escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return los exámenes activos que coinciden, ordenados por nombre.
     */
    public List<PersonaRol> buscarPorNombresApellidos(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("PersonaRol.findByNombresApellidos", PersonaRol.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

    public boolean existeRolEnClinica(UUID idPersona, UUID idRol, UUID idClinica, UUID idPersonaRolExcluir) {
        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(pr) FROM PersonaRol pr WHERE pr.idPersona.idPersona = :idPersona "
                + "AND pr.idRol.idRol = :idRol ");

        if (idClinica != null) {
            jpql.append("AND pr.idClinica.idClinica = :idClinica ");
        } else {
            jpql.append("AND pr.idClinica IS NULL ");
        }

        if (idPersonaRolExcluir != null) {
            jpql.append("AND pr.idPersonaRol <> :idExcluir");
        }

        var query = getEntityManager().createQuery(jpql.toString(), Long.class)
                .setParameter("idPersona", idPersona)
                .setParameter("idRol", idRol);

        if (idClinica != null) {
            query.setParameter("idClinica", idClinica);
        }
        if (idPersonaRolExcluir != null) {
            query.setParameter("idExcluir", idPersonaRolExcluir);
        }

        return query.getSingleResult() > 0;
    }

    /**
     * Lista al personal de una clínica: todas las asignaciones cuyo rol NO es
     * el indicado (normalmente el de paciente). Trae persona y rol cargados,
     * para elegir un responsable sin LazyInitializationException.
     *
     * @param clinica la clínica de la que se quiere el personal.
     * @param nombreRol nombre del rol a excluir (sin distinguir mayúsculas).
     * @return las asignaciones del personal; vacía si la clínica no tiene.
     */
    public List<PersonaRol> listarPersonalExcluyendoRol(Clinica clinica, String nombreRol) {
        return getEntityManager().createQuery(
                        "SELECT pr FROM PersonaRol pr "
                                + "JOIN FETCH pr.idPersona JOIN FETCH pr.idRol r "
                                + "WHERE pr.idClinica = :clinica AND LOWER(r.nombre) <> :rol",
                        PersonaRol.class)
                .setParameter("clinica", clinica)
                .setParameter("rol", nombreRol.trim().toLowerCase())
                .getResultList();
    }

    /**
     * Busca pacientes de una clínica por nombre o apellido; alimenta el
     * diálogo "Buscar paciente" de Consultas.
     *
     * @param clinica la clínica donde se busca.
     * @param texto fragmento del nombre completo escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return las asignaciones persona + rol paciente + clínica que coinciden.
     */
    public List<PersonaRol> buscarPacientes(Clinica clinica, String texto, int max) {
        return getEntityManager().createQuery(
                        "SELECT pr FROM PersonaRol pr "
                                + "JOIN FETCH pr.idPersona p JOIN FETCH pr.idRol r "
                                + "WHERE pr.idClinica = :clinica AND LOWER(r.nombre) = :rol "
                                + "AND LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :texto "
                                + "ORDER BY p.apellidos, p.nombres",
                        PersonaRol.class)
                .setParameter("clinica", clinica)
                .setParameter("rol", ROL_PACIENTE.toLowerCase())
                .setParameter("texto", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Trae, de una sola vez, los documentos de varias personas (las de una
     * página de la tabla o las sugerencias de un autocompletar), con el tipo de
     * documento ya cargado para mostrar "DUI: 00000000-3" sin consultas extra.
     *
     * @param personas las personas cuyos documentos se quieren.
     * @return los documentos, ordenados por tipo y valor (vacía si no se pasó ninguna persona).
     */
    public List<Documento> listarDocumentosDePersonas(Collection<Persona> personas) {
        if (personas == null || personas.isEmpty()) {
            return List.of();
        }
        return getEntityManager().createQuery(
                        "SELECT d FROM Documento d "
                                + "JOIN FETCH d.idTipoDocumento t JOIN FETCH d.idPersona "
                                + "WHERE d.idPersona IN :personas "
                                + "ORDER BY t.nombre, d.valor",
                        Documento.class)
                .setParameter("personas", personas)
                .getResultList();
    }
}