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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ProcedimientoModel;

@ExtendWith(MockitoExtension.class)
class ProcedimientoModelTest {

    @Mock
    private ProcedimientoService procServiceMock;

    private ProcedimientoModel procedimientoModel;

    private UUID sampleId;
    private Procedimiento sampleProcedimiento;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ProcedimientoModel.class, "procedimientoModel");
        sampleId = UUID.randomUUID();
        sampleProcedimiento = new Procedimiento();
        sampleProcedimiento.setIdProcedimiento(sampleId);
        sampleProcedimiento.setNombre("Extracción de muestra");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(procServiceMock, procedimientoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Procedimiento primero = procedimientoModel.instanciarRegistro();
        Procedimiento segundo = procedimientoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdProcedimiento());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(procServiceMock.buscar(sampleId)).thenReturn(sampleProcedimiento);

        Procedimiento resultado = procedimientoModel.getRegistroById(sampleId.toString());

        assertSame(sampleProcedimiento, resultado);
        verify(procServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(procServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(procedimientoModel.getRegistroById("no-es-un-uuid"));
        assertNull(procedimientoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(procServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, procedimientoModel.getIdByRegistro(sampleProcedimiento));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(procedimientoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(procServiceMock.contar()).thenReturn(3);

        assertNotNull(procedimientoModel.getLazyModel());
        assertEquals(3, procedimientoModel.getLazyModel().count(Collections.emptyMap()));
    }
}
