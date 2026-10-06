package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ProcedimientoPasoSecuenciaModel extends ModelHandler<ProcedimientoPasoSecuencia> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoSecuenciaService ppsService;
    
    @Inject
    private ProcedimientoPasoService ppDAO;
    
    
    private GenericLazyDataModel<ProcedimientoPasoSecuencia> lazyModel;

    public ProcedimientoPasoSecuenciaModel() {
        super(ProcedimientoPasoSecuencia.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<ProcedimientoPasoSecuencia> getLazyModel() {
        return lazyModel;
    }
    
    
    @Override
    public ParentServiceInterface<ProcedimientoPasoSecuencia> getDAO() {
        return ppsService;
    }

    @Override
    public ProcedimientoPasoSecuencia instanciarRegistro() {
        ProcedimientoPasoSecuencia pps = new ProcedimientoPasoSecuencia();
        pps.setIdProcedimientoPasoReferencia(UUID.randomUUID());
        return pps;
    }

    @Override
    public ProcedimientoPasoSecuencia getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ProcedimientoPasoSecuencia) ppsService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Procedimiento Paso Secuencia: " + id, ex);
            return null;
        }
    }
    

    @Override
    public Object getIdByRegistro(ProcedimientoPasoSecuencia registro) {
        return registro != null ? registro.getIdProcedimientoPasoSecuencia() : null;
    }
    
    public List<ProcedimientoPaso> completarProcedimientoPaso(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return ppDAO.buscarPorNombre(texto, 30);
    }
}
