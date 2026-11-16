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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoPasoModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoModelTest {

    @Mock
    private ProcedimientoPasoService ppServiceMock;

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
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(ppServiceMock, procedimientoPasoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        ProcedimientoPaso primero = procedimientoPasoModel.instanciarRegistro();
        ProcedimientoPaso segundo = procedimientoPasoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdProcedimientoPaso());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(ppServiceMock.buscar(sampleId)).thenReturn(sampleProcedimientoPaso);

        ProcedimientoPaso resultado = procedimientoPasoModel.getRegistroById(sampleId.toString());

        assertSame(sampleProcedimientoPaso, resultado);
        verify(ppServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(ppServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(procedimientoPasoModel.getRegistroById("no-es-un-uuid"));
        assertNull(procedimientoPasoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(ppServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, procedimientoPasoModel.getIdByRegistro(sampleProcedimientoPaso));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(procedimientoPasoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(ppServiceMock.contar()).thenReturn(3);

        assertNotNull(procedimientoPasoModel.getLazyModel());
        assertEquals(3, procedimientoPasoModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA completarProcedimiento
    // ==========================================

    @Test
    @DisplayName("completarProcedimiento - Éxito (Retorna los registros que encuentra el servicio)")
    void completarProcedimiento_Success() {
        List<Procedimiento> encontrados = List.of(new Procedimiento(UUID.randomUUID()));
        when(procServiceMock.buscarPorNombre("cir", 30)).thenReturn(encontrados);

        assertSame(encontrados, procedimientoPasoModel.completarProcedimiento("cir"));
    }

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
    @DisplayName("completarRol - Éxito (Retorna los registros que encuentra el servicio)")
    void completarRol_Success() {
        List<Rol> encontrados = List.of(new Rol(UUID.randomUUID()));
        when(rolServiceMock.buscarPorNombre("doc", 30)).thenReturn(encontrados);

        assertSame(encontrados, procedimientoPasoModel.completarRol("doc"));
    }

    @Test
    @DisplayName("completarRol - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarRol_Failures() {
        assertTrue(procedimientoPasoModel.completarRol(null).isEmpty());
        assertTrue(procedimientoPasoModel.completarRol("").isEmpty());
        assertTrue(procedimientoPasoModel.completarRol("   ").isEmpty());

        verify(rolServiceMock, never()).buscarPorNombre(any(), anyInt());
    }
}
