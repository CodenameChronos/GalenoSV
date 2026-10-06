package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

@Stateless
@LocalBean
public class ExamenTipoExamenService extends ParentService<ExamenTipoExamen> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenTipoExamenService() {
        super(ExamenTipoExamen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ExamenTipoExamen> findByIdExamen(
            final UUID uuid,
            int first,
            int max) {

        if (uuid == null) {
            return List.of();
        }

        return getEntityManager()
                .createNamedQuery(
                        "ExamenTipoExamen.findByIdExamen",
                        ExamenTipoExamen.class)
                .setParameter("idExamen", uuid)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultList();
    }

    public int contarPorIdExamen(final UUID uuid) {

        if (uuid == null) {
            return 0;
        }

        return getEntityManager()
                .createQuery(
                        "SELECT COUNT(e) "
                        + "FROM ExamenTipoExamen e "
                        + "WHERE e.idExamen.idExamen = :idExamen",
                        Long.class)
                .setParameter("idExamen", uuid)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<ExamenTipoExamen> findRange(int first, int max) {

        if (first < 0 || max < 0) {
            throw new IllegalArgumentException(
                    "first debe ser >= 0 y max debe ser >= 0");
        }

        if (max == 0) {
            return List.of();
        }

        try {

            return getEntityManager()
                    .createNamedQuery(
                            "ExamenTipoExamen.findRangePadresHijos",
                            ExamenTipoExamen.class)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();

        } catch (Exception ex) {

            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE,
                    ex.getMessage(),
                    ex);

            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object buscar(Object uuid) {

        if (uuid == null) {
            throw new IllegalArgumentException(
                    "Se requiere un UUID válido para realizar la búsqueda");
        }

        try {

            List<ExamenTipoExamen> resultado =
                    getEntityManager()
                            .createNamedQuery(
                                    "ExamenTipoExamen.buscarPadresHijos",
                                    ExamenTipoExamen.class)
                            .setParameter("id", uuid)
                            .getResultList();

            return resultado.isEmpty()
                    ? null
                    : resultado.get(0);

        } catch (Exception ex) {

            Logger.getLogger(getClass().getName()).log(
                    Level.SEVERE,
                    ex.getMessage(),
                    ex);

            throw new IllegalStateException(ex);
        }
    }

    public void sincronizarTipos(Examen examen, List<TipoExamen> tiposDeseados) {

        if (examen == null || examen.getIdExamen() == null) {
            throw new IllegalArgumentException(
                    "El examen debe existir antes de sincronizar sus tipos.");
        }

        Set<UUID> idsDeseados = new HashSet<>();

        if (tiposDeseados != null) {
            for (TipoExamen tipo : tiposDeseados) {
                if (tipo != null && tipo.getIdTipoExamen() != null) {
                    idsDeseados.add(tipo.getIdTipoExamen());
                }
            }
        }

        List<ExamenTipoExamen> actuales = em.createQuery(
                        "SELECT e FROM ExamenTipoExamen e "
                                + "WHERE e.idExamen.idExamen = :idExamen",
                        ExamenTipoExamen.class)
                .setParameter("idExamen", examen.getIdExamen())
                .getResultList();

        Set<UUID> yaExisten = new HashSet<>();

        for (ExamenTipoExamen relacion : actuales) {

            UUID idTipo = relacion.getIdTipoExamen().getIdTipoExamen();

            if (idsDeseados.contains(idTipo)) {
                yaExisten.add(idTipo);
            } else {
                em.remove(relacion);
            }
        }

        for (UUID idTipo : idsDeseados) {

            if (!yaExisten.contains(idTipo)) {

                ExamenTipoExamen nuevaRelacion = new ExamenTipoExamen();

                nuevaRelacion.setIdExamen(
                        em.getReference(Examen.class, examen.getIdExamen()));

                nuevaRelacion.setIdTipoExamen(
                        em.getReference(TipoExamen.class, idTipo));

                em.persist(nuevaRelacion);
            }
        }
    }

    public void eliminarTipos(UUID idExamen) {

        if (idExamen == null) {
            return;
        }

        em.createQuery(
                        "DELETE FROM ExamenTipoExamen e "
                                + "WHERE e.idExamen.idExamen = :idExamen")
                .setParameter("idExamen", idExamen)
                .executeUpdate();
    }

}