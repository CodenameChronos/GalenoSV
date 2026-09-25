package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

/**
 *
 * @author kardia
 */
@Stateless
@LocalBean
public class ExamenDAO extends DefaultDAO<Examen> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenDAO() {
        super(Examen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca exámenes activos cuyo nombre contenga el texto indicado. Se usa
     * como completeMethod de los p:autoComplete que seleccionan un Examen como
     * padre de otra entidad (por ejemplo, ExamenTipoExamen).
     *
     * @param texto fragmento de nombre escrito por el usuario.
     * @param max máximo de sugerencias a devolver.
     * @return los exámenes activos que coinciden, ordenados por nombre.
     */
    public List<Examen> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("Examen.findActiveByNombre", Examen.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

}
