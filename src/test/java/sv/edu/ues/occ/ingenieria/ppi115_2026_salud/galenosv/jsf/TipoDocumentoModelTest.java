package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.validator.ValidatorException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.TipoDocumentoModel;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoModelTest {

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
