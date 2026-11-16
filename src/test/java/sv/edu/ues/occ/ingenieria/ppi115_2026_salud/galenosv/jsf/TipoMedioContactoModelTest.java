package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

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

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoModelTest {

    @Mock
    private TipoMedioContactoService tmcServiceMock;

    private TipoMedioContactoModel tipoMedioContactoModel;

    private UUID sampleId;
    private TipoMedioContacto sampleTipoMedioContacto;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, TipoMedioContactoModel.class, "tipoMedioContactoModel");
        sampleId = UUID.randomUUID();
        sampleTipoMedioContacto = new TipoMedioContacto();
        sampleTipoMedioContacto.setIdTipoMedioContacto(sampleId);
        sampleTipoMedioContacto.setNombre("Correo");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(tmcServiceMock, tipoMedioContactoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        TipoMedioContacto primero = tipoMedioContactoModel.instanciarRegistro();
        TipoMedioContacto segundo = tipoMedioContactoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdTipoMedioContacto());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(tmcServiceMock.buscar(sampleId)).thenReturn(sampleTipoMedioContacto);

        TipoMedioContacto resultado = tipoMedioContactoModel.getRegistroById(sampleId.toString());

        assertSame(sampleTipoMedioContacto, resultado);
        verify(tmcServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(tmcServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(tipoMedioContactoModel.getRegistroById("no-es-un-uuid"));
        assertNull(tipoMedioContactoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(tmcServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, tipoMedioContactoModel.getIdByRegistro(sampleTipoMedioContacto));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(tipoMedioContactoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(tmcServiceMock.contar()).thenReturn(3);

        assertNotNull(tipoMedioContactoModel.getLazyModel());
        assertEquals(3, tipoMedioContactoModel.getLazyModel().count(Collections.emptyMap()));
    }
}
