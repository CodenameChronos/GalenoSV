package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoExamenModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenModelTest {

    @Mock
    private ExamenService exServiceMock;

    @Mock
    private ProcedimientoPasoService ppServiceMock;

    private ProcedimientoPasoExamenModel procedimientoPasoExamenModel;

    private UUID sampleId;
    private ProcedimientoPasoExamen sampleProcedimientoPasoExamen;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, ProcedimientoPasoExamenModel.class, "procedimientoPasoExamenModel");
        sampleId = UUID.randomUUID();
        sampleProcedimientoPasoExamen = new ProcedimientoPasoExamen();
        sampleProcedimientoPasoExamen.setIdProcedimientoPasoExamen(sampleId);
    }

    // ==========================================
    // PRUEBAS PARA completarExamen
    // ==========================================

    @Test
    @DisplayName("completarExamen - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarExamen_Failures() {
        assertTrue(procedimientoPasoExamenModel.completarExamen(null).isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarExamen("").isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarExamen("   ").isEmpty());

        verify(exServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarProcedimientoPaso
    // ==========================================

    @Test
    @DisplayName("completarProcedimientoPaso - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimientoPaso_Failures() {
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso(null).isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso("").isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso("   ").isEmpty());

        verify(ppServiceMock, never()).buscarPorNombre(any(), anyInt());
    }


    @Test
    @DisplayName("completarExamen - Éxito (Devuelve los exámenes encontrados)")
    void completarExamen_Success() {
        List<Examen> resultados = List.of(new Examen());
        when(exServiceMock.buscarPorNombre("Hemograma", 30)).thenReturn(resultados);

        assertSame(resultados, procedimientoPasoExamenModel.completarExamen("Hemograma"));
        verify(exServiceMock).buscarPorNombre("Hemograma", 30);
    }

    @Test
    @DisplayName("completarProcedimientoPaso - Éxito (Devuelve los pasos encontrados)")
    void completarProcedimientoPaso_Success() {
        List<ProcedimientoPaso> resultados = List.of(new ProcedimientoPaso());
        when(ppServiceMock.buscarPorNombre("Toma", 30)).thenReturn(resultados);

        assertSame(resultados, procedimientoPasoExamenModel.completarProcedimientoPaso("Toma"));
        verify(ppServiceMock).buscarPorNombre("Toma", 30);
    }

}
