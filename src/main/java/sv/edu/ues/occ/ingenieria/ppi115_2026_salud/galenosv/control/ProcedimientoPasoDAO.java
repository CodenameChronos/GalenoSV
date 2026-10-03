package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ProcedimientoPasoDAO extends DefaultDAO<ProcedimientoPaso> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ProcedimientoPasoDAO() {
        super(ProcedimientoPaso.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ProcedimientoPaso> buscarPorNombre(String texto, int max) {
        return getEntityManager()
                .createNamedQuery("ProcedimientoPaso.findActiveByNombre", ProcedimientoPaso.class)
                .setParameter("nombre", "%" + texto.trim().toLowerCase() + "%")
                .setMaxResults(max)
                .getResultList();
    }

    /**
     * Lista todos los pasos de un procedimiento, con su Rol precargado para
     * mostrarlo en el árbol sin LazyInitializationException.
     *
     * @param idProcedimiento el procedimiento cuyos pasos se listan.
     * @return los pasos de ese procedimiento, sin ningún orden particular
     */
    public List<ProcedimientoPaso> listarPorProcedimiento(UUID idProcedimiento) {
        return em.createNamedQuery("ProcedimientoPaso.findByProcedimiento",
                        ProcedimientoPaso.class)
                .setParameter("idProcedimiento", idProcedimiento)
                .getResultList();
    }
}
