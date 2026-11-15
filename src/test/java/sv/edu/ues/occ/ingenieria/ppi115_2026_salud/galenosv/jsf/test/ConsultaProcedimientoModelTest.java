package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ConsultaProcedimientoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ConsultaModel;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoModelTest {

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
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, ConsultaProcedimientoModel.class, "consultaProcedimientoModel");
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

}
