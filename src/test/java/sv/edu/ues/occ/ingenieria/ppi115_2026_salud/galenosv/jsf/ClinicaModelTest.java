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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ClinicaModel;

@ExtendWith(MockitoExtension.class)
class ClinicaModelTest {

    @Mock
    private ClinicaService clServiceMock;

    private ClinicaModel clinicaModel;

    private UUID sampleId;
    private Clinica sampleClinica;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ClinicaModel.class, "clinicaModel");
        sampleId = UUID.randomUUID();
        sampleClinica = new Clinica();
        sampleClinica.setIdClinica(sampleId);
        sampleClinica.setNombre("Clínica Central");
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(clServiceMock, clinicaModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Clinica primero = clinicaModel.instanciarRegistro();
        Clinica segundo = clinicaModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdClinica());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(clServiceMock.buscar(sampleId)).thenReturn(sampleClinica);

        Clinica resultado = clinicaModel.getRegistroById(sampleId.toString());

        assertSame(sampleClinica, resultado);
        verify(clServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(clServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(clinicaModel.getRegistroById("no-es-un-uuid"));
        assertNull(clinicaModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(clServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, clinicaModel.getIdByRegistro(sampleClinica));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(clinicaModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(clServiceMock.contar()).thenReturn(3);

        assertNotNull(clinicaModel.getLazyModel());
        assertEquals(3, clinicaModel.getLazyModel().count(Collections.emptyMap()));
    }
}
