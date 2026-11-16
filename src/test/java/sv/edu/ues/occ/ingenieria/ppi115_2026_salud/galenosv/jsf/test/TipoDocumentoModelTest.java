package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.validator.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.TipoDocumentoModel;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoModelTest {

    private TipoDocumentoModel tipoDocumentoModel;

    @BeforeEach
    void setUp() {
        ModelTestSupport.crearModelo(this, TipoDocumentoModel.class, "tipoDocumentoModel");
    }

    @Test
    @DisplayName("validarExpresionRegular - Éxito")
    void validarExpresionRegular_Success() {
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, "\\d{8}-\\d"));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, null));
        assertDoesNotThrow(() -> tipoDocumentoModel.validarExpresionRegular(null, null, ""));
    }

    @Test
    @DisplayName("validarExpresionRegular - Fallo")
    void validarExpresionRegular_Failure() {
        ValidatorException ex = assertThrows(ValidatorException.class,
                () -> tipoDocumentoModel.validarExpresionRegular(null, null, "[abc"));
        assertEquals(FacesMessage.SEVERITY_ERROR, ex.getFacesMessage().getSeverity());
    }

}
