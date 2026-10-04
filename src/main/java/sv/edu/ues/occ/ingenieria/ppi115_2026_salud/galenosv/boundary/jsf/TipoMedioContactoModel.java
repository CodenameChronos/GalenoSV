package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

@Named()
@ViewScoped
public class TipoMedioContactoModel extends ModelHandler<TipoMedioContacto> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoMedioContactoDAO tmcDAO;
    
    private GenericLazyDataModel<TipoMedioContacto> lazyModel;
    
    public TipoMedioContactoModel() {
        super(TipoMedioContacto.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<TipoMedioContacto> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<TipoMedioContacto> getDAO() {
        return tmcDAO;
    }

    @Override
    public TipoMedioContacto instanciarRegistro() {
        return new TipoMedioContacto();
    }

    @Override
    public TipoMedioContacto getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (TipoMedioContacto) tmcDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Clinica: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(TipoMedioContacto registro) {
        return registro != null ? registro.getIdTipoMedioContacto() : null;
    }

    
}
