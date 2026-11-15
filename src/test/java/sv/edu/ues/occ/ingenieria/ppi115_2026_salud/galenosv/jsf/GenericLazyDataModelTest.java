package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.GenericLazyDataModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ModelHandler;

@ExtendWith(MockitoExtension.class)
class GenericLazyDataModelTest {

    @Mock
    private ModelHandler<Clinica> handlerMock;

    private GenericLazyDataModel<Clinica> lazyModel;
    private UUID sampleId;
    private Clinica sampleClinica;

    @BeforeEach
    void setUp() {
        lazyModel = new GenericLazyDataModel<>(handlerMock);
        sampleId = UUID.randomUUID();
        sampleClinica = new Clinica(sampleId, "Clínica Central");
    }

    // ==========================================
    // PRUEBAS PARA getRowKey
    // ==========================================

    @Test
    @DisplayName("getRowKey - Éxito (Retorna el id del registro como texto)")
    void getRowKey_Success() {
        when(handlerMock.getIdByRegistro(sampleClinica)).thenReturn(sampleId);

        assertEquals(sampleId.toString(), lazyModel.getRowKey(sampleClinica));
    }

    @Test
    @DisplayName("getRowKey - Fallos (Registro sin id retorna null)")
    void getRowKey_Failures() {
        Clinica sinId = new Clinica();
        when(handlerMock.getIdByRegistro(sinId)).thenReturn(null);

        assertNull(lazyModel.getRowKey(sinId));
    }

}
