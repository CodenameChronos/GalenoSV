package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;


@Named
@ViewScoped
public class ConsultaProcedimientoModel extends ModelHandler<ConsultaProcedimiento> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ConsultaProcedimientoService conspService;

    @Inject
    private ConsultaProcedimientoPasoService pasoService;

    @Inject
    private sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService service;

    @Inject
    private sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ConsultaModel consultaModel;

    @Inject
    private Sesion sesion;

    private GenericLazyDataModel<ConsultaProcedimiento> lazyModel;

    private Map<UUID, List<ConsultaProcedimientoPaso>> pasosPorConsp = new HashMap<>();

    /** Procedimiento elegido en el diálogo, antes de pulsar "Seleccionar". */
    private Procedimiento procedimientoBusqueda;

    public ConsultaProcedimientoModel() {
        super(ConsultaProcedimiento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public ParentServiceInterface<ConsultaProcedimiento> getDAO() {
        return conspService;
    }

    /** Procedimiento de consulta en blanco con su UUID y la fecha y hora actuales. */
    @Override
    public ConsultaProcedimiento instanciarRegistro() {
        ConsultaProcedimiento nuevo = new ConsultaProcedimiento();
        nuevo.setIdConsultaProcedimiento(UUID.randomUUID());
        nuevo.setFechaInicio(new Date());
        return nuevo;
    }

    @Override
    public ConsultaProcedimiento getRegistroById(String id) {
        try {
            return (ConsultaProcedimiento) conspService.buscar(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID inválido recibido para ConsultaProcedimiento: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimiento registro) {
        return registro != null ? registro.getIdConsultaProcedimiento() : null;
    }

    private Consulta consultaActual() {
        Consulta c = consultaModel.getRegistroActual();
        return (c != null && c.getIdConsulta() != null) ? c : null;
    }

    @Override
    public List<ConsultaProcedimiento> buscarRegistros(int first, int max) {
        pasosPorConsp = new HashMap<>();
        Consulta consulta = consultaActual();
        if (consulta == null) {
            return List.of();
        }
        List<ConsultaProcedimiento> pagina = conspService.listarPorConsulta(consulta, first, max);
        cargarPasos(pagina);
        return pagina;
    }

    @Override
    public int contar() {
        Consulta consulta = consultaActual();
        return consulta == null ? 0 : (int) conspService.contarPorConsulta(consulta);
    }

    private void cargarPasos(List<ConsultaProcedimiento> pagina) {
        for (ConsultaProcedimiento cp : pagina) {
            pasosPorConsp.put(cp.getIdConsultaProcedimiento(),
                    pasoService.listarPorConsultaProcedimiento(cp.getIdConsultaProcedimiento()));
        }
    }

    public List<ConsultaProcedimientoPaso> pasosDe(ConsultaProcedimiento consp) {
        return pasosPorConsp.getOrDefault(consp.getIdConsultaProcedimiento(), List.of());
    }

    @Override
    public void guardarHandler(ActionEvent ae) {
        if (estado != ESTADO_CRUD.CREAR) {
            super.guardarHandler(ae);
            return;
        }
        try {
            registroActual.setIdConsulta(consultaModel.getRegistroActual());
            service.crearConPasoInicial(registroActual, sesion.getClinica());
            estado = ESTADO_CRUD.NINGUNO;
        } catch (ReglaNegocioException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    public String getNombreProcedimiento() {
        if (registroActual == null || registroActual.getIdProcedimiento() == null) {
            return "";
        }
        return registroActual.getIdProcedimiento().getNombre();
    }

    public boolean isProcedimientoEditable() {
        return estado == ESTADO_CRUD.CREAR;
    }

    public void abrirBuscarProcedimiento() {
        procedimientoBusqueda = null;
    }

    public List<Procedimiento> completarProcedimiento(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return conspService.buscarProcedimientosActivos(texto, 10);
    }

    public void aceptarProcedimiento() {
        if (procedimientoBusqueda != null) {
            registroActual.setIdProcedimiento(procedimientoBusqueda);
        }
    }

    public GenericLazyDataModel<ConsultaProcedimiento> getLazyModel() {
        return lazyModel;
    }

    public Procedimiento getProcedimientoBusqueda() {
        return procedimientoBusqueda;
    }

    public void setProcedimientoBusqueda(Procedimiento procedimientoBusqueda) {
        this.procedimientoBusqueda = procedimientoBusqueda;
    }

    public Date getAhora() {
        return new Date();
    }
}