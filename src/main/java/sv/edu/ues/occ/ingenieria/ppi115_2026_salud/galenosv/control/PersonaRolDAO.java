package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class PersonaRolDAO extends DefaultDAO<PersonaRol> {

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
}
