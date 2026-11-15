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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoSecuenciaModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaModelTest {

    @Mock
    private ProcedimientoPasoService ppDAOMock;

    private ProcedimientoPasoSecuenciaModel procedimientoPasoSecuenciaModel;

    private UUID sampleId;
    private ProcedimientoPasoSecuencia sampleProcedimientoPasoSecuencia;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ProcedimientoPasoSecuenciaModel.class, "procedimientoPasoSecuenciaModel");
        sampleId = UUID.randomUUID();
        sampleProcedimientoPasoSecuencia = new ProcedimientoPasoSecuencia();
        sampleProcedimientoPasoSecuencia.setIdProcedimientoPasoSecuencia(sampleId);
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro (referencia)
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (La nueva secuencia trae un UUID de paso de referencia)")
    void instanciarRegistro_ReferenciaGenerada_Success() {
        ProcedimientoPasoSecuencia primera = procedimientoPasoSecuenciaModel.instanciarRegistro();
        ProcedimientoPasoSecuencia segunda = procedimientoPasoSecuenciaModel.instanciarRegistro();

        assertNotNull(primera.getIdProcedimientoPasoReferencia());
        assertNotEquals(primera.getIdProcedimientoPasoReferencia(), segunda.getIdProcedimientoPasoReferencia());
    }

    // ==========================================
    // PRUEBAS PARA completarProcedimientoPaso
    // ==========================================

    @Test
    @DisplayName("completarProcedimientoPaso - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimientoPaso_Failures() {
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso(null).isEmpty());
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso("").isEmpty());
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso("   ").isEmpty());

        verify(ppDAOMock, never()).buscarPorNombre(any(), anyInt());
    }


    @Test
    @DisplayName("completarProcedimientoPaso - Éxito (Devuelve los pasos encontrados)")
    void completarProcedimientoPaso_Success() {
        List<ProcedimientoPaso> resultados = List.of(new ProcedimientoPaso());
        when(ppDAOMock.buscarPorNombre("Toma", 30)).thenReturn(resultados);

        assertSame(resultados, procedimientoPasoSecuenciaModel.completarProcedimientoPaso("Toma"));
        verify(ppDAOMock).buscarPorNombre("Toma", 30);
    }

}
