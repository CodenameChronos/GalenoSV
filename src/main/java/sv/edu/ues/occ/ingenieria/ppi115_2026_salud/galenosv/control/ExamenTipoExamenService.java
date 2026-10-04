package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

@Stateless
public class ExamenTipoExamenService {

    @PersistenceContext(unitName = "Galeno-PU")
    private EntityManager em;

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