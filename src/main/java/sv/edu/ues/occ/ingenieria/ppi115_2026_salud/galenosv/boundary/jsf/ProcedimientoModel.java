package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ProcedimientoModel extends ModelHandler<Procedimiento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoDAO prDAO;
    
    private GenericLazyDataModel<Procedimiento> lazyModel;

    public ProcedimientoModel() {
        super(Procedimiento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<Procedimiento> getLazyModel() {
        return lazyModel;
    }
    
    @Override
    public DAOInterface<Procedimiento> getDAO() {
        return prDAO;
    }

    @Override
    public Procedimiento instanciarRegistro() {
        return new Procedimiento();
    }

    @Override
    public Procedimiento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Procedimiento) prDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Procedimiento: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Procedimiento registro) {
        return registro != null ? registro.getIdProcedimiento() : null;
    }
}
