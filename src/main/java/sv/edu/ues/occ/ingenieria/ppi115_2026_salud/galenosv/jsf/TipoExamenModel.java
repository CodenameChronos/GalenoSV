package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

@Named
@ViewScoped
public class TipoExamenModel extends ModelHandler<TipoExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoExamenDAO teDAO;
    
    private GenericLazyDataModel<TipoExamen> lazyModel;

    public TipoExamenModel() {
        super(TipoExamen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<TipoExamen> getLazyModel() {
        return lazyModel;
    }
    
    @Override
    public DAOInterface<TipoExamen> getDAO(){
        return teDAO;
    }
    
    @Override
    public TipoExamen instanciarRegistro(){
        return new TipoExamen();
    }

    @Override
    public TipoExamen getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (TipoExamen) teDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para TipoExamen: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(TipoExamen registro) {
        return registro != null ? registro.getIdTipoExamen() : null;
    }
    
}
