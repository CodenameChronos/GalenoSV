package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.GenericLazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ModelHandler;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

/**
 * Bean de la pestaña "Procedimiento" de la pantalla de Consultas: los
 * procedimientos aplicados a la consulta que está abierta en {@link ConsultaModel}.
 *
 * <p><b>Qué cambia respecto al CRUD genérico:</b></p>
 * <ul>
 *   <li>La tabla lista solo los procedimientos de la consulta actual, y cada fila
 *   trae sus pasos (con la persona asignada y su rol).</li>
 *   <li>Al <b>crear</b>, no se guarda directamente: se delega en
 *   {@link ConsultaProcedimientoService}, que además crea el paso inicial del
 *   procedimiento y le asigna una persona del personal de la clínica.</li>
 *   <li>Una vez guardado, el procedimiento elegido ya no se puede cambiar (es
 *   llave foránea); sí se pueden editar fechas y observaciones.</li>
 * </ul>
 */
@Named
@ViewScoped
public class ConsultaProcedimientoModel extends ModelHandler<ConsultaProcedimiento> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ConsultaProcedimientoDAO cpDAO;

    @Inject
    private ConsultaProcedimientoPasoDAO pasoDAO;

    @Inject
    private ConsultaProcedimientoService service;

    @Inject
    private ConsultaModel consultaModel;

    @Inject
    private Sesion sesion;

    private GenericLazyDataModel<ConsultaProcedimiento> lazyModel;

    /**
     * Pasos de la página que se está mostrando, agrupados por id del
     * procedimiento de consulta y ordenados por fecha de inicio. Se llena al
     * cargar cada página de la tabla.
     */
    private Map<UUID, List<ConsultaProcedimientoPaso>> pasosPorCp = new HashMap<>();

    /** Procedimiento elegido en el diálogo, antes de pulsar "Seleccionar". */
    private Procedimiento procedimientoBusqueda;

    public ConsultaProcedimientoModel() {
        super(ConsultaProcedimiento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    // ------------------------------------------------------------------
    // Contrato de ModelHandler
    // ------------------------------------------------------------------

    @Override
    public DAOInterface<ConsultaProcedimiento> getDAO() {
        return cpDAO;
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
            return (ConsultaProcedimiento) cpDAO.buscar(UUID.fromString(id));
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

    // ------------------------------------------------------------------
    // Listado: solo los procedimientos de la consulta actual
    // ------------------------------------------------------------------

    /**
     * @return la consulta abierta en ConsultaModel, o {@code null} si todavía no
     * está guardada (sin id no hay procedimientos que listar).
     */
    private Consulta consultaActual() {
        Consulta c = consultaModel.getRegistroActual();
        return (c != null && c.getIdConsulta() != null) ? c : null;
    }

    @Override
    public List<ConsultaProcedimiento> buscarRegistros(int first, int max) {
        pasosPorCp = new HashMap<>();
        Consulta consulta = consultaActual();
        if (consulta == null) {
            return List.of();
        }
        List<ConsultaProcedimiento> pagina = cpDAO.listarPorConsulta(consulta, first, max);
        cargarPasos(pagina);
        return pagina;
    }

    @Override
    public int contar() {
        Consulta consulta = consultaActual();
        return consulta == null ? 0 : (int) cpDAO.contarPorConsulta(consulta);
    }

    /**
     * Carga los pasos de cada procedimiento de la página, ya ordenados por
     * fecha de inicio. Los pasos se van creando uno tras otro a medida que
     * avanza el procedimiento, así que ese orden es el de la cadena.
     */
    private void cargarPasos(List<ConsultaProcedimiento> pagina) {
        for (ConsultaProcedimiento cp : pagina) {
            pasosPorCp.put(cp.getIdConsultaProcedimiento(),
                    pasoDAO.listarPorConsultaProcedimiento(cp.getIdConsultaProcedimiento()));
        }
    }

    /**
     * Pasos de una fila de la tabla (columna "Pasos").
     *
     * @param cp el procedimiento de consulta de la fila.
     * @return sus pasos en orden; vacía si no tiene.
     */
    public List<ConsultaProcedimientoPaso> pasosDe(ConsultaProcedimiento cp) {
        return pasosPorCp.getOrDefault(cp.getIdConsultaProcedimiento(), List.of());
    }

    // ------------------------------------------------------------------
    // Guardar
    // ------------------------------------------------------------------

    /**
     * Al crear delega en el servicio (procedimiento + paso inicial con un
     * responsable elegido al azar, todo en una transacción). Si falla una regla de negocio se muestra el
     * mensaje y el formulario se queda abierto. Al modificar usa el flujo normal.
     */
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

    // ------------------------------------------------------------------
    // Procedimiento (diálogo de selección)
    // ------------------------------------------------------------------

    /** Nombre del procedimiento elegido ("Tipo") para mostrarlo en el formulario. */
    public String getNombreProcedimiento() {
        if (registroActual == null || registroActual.getIdProcedimiento() == null) {
            return "";
        }
        return registroActual.getIdProcedimiento().getNombre();
    }

    /** El procedimiento solo se elige al crear: una vez guardado es llave foránea y no cambia. */
    public boolean isProcedimientoEditable() {
        return estado == ESTADO_CRUD.CREAR;
    }

    /** Prepara el diálogo vacío antes de abrirlo. */
    public void abrirBuscarProcedimiento() {
        procedimientoBusqueda = null;
    }

    /**
     * Sugerencias del autocompletar: procedimientos activos del catálogo.
     *
     * @param texto lo que el usuario lleva escrito.
     * @return hasta 10 procedimientos activos que coinciden; vacía sin texto.
     */
    public List<Procedimiento> completarProcedimiento(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return cpDAO.buscarProcedimientosActivos(texto, 10);
    }

    /** Botón "Seleccionar" del diálogo: asigna el procedimiento al registro en edición. */
    public void aceptarProcedimiento() {
        if (procedimientoBusqueda != null) {
            registroActual.setIdProcedimiento(procedimientoBusqueda);
        }
    }

    // ------------------------------------------------------------------
    // Getters / setters para la vista
    // ------------------------------------------------------------------

    public GenericLazyDataModel<ConsultaProcedimiento> getLazyModel() {
        return lazyModel;
    }

    public Procedimiento getProcedimientoBusqueda() {
        return procedimientoBusqueda;
    }

    public void setProcedimientoBusqueda(Procedimiento procedimientoBusqueda) {
        this.procedimientoBusqueda = procedimientoBusqueda;
    }

    /** Fecha y hora actuales: tope de los calendarios (las fechas no pueden ser futuras). */
    public Date getAhora() {
        return new Date();
    }
}