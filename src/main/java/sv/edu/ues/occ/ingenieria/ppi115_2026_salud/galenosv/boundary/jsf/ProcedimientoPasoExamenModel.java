package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;


@Named
@ViewScoped
public class ProcedimientoPasoExamenModel extends ModelHandler<ProcedimientoPasoExamen> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ProcedimientoPasoExamenService ppeService;

    @Inject
    private ExamenService exService;

    @Inject
    private ProcedimientoPasoService ppService;

    private GenericLazyDataModel<ProcedimientoPasoExamen> lazyModel;

    public ProcedimientoPasoExamenModel() {
        super(ProcedimientoPasoExamen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public ParentServiceInterface<ProcedimientoPasoExamen> getDAO() {
        return ppeService;
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
            return (ProcedimientoPasoExamen) ppeService.buscar(uuid);
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
        return exService.buscarPorNombre(texto, 30);
    }

    public List<ProcedimientoPaso> completarProcedimientoPaso(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return ppService.buscarPorNombre(texto, 30);
    }
}