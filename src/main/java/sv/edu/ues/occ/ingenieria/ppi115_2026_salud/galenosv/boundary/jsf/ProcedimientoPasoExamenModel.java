package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;


@Named
@ViewScoped
public class ProcedimientoPasoExamenModel extends ModelHandler<ProcedimientoPasoExamen> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProcedimientoPasoExamenDAO ppeDAO;

    @Inject
    private ExamenDAO exDAO;

    @Inject
    private ProcedimientoPasoDAO ppDAO;

    private GenericLazyDataModel<ProcedimientoPasoExamen> lazyModel;

    public ProcedimientoPasoExamenModel() {
        super(ProcedimientoPasoExamen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<ProcedimientoPasoExamen> getDAO() {
        return ppeDAO;
    }

    public GenericLazyDataModel<ProcedimientoPasoExamen> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ProcedimientoPasoExamen instanciarRegistro() {
        return new ProcedimientoPasoExamen();
    }

    @Override
    public ProcedimientoPasoExamen getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ProcedimientoPasoExamen) ppeDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID inválido recibido para ProcedimientoPasoExamen: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ProcedimientoPasoExamen registro) {
        return registro != null ? registro.getIdProcedimientoPasoExamen() : null;
    }

    public List<Examen> completarExamen(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return exDAO.buscarPorNombre(texto, 30);
    }

    public List<ProcedimientoPaso> completarProcedimientoPaso(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return ppDAO.buscarPorNombre(texto, 30);
    }
}