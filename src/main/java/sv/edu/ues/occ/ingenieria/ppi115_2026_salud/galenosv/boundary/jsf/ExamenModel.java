package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

@Named
@ViewScoped
public class ExamenModel extends ModelHandler<Examen> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExamenDAO examenDAO;

    private GenericLazyDataModel<Examen> lazyModel;

    public ExamenModel() {
        super(Examen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<Examen> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<Examen> getDAO() {
        return examenDAO;
    }

    @Override
    public Examen instanciarRegistro() {
        return new Examen();
    }

    @Override
    public Examen getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Examen) examenDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                Level.WARNING,
                "ID inválido recibido para Examen: " + id,
                ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Examen registro) {
        return registro != null ? registro.getIdExamen() : null;
    }
}