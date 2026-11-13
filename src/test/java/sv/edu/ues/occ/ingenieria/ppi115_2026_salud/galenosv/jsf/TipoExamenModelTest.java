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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.TipoExamenModel;

@ExtendWith(MockitoExtension.class)
class TipoExamenModelTest {

    @Mock
    private TipoExamenService teServiceMock;

    private TipoExamenModel tipoExamenModel;

    private UUID sampleId;
    private TipoExamen sampleTipoExamen;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, TipoExamenModel.class, "tipoExamenModel");
        sampleId = UUID.randomUUID();
        sampleTipoExamen = new TipoExamen();
        sampleTipoExamen.setIdTipoExamen(sampleId);
        sampleTipoExamen.setNombre("Laboratorio");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(teServiceMock, tipoExamenModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        TipoExamen primero = tipoExamenModel.instanciarRegistro();
        TipoExamen segundo = tipoExamenModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdTipoExamen());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(teServiceMock.buscar(sampleId)).thenReturn(sampleTipoExamen);

        TipoExamen resultado = tipoExamenModel.getRegistroById(sampleId.toString());

        assertSame(sampleTipoExamen, resultado);
        verify(teServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(teServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(tipoExamenModel.getRegistroById("no-es-un-uuid"));
        assertNull(tipoExamenModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(teServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, tipoExamenModel.getIdByRegistro(sampleTipoExamen));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(tipoExamenModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(teServiceMock.contar()).thenReturn(3);

        assertNotNull(tipoExamenModel.getLazyModel());
        assertEquals(3, tipoExamenModel.getLazyModel().count(Collections.emptyMap()));
    }
}
