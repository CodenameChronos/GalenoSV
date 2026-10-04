package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

@Named
@ViewScoped
public class TipoDocumentoModel extends ModelHandler<TipoDocumento> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDocumentoDAO tdDAO;

    private GenericLazyDataModel<TipoDocumento> lazyModel;

    public TipoDocumentoModel() {
        super(TipoDocumento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<TipoDocumento> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<TipoDocumento> getDAO() {
        return tdDAO;
    }

    @Override
    public TipoDocumento instanciarRegistro() {
        return new TipoDocumento();
    }

    @Override
    public TipoDocumento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (TipoDocumento) tdDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "ID inválido recibido para TipoDocumento: " + id,
                    ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(TipoDocumento registro) {
        return registro != null ? registro.getIdTipoDocumento() : null;
    }
}
