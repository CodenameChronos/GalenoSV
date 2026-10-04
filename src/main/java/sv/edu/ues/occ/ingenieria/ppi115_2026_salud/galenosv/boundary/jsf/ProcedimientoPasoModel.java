package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Named
@ViewScoped
public class ProcedimientoPasoModel extends ModelHandler<ProcedimientoPaso> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProcedimientoPasoDAO ppDAO;

    @Inject
    private ProcedimientoDAO prDAO;

    @Inject
    private RolDAO rolDAO;

    private GenericLazyDataModel<ProcedimientoPaso> lazyModel;

    public ProcedimientoPasoModel() {
        super(ProcedimientoPaso.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<ProcedimientoPaso> getLazyModel() {
        return lazyModel;
    }

    public List<Procedimiento> completarProcedimiento(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return prDAO.buscarPorNombre(texto, 30);
    }

    public List<Rol> completarRol(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return rolDAO.buscarPorNombre(texto, 30);
    }

    @Override
    public DAOInterface<ProcedimientoPaso> getDAO() {
        return ppDAO;
    }

    @Override
    public ProcedimientoPaso instanciarRegistro() {
        return new ProcedimientoPaso();
    }

    @Override
    public ProcedimientoPaso getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ProcedimientoPaso) ppDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "ID inválido recibido para ProcedimientoPaso: " + id,
                    ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ProcedimientoPaso registro) {
        return registro != null
                ? registro.getIdProcedimientoPaso()
                : null;
    }
}
