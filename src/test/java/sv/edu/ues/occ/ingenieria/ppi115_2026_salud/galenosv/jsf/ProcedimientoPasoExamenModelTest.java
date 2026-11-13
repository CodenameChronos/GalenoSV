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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoExamenModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenModelTest {

    @Mock
    private ProcedimientoPasoExamenService ppeServiceMock;

    @Mock
    private ExamenService exServiceMock;

    @Mock
    private ProcedimientoPasoService ppServiceMock;

    private ProcedimientoPasoExamenModel procedimientoPasoExamenModel;

    private UUID sampleId;
    private ProcedimientoPasoExamen sampleProcedimientoPasoExamen;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ProcedimientoPasoExamenModel.class, "procedimientoPasoExamenModel");
        sampleId = UUID.randomUUID();
        sampleProcedimientoPasoExamen = new ProcedimientoPasoExamen();
        sampleProcedimientoPasoExamen.setIdProcedimientoPasoExamen(sampleId);
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(ppeServiceMock, procedimientoPasoExamenModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        ProcedimientoPasoExamen primero = procedimientoPasoExamenModel.instanciarRegistro();
        ProcedimientoPasoExamen segundo = procedimientoPasoExamenModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdProcedimientoPasoExamen());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(ppeServiceMock.buscar(sampleId)).thenReturn(sampleProcedimientoPasoExamen);

        ProcedimientoPasoExamen resultado = procedimientoPasoExamenModel.getRegistroById(sampleId.toString());

        assertSame(sampleProcedimientoPasoExamen, resultado);
        verify(ppeServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(ppeServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(procedimientoPasoExamenModel.getRegistroById("no-es-un-uuid"));
        assertNull(procedimientoPasoExamenModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(ppeServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, procedimientoPasoExamenModel.getIdByRegistro(sampleProcedimientoPasoExamen));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(procedimientoPasoExamenModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(ppeServiceMock.contar()).thenReturn(3);

        assertNotNull(procedimientoPasoExamenModel.getLazyModel());
        assertEquals(3, procedimientoPasoExamenModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA completarExamen
    // ==========================================

    @Test
    @DisplayName("completarExamen - Éxito (Retorna los registros que encuentra el servicio)")
    void completarExamen_Success() {
        List<Examen> encontrados = List.of(new Examen(UUID.randomUUID()));
        when(exServiceMock.buscarPorNombre("hemo", 30)).thenReturn(encontrados);

        assertSame(encontrados, procedimientoPasoExamenModel.completarExamen("hemo"));
    }

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
    @DisplayName("completarProcedimientoPaso - Éxito (Retorna los registros que encuentra el servicio)")
    void completarProcedimientoPaso_Success() {
        List<ProcedimientoPaso> encontrados = List.of(new ProcedimientoPaso(UUID.randomUUID()));
        when(ppServiceMock.buscarPorNombre("toma", 30)).thenReturn(encontrados);

        assertSame(encontrados, procedimientoPasoExamenModel.completarProcedimientoPaso("toma"));
    }

    @Test
    @DisplayName("completarProcedimientoPaso - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarProcedimientoPaso_Failures() {
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso(null).isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso("").isEmpty());
        assertTrue(procedimientoPasoExamenModel.completarProcedimientoPaso("   ").isEmpty());

        verify(ppServiceMock, never()).buscarPorNombre(any(), anyInt());
    }
}
