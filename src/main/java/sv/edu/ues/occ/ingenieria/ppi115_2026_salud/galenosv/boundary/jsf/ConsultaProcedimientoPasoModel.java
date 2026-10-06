package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelHandler<ConsultaProcedimientoPaso> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaProcedimientoPasoService coppService;
    
    @Inject
    private ConsultaProcedimientoService conspService;
    
    @Inject
    private PersonaRolService prService;
    
    private GenericLazyDataModel<ConsultaProcedimientoPaso> lazyModel;

    private static final int MAX_REGISTROS = Integer.MAX_VALUE;

    private String filtroConsultaProcedimiento;
    private List<ConsultaProcedimiento> resultadosConsultaProcedimiento;

    private String filtroPersonaRol;
    private List<PersonaRol> resultadosPersonaRol;

    public ConsultaProcedimientoPasoModel() {
        super(ConsultaProcedimientoPaso.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public ParentServiceInterface<ConsultaProcedimientoPaso> getDAO() {
        return coppService;
    }

    public GenericLazyDataModel<ConsultaProcedimientoPaso> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ConsultaProcedimientoPaso instanciarRegistro() {
        return new ConsultaProcedimientoPaso();
    }

    @Override
    public ConsultaProcedimientoPaso getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ConsultaProcedimientoPaso) coppService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para ConsultaProcedimientoPaso: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimientoPaso registro) {
        return registro != null ? registro.getIdConsultaProcedimientoPaso() : null;
    }

    public void buscarConsultaProcedimiento() {
        String texto = filtroConsultaProcedimiento == null ? "" : filtroConsultaProcedimiento.trim();
        resultadosConsultaProcedimiento = conspService.buscarPorNombreProcedimiento(texto, MAX_REGISTROS);
    }


    public String etiquetaConsultaProcedimiento(ConsultaProcedimiento consp) {
        if (consp == null || consp.getIdProcedimiento() == null) {
            return "";
        }
        String nombreProcedimiento = consp.getIdProcedimiento().getNombre();
        String nombrePersona = "";
        if (consp.getIdConsulta() != null
                && consp.getIdConsulta().getIdPersonaRol() != null
                && consp.getIdConsulta().getIdPersonaRol().getIdPersona() != null) {
            nombrePersona = consp.getIdConsulta().getIdPersonaRol().getIdPersona().getNombreCompleto();
        }
        return nombreProcedimiento + " — " + nombrePersona;
    }

    public void buscarPersonaRol() {
        String texto = filtroPersonaRol == null ? "" : filtroPersonaRol.trim();
        resultadosPersonaRol = prService.buscarPorNombresApellidos(texto, MAX_REGISTROS);
    }

    public String etiquetaPersonaRol(PersonaRol pr) {
        if (pr == null || pr.getIdPersona() == null) {
            return "";
        }
        String rol = pr.getIdRol() != null ? pr.getIdRol().getNombre() : "";
        return pr.getIdPersona().getNombreCompleto() + " (" + rol + ")";
    }

    public String getFiltroConsultaProcedimiento() {
        return filtroConsultaProcedimiento;
    }

    public void setFiltroConsultaProcedimiento(String filtroConsultaProcedimiento) {
        this.filtroConsultaProcedimiento = filtroConsultaProcedimiento;
    }

    public List<ConsultaProcedimiento> getResultadosConsultaProcedimiento() {
        if (resultadosConsultaProcedimiento == null) {
            buscarConsultaProcedimiento();
        }
        return resultadosConsultaProcedimiento;
    }

    public String getFiltroPersonaRol() {
        return filtroPersonaRol;
    }

    public void setFiltroPersonaRol(String filtroPersonaRol) {
        this.filtroPersonaRol = filtroPersonaRol;
    }

    public List<PersonaRol> getResultadosPersonaRol() {
        if (resultadosPersonaRol == null) {
            buscarPersonaRol();
        }
        return resultadosPersonaRol;
    }

}