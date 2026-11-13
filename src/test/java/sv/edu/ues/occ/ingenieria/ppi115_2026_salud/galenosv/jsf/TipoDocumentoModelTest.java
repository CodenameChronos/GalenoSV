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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.TipoDocumentoModel;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoModelTest {

    @Mock
    private TipoDocumentoService tdServiceMock;

    private TipoDocumentoModel tipoDocumentoModel;

    private UUID sampleId;
    private TipoDocumento sampleTipoDocumento;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, TipoDocumentoModel.class, "tipoDocumentoModel");
        sampleId = UUID.randomUUID();
        sampleTipoDocumento = new TipoDocumento();
        sampleTipoDocumento.setIdTipoDocumento(sampleId);
        sampleTipoDocumento.setNombre("DUI");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(tdServiceMock, tipoDocumentoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        TipoDocumento primero = tipoDocumentoModel.instanciarRegistro();
        TipoDocumento segundo = tipoDocumentoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdTipoDocumento());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(tdServiceMock.buscar(sampleId)).thenReturn(sampleTipoDocumento);

        TipoDocumento resultado = tipoDocumentoModel.getRegistroById(sampleId.toString());

        assertSame(sampleTipoDocumento, resultado);
        verify(tdServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(tdServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(tipoDocumentoModel.getRegistroById("no-es-un-uuid"));
        assertNull(tipoDocumentoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(tdServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, tipoDocumentoModel.getIdByRegistro(sampleTipoDocumento));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(tipoDocumentoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(tdServiceMock.contar()).thenReturn(3);

        assertNotNull(tipoDocumentoModel.getLazyModel());
        assertEquals(3, tipoDocumentoModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA validarExpresionRegular
    // ==========================================

    @Test
    @DisplayName("validarExpresionRegular - Éxito (Acepta expresiones válidas, nulas o en blanco)")
    void validarExpresionRegular_Success() {
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, "\\d{8}-\\d"));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, " [A-Z]{3} "));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, null));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, ""));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, "   "));
    }

    @Test
    @DisplayName("validarExpresionRegular - Fallos (Expresión con sintaxis inválida lanza ValidatorException)")
    void validarExpresionRegular_Failures() {
        for (String invalida : new String[]{"[abc", "(sin cerrar", "*inicio"}) {
            ValidatorException ex = assertThrows(ValidatorException.class,
                    () -> tipoDocumentoModel.validarExpresionRegular(null, null, invalida));
            assertEquals(FacesMessage.SEVERITY_ERROR, ex.getFacesMessage().getSeverity());
        }
    }
}
