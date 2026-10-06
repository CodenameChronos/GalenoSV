package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
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
    private ProcedimientoService procService;
    
    private GenericLazyDataModel<Procedimiento> lazyModel;

    public ProcedimientoModel() {
        super(Procedimiento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<Procedimiento> getLazyModel() {
        return lazyModel;
    }
    
    @Override
    public ParentServiceInterface<Procedimiento> getDAO() {
        return procService;
    }

    @Override
    public Procedimiento instanciarRegistro() {
        return new Procedimiento();
    }

    @Override
    public Procedimiento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Procedimiento) procService.buscar(uuid);
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
