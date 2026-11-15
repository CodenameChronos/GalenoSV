package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.UUID;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoModelTest {

    @Mock
    private ProcedimientoService procServiceMock;

    @Mock
    private RolService rolServiceMock;

    private ProcedimientoPasoModel procedimientoPasoModel;

    private UUID sampleId;
    private ProcedimientoPaso sampleProcedimientoPaso;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ProcedimientoPasoModel.class, "procedimientoPasoModel");
        sampleId = UUID.randomUUID();
        sampleProcedimientoPaso = new ProcedimientoPaso();
        sampleProcedimientoPaso.setIdProcedimientoPaso(sampleId);
        sampleProcedimientoPaso.setNombre("Toma de signos");
    }

    // ==========================================
    // PRUEBAS PARA completarProcedimiento
    // ==========================================

    @Test
    @DisplayName("completarProcedimiento - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimiento_Failures() {
        assertTrue(procedimientoPasoModel.completarProcedimiento(null).isEmpty());
        assertTrue(procedimientoPasoModel.completarProcedimiento("").isEmpty());
        assertTrue(procedimientoPasoModel.completarProcedimiento("   ").isEmpty());

        verify(procServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarRol
    // ==========================================

    @Test
    @DisplayName("completarRol - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarRol_Failures() {
        assertTrue(procedimientoPasoModel.completarRol(null).isEmpty());
        assertTrue(procedimientoPasoModel.completarRol("").isEmpty());
        assertTrue(procedimientoPasoModel.completarRol("   ").isEmpty());

        verify(rolServiceMock, never()).buscarPorNombre(any(), anyInt());
    }


    @Test
    @DisplayName("completarProcedimiento - Éxito (Devuelve los procedimientos encontrados)")
    void completarProcedimiento_Success() {
        List<Procedimiento> resultados = List.of(new Procedimiento());
        when(procServiceMock.buscarPorNombre("Consulta", 30)).thenReturn(resultados);

        assertSame(resultados, procedimientoPasoModel.completarProcedimiento("Consulta"));
        verify(procServiceMock).buscarPorNombre("Consulta", 30);
    }

    @Test
    @DisplayName("completarRol - Éxito (Devuelve los roles encontrados)")
    void completarRol_Success() {
        List<Rol> resultados = List.of(new Rol());
        when(rolServiceMock.buscarPorNombre("Médico", 30)).thenReturn(resultados);

        assertSame(resultados, procedimientoPasoModel.completarRol("Médico"));
        verify(rolServiceMock).buscarPorNombre("Médico", 30);
    }

}
