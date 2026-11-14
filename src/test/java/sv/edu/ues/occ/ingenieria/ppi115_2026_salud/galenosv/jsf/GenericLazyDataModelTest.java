package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.Collections;
import java.util.List;
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
    // PRUEBAS PARA count
    // ==========================================

    @Test
    @DisplayName("count - Éxito (Retorna el total que calcula el Model)")
    void count_Success() {
        when(handlerMock.contar()).thenReturn(5);

        assertEquals(5, lazyModel.count(Collections.emptyMap()));
        verify(handlerMock, times(1)).contar();
    }

    @Test
    @DisplayName("count - Fallos (La excepción del Model se propaga)")
    void count_Failures() {
        when(handlerMock.contar()).thenThrow(new IllegalStateException("Query failed"));

        assertThrows(IllegalStateException.class, () -> lazyModel.count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA load
    // ==========================================

    @Test
    @DisplayName("load - Éxito (Retorna la página que busca el Model)")
    void load_Success() {
        List<Clinica> pagina = List.of(sampleClinica);
        when(handlerMock.buscarRegistros(10, 5)).thenReturn(pagina);

        List<Clinica> resultado = lazyModel.load(10, 5, Collections.emptyMap(), Collections.emptyMap());

        assertSame(pagina, resultado);
    }

    @Test
    @DisplayName("load - Fallos (La excepción del Model se propaga)")
    void load_Failures() {
        when(handlerMock.buscarRegistros(0, 10)).thenThrow(new IllegalStateException("Query failed"));

        assertThrows(IllegalStateException.class,
                () -> lazyModel.load(0, 10, Collections.emptyMap(), Collections.emptyMap()));
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

    // ==========================================
    // PRUEBAS PARA getRowData
    // ==========================================

    @Test
    @DisplayName("getRowData - Éxito (Retorna el registro que busca el Model por id)")
    void getRowData_Success() {
        when(handlerMock.getRegistroById(sampleId.toString())).thenReturn(sampleClinica);

        assertSame(sampleClinica, lazyModel.getRowData(sampleId.toString()));
    }

    @Test
    @DisplayName("getRowData - Fallos (Registro inexistente retorna null)")
    void getRowData_Failures() {
        String idInexistente = UUID.randomUUID().toString();
        when(handlerMock.getRegistroById(idInexistente)).thenReturn(null);

        assertNull(lazyModel.getRowData(idInexistente));
    }
}
