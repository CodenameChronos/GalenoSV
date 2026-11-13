package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
// OJO: ConsultaModel declara el paquete "galenosv.jsf" aunque su archivo está en boundary/jsf.
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ConsultaModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ConsultaProcedimientoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoModelTest {

    // El Model inyecta DOS campos del mismo tipo (conspService y service).
    // Mockito los distingue por nombre, por eso los mocks se llaman igual que los campos.
    @Mock
    private ConsultaProcedimientoService conspService;

    @Mock
    private ConsultaProcedimientoService service;

    @Mock
    private ConsultaProcedimientoPasoService pasoService;

    @Mock
    private ConsultaModel consultaModel;

    @Mock
    private Sesion sesion;

    private ConsultaProcedimientoModel consultaProcedimientoModel;

    private FacesTestSupport faces;
    private UUID sampleId;
    private ConsultaProcedimiento sampleConsp;
    private Consulta sampleConsulta;
    private Clinica clinica;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ConsultaProcedimientoModel.class, "consultaProcedimientoModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        sampleConsp = new ConsultaProcedimiento(sampleId);
        sampleConsulta = new Consulta(UUID.randomUUID());
        clinica = new Clinica(UUID.randomUUID(), "Clínica Central");
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA getDAO / instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio de consulta-procedimiento)")
    void getDAO_Success() {
        assertSame(conspService, consultaProcedimientoModel.getDAO());
    }

    @Test
    @DisplayName("instanciarRegistro - Éxito (Registro nuevo con id y fecha de inicio)")
    void instanciarRegistro_Success() {
        ConsultaProcedimiento primero = consultaProcedimientoModel.instanciarRegistro();
        ConsultaProcedimiento segundo = consultaProcedimientoModel.instanciarRegistro();

        assertNotNull(primero.getIdConsultaProcedimiento());
        assertNotNull(primero.getFechaInicio());
        assertNotEquals(primero.getIdConsultaProcedimiento(), segundo.getIdConsultaProcedimiento());
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(conspService.buscar(sampleId)).thenReturn(sampleConsp);

        assertSame(sampleConsp, consultaProcedimientoModel.getRegistroById(sampleId.toString()));
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(conspService.buscar(idInexistente)).thenReturn(null);

        assertNull(consultaProcedimientoModel.getRegistroById("no-es-un-uuid"));
        assertNull(consultaProcedimientoModel.getRegistroById(idInexistente.toString()));
        verify(conspService, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, consultaProcedimientoModel.getIdByRegistro(sampleConsp));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(consultaProcedimientoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso disponible)")
    void getLazyModel_Success() {
        assertNotNull(consultaProcedimientoModel.getLazyModel());
    }

    // ==========================================
    // PRUEBAS PARA buscarRegistros / pasosDe
    // ==========================================

    @Test
    @DisplayName("buscarRegistros - Éxito (Procedimientos de la consulta actual con sus pasos)")
    void buscarRegistros_Success() {
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        when(consultaModel.getRegistroActual()).thenReturn(sampleConsulta);
        when(conspService.listarPorConsulta(sampleConsulta.getIdConsulta(), 0, 10)).thenReturn(List.of(sampleConsp));
        when(pasoService.contarPorConsultaProcedimiento(sampleId)).thenReturn(1);
        when(pasoService.listarPorConsultaProcedimiento(sampleId, 0, 1)).thenReturn(List.of(paso));

        List<ConsultaProcedimiento> resultado = consultaProcedimientoModel.buscarRegistros(0, 10);

        assertEquals(List.of(sampleConsp), resultado);
        assertEquals(List.of(paso), consultaProcedimientoModel.pasosDe(sampleConsp));
    }

    @Test
    @DisplayName("buscarRegistros - Fallos (Sin consulta actual o sin id retorna lista vacía sin consultar)")
    void buscarRegistros_Failures() {
        // Sin consulta actual
        when(consultaModel.getRegistroActual()).thenReturn(null);
        assertTrue(consultaProcedimientoModel.buscarRegistros(0, 10).isEmpty());

        // Consulta todavía sin id
        when(consultaModel.getRegistroActual()).thenReturn(new Consulta());
        assertTrue(consultaProcedimientoModel.buscarRegistros(0, 10).isEmpty());

        verify(conspService, never()).listarPorConsulta(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("pasosDe - Éxito (Pasos cargados del procedimiento de consulta)")
    void pasosDe_Success() {
        ConsultaProcedimientoPaso paso = new ConsultaProcedimientoPaso(UUID.randomUUID());
        when(consultaModel.getRegistroActual()).thenReturn(sampleConsulta);
        when(conspService.listarPorConsulta(sampleConsulta.getIdConsulta(), 0, 10)).thenReturn(List.of(sampleConsp));
        when(pasoService.contarPorConsultaProcedimiento(sampleId)).thenReturn(1);
        when(pasoService.listarPorConsultaProcedimiento(sampleId, 0, 1)).thenReturn(List.of(paso));
        consultaProcedimientoModel.buscarRegistros(0, 10);

        assertEquals(List.of(paso), consultaProcedimientoModel.pasosDe(sampleConsp));
    }

    @Test
    @DisplayName("pasosDe - Fallos (Procedimiento sin pasos cargados retorna lista vacía)")
    void pasosDe_Failures() {
        assertTrue(consultaProcedimientoModel.pasosDe(new ConsultaProcedimiento(UUID.randomUUID())).isEmpty());
    }

    // ==========================================
    // PRUEBAS PARA contar
    // ==========================================

    @Test
    @DisplayName("contar - Éxito (Total de procedimientos de la consulta actual)")
    void contar_Success() {
        when(consultaModel.getRegistroActual()).thenReturn(sampleConsulta);
        when(conspService.contarPorConsulta(sampleConsulta.getIdConsulta())).thenReturn(3L);

        assertEquals(3, consultaProcedimientoModel.contar());
    }

    @Test
    @DisplayName("contar - Fallos (Sin consulta actual o sin id retorna 0 sin consultar)")
    void contar_Failures() {
        when(consultaModel.getRegistroActual()).thenReturn(null);
        assertEquals(0, consultaProcedimientoModel.contar());

        when(consultaModel.getRegistroActual()).thenReturn(new Consulta());
        assertEquals(0, consultaProcedimientoModel.contar());

        verify(conspService, never()).contarPorConsulta(any());
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================

    @Test
    @DisplayName("guardarHandler - Éxito (CREAR crea con paso inicial; MODIFICAR actualiza)")
    void guardarHandler_Success() {
        ConsultaProcedimiento nuevo = new ConsultaProcedimiento();
        when(consultaModel.getRegistroActual()).thenReturn(sampleConsulta);
        when(sesion.getClinica()).thenReturn(clinica);
        consultaProcedimientoModel.setRegistroActual(nuevo);
        consultaProcedimientoModel.setEstado(ESTADO_CRUD.CREAR);

        consultaProcedimientoModel.guardarHandler(null);

        assertSame(sampleConsulta, nuevo.getIdConsulta());
        verify(service, times(1)).crearConPasoInicial(nuevo, clinica);
        assertEquals(ESTADO_CRUD.NINGUNO, consultaProcedimientoModel.getEstado());

        consultaProcedimientoModel.setRegistroActual(sampleConsp);
        consultaProcedimientoModel.setEstado(ESTADO_CRUD.MODIFICAR);

        consultaProcedimientoModel.guardarHandler(null);

        verify(conspService, times(1)).actualizar(sampleConsp);
        assertEquals(FacesMessage.SEVERITY_INFO, faces.ultimoMensaje().getSeverity());
    }

    @Test
    @DisplayName("guardarHandler - Fallos (Regla de negocio muestra su mensaje; otro error se relanza)")
    void guardarHandler_Failures() {
        ConsultaProcedimiento nuevo = new ConsultaProcedimiento();
        RuntimeException errorInesperado = new IllegalStateException("Database error");
        when(consultaModel.getRegistroActual()).thenReturn(sampleConsulta);
        when(sesion.getClinica()).thenReturn(clinica);
        doThrow(new ReglaNegocioException("Seleccione el procedimiento a aplicar."))
                .doThrow(errorInesperado)
                .when(service).crearConPasoInicial(any(), any());
        consultaProcedimientoModel.setRegistroActual(nuevo);
        consultaProcedimientoModel.setEstado(ESTADO_CRUD.CREAR);

        // Regla de negocio: se muestra el mensaje y se conserva el estado
        consultaProcedimientoModel.guardarHandler(null);

        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals("Seleccione el procedimiento a aplicar.", faces.ultimoMensaje().getSummary());
        assertEquals(ESTADO_CRUD.CREAR, consultaProcedimientoModel.getEstado());

        // Error inesperado: se relanza como IllegalStateException
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> consultaProcedimientoModel.guardarHandler(null));

        assertSame(errorInesperado, ex.getCause());
        assertEquals(ESTADO_CRUD.CREAR, consultaProcedimientoModel.getEstado());
    }

    // ==========================================
    // PRUEBAS PARA getNombreProcedimiento
    // ==========================================

    @Test
    @DisplayName("getNombreProcedimiento - Éxito (Nombre del procedimiento elegido)")
    void getNombreProcedimiento_Success() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        procedimiento.setNombre("Limpieza");
        sampleConsp.setIdProcedimiento(procedimiento);
        consultaProcedimientoModel.setRegistroActual(sampleConsp);

        assertEquals("Limpieza", consultaProcedimientoModel.getNombreProcedimiento());
    }

    @Test
    @DisplayName("getNombreProcedimiento - Fallos (Sin registro o sin procedimiento retorna cadena vacía)")
    void getNombreProcedimiento_Failures() {
        consultaProcedimientoModel.setRegistroActual(null);
        assertEquals("", consultaProcedimientoModel.getNombreProcedimiento());

        consultaProcedimientoModel.setRegistroActual(sampleConsp);
        assertEquals("", consultaProcedimientoModel.getNombreProcedimiento());
    }

    // ==========================================
    // PRUEBAS PARA isProcedimientoEditable
    // ==========================================

    @Test
    @DisplayName("isProcedimientoEditable - Éxito (Editable solo al crear)")
    void isProcedimientoEditable_Success() {
        consultaProcedimientoModel.setEstado(ESTADO_CRUD.CREAR);

        assertTrue(consultaProcedimientoModel.isProcedimientoEditable());
    }

    @Test
    @DisplayName("isProcedimientoEditable - Fallos (No editable al modificar ni sin estado)")
    void isProcedimientoEditable_Failures() {
        consultaProcedimientoModel.setEstado(ESTADO_CRUD.MODIFICAR);
        assertFalse(consultaProcedimientoModel.isProcedimientoEditable());

        consultaProcedimientoModel.setEstado(ESTADO_CRUD.NINGUNO);
        assertFalse(consultaProcedimientoModel.isProcedimientoEditable());
    }

    // ==========================================
    // PRUEBAS PARA abrirBuscarProcedimiento
    // ==========================================

    @Test
    @DisplayName("abrirBuscarProcedimiento - Éxito (Limpia el procedimiento de búsqueda)")
    void abrirBuscarProcedimiento_Success() {
        consultaProcedimientoModel.setProcedimientoBusqueda(new Procedimiento(UUID.randomUUID()));

        consultaProcedimientoModel.abrirBuscarProcedimiento();

        assertNull(consultaProcedimientoModel.getProcedimientoBusqueda());
    }

    // ==========================================
    // PRUEBAS PARA completarProcedimiento
    // ==========================================

    @Test
    @DisplayName("completarProcedimiento - Éxito (Retorna los procedimientos activos que coinciden)")
    void completarProcedimiento_Success() {
        List<Procedimiento> encontrados = List.of(new Procedimiento(UUID.randomUUID()));
        when(conspService.buscarProcedimientosActivos("lim", 10)).thenReturn(encontrados);

        assertSame(encontrados, consultaProcedimientoModel.completarProcedimiento("lim"));
    }

    @Test
    @DisplayName("completarProcedimiento - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimiento_Failures() {
        assertTrue(consultaProcedimientoModel.completarProcedimiento(null).isEmpty());
        assertTrue(consultaProcedimientoModel.completarProcedimiento("").isEmpty());
        assertTrue(consultaProcedimientoModel.completarProcedimiento("   ").isEmpty());

        verify(conspService, never()).buscarProcedimientosActivos(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA aceptarProcedimiento
    // ==========================================

    @Test
    @DisplayName("aceptarProcedimiento - Éxito (Asigna el procedimiento elegido al registro)")
    void aceptarProcedimiento_Success() {
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        consultaProcedimientoModel.setRegistroActual(sampleConsp);
        consultaProcedimientoModel.setProcedimientoBusqueda(procedimiento);

        consultaProcedimientoModel.aceptarProcedimiento();

        assertSame(procedimiento, sampleConsp.getIdProcedimiento());
    }

    @Test
    @DisplayName("aceptarProcedimiento - Fallos (Sin procedimiento elegido no modifica el registro)")
    void aceptarProcedimiento_Failures() {
        consultaProcedimientoModel.setRegistroActual(sampleConsp);
        consultaProcedimientoModel.setProcedimientoBusqueda(null);

        consultaProcedimientoModel.aceptarProcedimiento();

        assertNull(sampleConsp.getIdProcedimiento());
    }

    // ==========================================
    // PRUEBAS PARA getAhora
    // ==========================================

    @Test
    @DisplayName("getAhora - Éxito (Fecha y hora actuales)")
    void getAhora_Success() {
        Date antes = new Date();
        Date ahora = consultaProcedimientoModel.getAhora();
        Date despues = new Date();

        assertFalse(ahora.before(antes));
        assertFalse(ahora.after(despues));
    }
}
