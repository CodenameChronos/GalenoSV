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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenModel;

@ExtendWith(MockitoExtension.class)
class ExamenModelTest {

    @Mock
    private ExamenService examenServiceMock;

    private ExamenModel examenModel;

    private UUID sampleId;
    private Examen sampleExamen;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ExamenModel.class, "examenModel");
        sampleId = UUID.randomUUID();
        sampleExamen = new Examen();
        sampleExamen.setIdExamen(sampleId);
        sampleExamen.setNombre("Hemograma");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(examenServiceMock, examenModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Examen primero = examenModel.instanciarRegistro();
        Examen segundo = examenModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdExamen());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(examenServiceMock.buscar(sampleId)).thenReturn(sampleExamen);

        Examen resultado = examenModel.getRegistroById(sampleId.toString());

        assertSame(sampleExamen, resultado);
        verify(examenServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(examenServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(examenModel.getRegistroById("no-es-un-uuid"));
        assertNull(examenModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(examenServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, examenModel.getIdByRegistro(sampleExamen));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(examenModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(examenServiceMock.contar()).thenReturn(3);

        assertNotNull(examenModel.getLazyModel());
        assertEquals(3, examenModel.getLazyModel().count(Collections.emptyMap()));
    }
}
