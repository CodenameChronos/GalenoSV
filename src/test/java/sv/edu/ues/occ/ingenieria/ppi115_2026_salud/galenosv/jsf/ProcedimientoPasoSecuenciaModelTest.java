package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.validator.ValidatorException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoSecuenciaModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaModelTest {

    @Mock
    private ProcedimientoPasoSecuenciaService ppsServiceMock;

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
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(ppsServiceMock, procedimientoPasoSecuenciaModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        ProcedimientoPasoSecuencia primero = procedimientoPasoSecuenciaModel.instanciarRegistro();
        ProcedimientoPasoSecuencia segundo = procedimientoPasoSecuenciaModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdProcedimientoPasoSecuencia());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(ppsServiceMock.buscar(sampleId)).thenReturn(sampleProcedimientoPasoSecuencia);

        ProcedimientoPasoSecuencia resultado = procedimientoPasoSecuenciaModel.getRegistroById(sampleId.toString());

        assertSame(sampleProcedimientoPasoSecuencia, resultado);
        verify(ppsServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(ppsServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(procedimientoPasoSecuenciaModel.getRegistroById("no-es-un-uuid"));
        assertNull(procedimientoPasoSecuenciaModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(ppsServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, procedimientoPasoSecuenciaModel.getIdByRegistro(sampleProcedimientoPasoSecuencia));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(procedimientoPasoSecuenciaModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(ppsServiceMock.contar()).thenReturn(3);

        assertNotNull(procedimientoPasoSecuenciaModel.getLazyModel());
        assertEquals(3, procedimientoPasoSecuenciaModel.getLazyModel().count(Collections.emptyMap()));
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
    @DisplayName("completarProcedimientoPaso - Éxito (Retorna los registros que encuentra el servicio)")
    void completarProcedimientoPaso_Success() {
        List<ProcedimientoPaso> encontrados = List.of(new ProcedimientoPaso(UUID.randomUUID()));
        when(ppDAOMock.buscarPorNombre("toma", 30)).thenReturn(encontrados);

        assertSame(encontrados, procedimientoPasoSecuenciaModel.completarProcedimientoPaso("toma"));
    }

    @Test
    @DisplayName("completarProcedimientoPaso - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimientoPaso_Failures() {
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso(null).isEmpty());
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso("").isEmpty());
        assertTrue(procedimientoPasoSecuenciaModel.completarProcedimientoPaso("   ").isEmpty());

        verify(ppDAOMock, never()).buscarPorNombre(any(), anyInt());
    }
}
