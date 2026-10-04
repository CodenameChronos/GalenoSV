package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.GenericLazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ModelHandler;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

@Named
@ViewScoped
public class ConsultaModel extends ModelHandler<Consulta> {

    private static final long serialVersionUID = 1L;

    @Inject
    private ConsultaDAO consultaDAO;

    @Inject
    private PersonaRolDAO personaRolDAO;

    @Inject
    private Sesion sesion;

    private GenericLazyDataModel<Consulta> lazyModel;

    private Date fechaDesde;
    private Date fechaHasta;
    private PersonaRol pacienteBusqueda;
    private int tabIndex = 0;

    private Map<UUID, String> documentosPorPersona = new HashMap<>();

    public ConsultaModel() {
        super(Consulta.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @PostConstruct
    public void iniciarFiltro() {
        LocalDate hoy = LocalDate.now();
        ZoneId zona = ZoneId.systemDefault();
        fechaDesde = Date.from(hoy.atStartOfDay(zona).toInstant());
        fechaHasta = Date.from(hoy.plusDays(1).atStartOfDay(zona).toInstant());
    }

    @Override
    public DAOInterface<Consulta> getDAO() {
        return consultaDAO;
    }

    @Override
    public Consulta instanciarRegistro() {
        Consulta nueva = new Consulta();
        nueva.setIdConsulta(UUID.randomUUID());
        nueva.setFechaInicio(new Date());
        return nueva;
    }

    @Override
    public Consulta getRegistroById(String id) {
        try {
            return (Consulta) consultaDAO.buscar(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID inválido recibido para Consulta: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Consulta registro) {
        return registro != null ? registro.getIdConsulta() : null;
    }

    private boolean filtroCompleto() {
        return sesion.getClinica() != null && fechaDesde != null && fechaHasta != null;
    }

    @Override
    public List<Consulta> buscarRegistros(int first, int max) {
        if (!filtroCompleto()) {
            return List.of();
        }
        List<Consulta> pagina
                = consultaDAO.buscarPorClinicaYFechas(sesion.getClinica(), fechaDesde, fechaHasta, first, max);
        cargarDocumentos(pagina.stream().map(Consulta::getIdPersonaRol).toList());
        return pagina;
    }

    @Override
    public int contar() {
        if (!filtroCompleto()) {
            return 0;
        }
        return (int) consultaDAO.contarPorClinicaYFechas(sesion.getClinica(), fechaDesde, fechaHasta);
    }

    public void filtrar() {
        if (fechaDesde != null && fechaHasta != null && fechaHasta.before(fechaDesde)) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "consulta.error.fechas");
        }
    }

    @Override
    public void guardarHandler(ActionEvent ae) {
        if (estado == ESTADO_CRUD.CREAR && registroActual.getIdPersonaRol() == null) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "consulta.error.paciente");
            return;
        }
        super.guardarHandler(ae);
    }

    public String getNombrePaciente() {
        if (registroActual == null || registroActual.getIdPersonaRol() == null) {
            return "";
        }
        return registroActual.getIdPersonaRol().getIdPersona().getNombres() + " "
                + registroActual.getIdPersonaRol().getIdPersona().getApellidos();
    }

    public boolean isPacienteEditable() {
        return estado == ESTADO_CRUD.CREAR;
    }

    public void abrirBuscarPaciente() {
        pacienteBusqueda = null;
    }

    public List<PersonaRol> completarPaciente(String texto) {
        if (sesion.getClinica() == null || texto == null || texto.isBlank()) {
            return List.of();
        }
        List<PersonaRol> pacientes = personaRolDAO.buscarPacientes(sesion.getClinica(), texto, 15);
        cargarDocumentos(pacientes);
        return pacientes;
    }

    public void aceptarPaciente() {
        if (pacienteBusqueda != null) {
            registroActual.setIdPersonaRol(pacienteBusqueda);
        }
    }

    public GenericLazyDataModel<Consulta> getLazyModel() {
        return lazyModel;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public PersonaRol getPacienteBusqueda() {
        return pacienteBusqueda;
    }

    public void setPacienteBusqueda(PersonaRol pacienteBusqueda) {
        this.pacienteBusqueda = pacienteBusqueda;
    }

    private void cargarDocumentos(Collection<PersonaRol> asignaciones) {
        Set<Persona> personas = new HashSet<>();
        for (PersonaRol asignacion : asignaciones) {
            personas.add(asignacion.getIdPersona());
        }
        Map<UUID, String> nuevos = new HashMap<>();
        for (Persona persona : personas) {
            nuevos.put(persona.getIdPersona(), "");
        }
        for (Documento d : personaRolDAO.listarDocumentosDePersonas(personas)) {
            String documento = d.getIdTipoDocumento().getNombre() + ": " + d.getValor();
            nuevos.merge(d.getIdPersona().getIdPersona(), documento, (previo, actual) -> previo + ", " + actual);
        }
        documentosPorPersona.putAll(nuevos);
    }

    public String informacionDe(Persona persona) {
        return persona == null ? "" : documentosPorPersona.getOrDefault(persona.getIdPersona(), "");
    }

    public TimeZone getZonaHoraria() {
        return TimeZone.getDefault();
    }

    public Date getAhora() {
        return new Date();
    }

    private void agregarMensaje(FacesMessage.Severity severidad, String clave) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String texto = ctx.getApplication().getResourceBundle(ctx, "msg").getString(clave);
        ctx.addMessage(null, new FacesMessage(severidad, texto, null));
    }

    public void onTabChange(org.primefaces.event.TabChangeEvent event) {

    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public boolean isMostrarBotonesConsulta() {
        return this.tabIndex == 0;
    }
}
