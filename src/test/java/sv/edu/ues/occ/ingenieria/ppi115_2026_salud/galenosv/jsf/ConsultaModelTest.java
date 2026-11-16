package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ConsultaModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;

@ExtendWith(MockitoExtension.class)
class ConsultaModelTest {

    @Mock
    private ConsultaService consultaServiceMock;

    @Mock
    private PersonaRolService personaRolServiceMock;

    @Mock
    private Sesion sesionMock;

    private ConsultaModel consultaModel;

    private FacesTestSupport faces;
    private UUID sampleId;
    private Consulta sampleConsulta;
    private Clinica clinica;
    private Persona persona;
    private PersonaRol pacienteRol;
    private Date desde;
    private Date hasta;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ConsultaModel.class, "consultaModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        sampleConsulta = new Consulta(sampleId);
        clinica = new Clinica(UUID.randomUUID(), "Clínica Central");
        persona = new Persona(UUID.randomUUID());
        persona.setNombres("Ana");
        persona.setApellidos("Pérez");
        pacienteRol = new PersonaRol(UUID.randomUUID());
        pacienteRol.setIdPersona(persona);
        pacienteRol.setIdClinica(clinica);
        desde = new Date(1_000_000L);
        hasta = new Date(2_000_000L);
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    private Documento documento(String tipo, String valor) {
        TipoDocumento tipoDocumento = new TipoDocumento(UUID.randomUUID());
        tipoDocumento.setNombre(tipo);
        Documento documento = new Documento(UUID.randomUUID());
        documento.setIdPersona(persona);
        documento.setIdTipoDocumento(tipoDocumento);
        documento.setValor(valor);
        return documento;
    }

    // ==========================================
    // PRUEBAS PARA iniciarFiltro
    // ==========================================

    @Test
    @DisplayName("iniciarFiltro - Éxito (Filtra desde hoy 00:00 hasta mañana 00:00)")
    void iniciarFiltro_Success() {
        consultaModel.iniciarFiltro();

        ZoneId zona = ZoneId.systemDefault();
        Date esperadoDesde = Date.from(LocalDate.now().atStartOfDay(zona).toInstant());
        Date esperadoHasta = Date.from(LocalDate.now().plusDays(1).atStartOfDay(zona).toInstant());
        assertEquals(esperadoDesde, consultaModel.getFechaDesde());
        assertEquals(esperadoHasta, consultaModel.getFechaHasta());
    }

    // ==========================================
    // PRUEBAS PARA getDAO / instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Consulta nueva con id y fecha de inicio)")
    void instanciarRegistro_Success() {
        Consulta primera = consultaModel.instanciarRegistro();
        Consulta segunda = consultaModel.instanciarRegistro();

        assertNotNull(primera.getIdConsulta());
        assertNotNull(primera.getFechaInicio());
        assertNotEquals(primera.getIdConsulta(), segunda.getIdConsulta());
    }

    // ==========================================
    // PRUEBAS PARA buscarRegistros
    // ==========================================

    @Test
    @DisplayName("buscarRegistros - Éxito (Consultas de la clínica en el rango y documentos de los pacientes)")
    void buscarRegistros_Success() {
        sampleConsulta.setIdPersonaRol(pacienteRol);
        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(hasta);
        when(sesionMock.getClinica()).thenReturn(clinica);
        when(consultaServiceMock.buscarPorClinicaYFechas(clinica, desde, hasta, 0, 10))
                .thenReturn(List.of(sampleConsulta));
        when(personaRolServiceMock.listarDocumentosDePersonas(anyCollection()))
                .thenReturn(List.of(documento("DUI", "01234567-8"), documento("NIT", "0614-010190-101-1")));

        List<Consulta> resultado = consultaModel.buscarRegistros(0, 10);

        assertEquals(List.of(sampleConsulta), resultado);
        assertEquals(", DUI: 01234567-8, NIT: 0614-010190-101-1", consultaModel.informacionDe(persona));
    }

    @Test
    @DisplayName("buscarRegistros - Fallos (Sin clínica o sin fechas retorna lista vacía sin consultar)")
    void buscarRegistros_Failures() {
        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(hasta);

        // Sin clínica en la sesión
        assertTrue(consultaModel.buscarRegistros(0, 10).isEmpty());

        // Con clínica pero sin alguna de las fechas
        when(sesionMock.getClinica()).thenReturn(clinica);
        consultaModel.setFechaDesde(null);
        assertTrue(consultaModel.buscarRegistros(0, 10).isEmpty());

        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(null);
        assertTrue(consultaModel.buscarRegistros(0, 10).isEmpty());

        verify(consultaServiceMock, never()).buscarPorClinicaYFechas(any(), any(), any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA contar
    // ==========================================

    @Test
    @DisplayName("contar - Éxito (Total de consultas de la clínica en el rango)")
    void contar_Success() {
        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(hasta);
        when(sesionMock.getClinica()).thenReturn(clinica);
        when(consultaServiceMock.contarPorClinicaYFechas(clinica, desde, hasta)).thenReturn(4L);

        assertEquals(4, consultaModel.contar());
    }

    @Test
    @DisplayName("contar - Fallos (Sin clínica o sin fechas retorna 0 sin consultar)")
    void contar_Failures() {
        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(hasta);
        assertEquals(0, consultaModel.contar());

        when(sesionMock.getClinica()).thenReturn(clinica);
        consultaModel.setFechaDesde(null);
        assertEquals(0, consultaModel.contar());

        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(null);
        assertEquals(0, consultaModel.contar());

        verify(consultaServiceMock, never()).contarPorClinicaYFechas(any(), any(), any());
    }

    // ==========================================
    // PRUEBAS PARA filtrar
    // ==========================================

    @Test
    @DisplayName("filtrar - Éxito (Rango válido, igual o incompleto no muestra mensajes)")
    void filtrar_Success() {
        consultaModel.setFechaDesde(desde);
        consultaModel.setFechaHasta(hasta);
        consultaModel.filtrar();

        consultaModel.setFechaHasta(desde);
        consultaModel.filtrar();

        consultaModel.setFechaDesde(null);
        consultaModel.filtrar();

        assertTrue(faces.mensajes().isEmpty());
    }

    @Test
    @DisplayName("filtrar - Fallos (Fecha final anterior a la inicial muestra error)")
    void filtrar_Failures() {
        consultaModel.setFechaDesde(hasta);
        consultaModel.setFechaHasta(desde);

        consultaModel.filtrar();

        assertEquals(1, faces.mensajes().size());
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("consulta.error.fechas"), faces.ultimoMensaje().getSummary());
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================

    @Test
    @DisplayName("guardarHandler - Fallos (CREAR sin paciente muestra error y no guarda)")
    void guardarHandler_Failures() {
        consultaModel.setRegistroActual(new Consulta(UUID.randomUUID()));
        consultaModel.setEstado(ESTADO_CRUD.CREAR);

        consultaModel.guardarHandler(null);

        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("consulta.error.paciente"), faces.ultimoMensaje().getSummary());
        assertEquals(ESTADO_CRUD.CREAR, consultaModel.getEstado());
        verify(consultaServiceMock, never()).crear(any());
    }

    // ==========================================
    // PRUEBAS PARA getNombrePaciente
    // ==========================================

    // ==========================================
    // PRUEBAS PARA completarPaciente
    // ==========================================

    @Test
    @DisplayName("completarPaciente - Éxito (Retorna los pacientes de la clínica que coinciden)")
    void completarPaciente_Success() {
        List<PersonaRol> encontrados = List.of(pacienteRol);
        when(sesionMock.getClinica()).thenReturn(clinica);
        when(personaRolServiceMock.buscarPacientes(clinica, "ana", 15)).thenReturn(encontrados);

        assertSame(encontrados, consultaModel.completarPaciente("ana"));
    }

    @Test
    @DisplayName("completarPaciente - Fallos (Sin clínica o texto nulo/en blanco retorna lista vacía)")
    void completarPaciente_Failures() {
        // Sin clínica
        assertTrue(consultaModel.completarPaciente("ana").isEmpty());

        // Con clínica pero texto inválido
        when(sesionMock.getClinica()).thenReturn(clinica);
        assertTrue(consultaModel.completarPaciente(null).isEmpty());
        assertTrue(consultaModel.completarPaciente("   ").isEmpty());

        verify(personaRolServiceMock, never()).buscarPacientes(any(), any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA aceptarPaciente
    // ==========================================

    @Test
    @DisplayName("aceptarPaciente - Éxito (Asigna el paciente elegido a la consulta)")
    void aceptarPaciente_Success() {
        consultaModel.setRegistroActual(sampleConsulta);
        consultaModel.setPacienteBusqueda(pacienteRol);

        consultaModel.aceptarPaciente();

        assertSame(pacienteRol, sampleConsulta.getIdPersonaRol());
    }

    @Test
    @DisplayName("aceptarPaciente - Fallos (Sin paciente elegido no modifica la consulta)")
    void aceptarPaciente_Failures() {
        consultaModel.setRegistroActual(sampleConsulta);
        consultaModel.setPacienteBusqueda(null);

        consultaModel.aceptarPaciente();

        assertNull(sampleConsulta.getIdPersonaRol());
    }

    // ==========================================
    // PRUEBAS PARA informacionDe
    // ==========================================

}
