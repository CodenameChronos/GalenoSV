package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Documento> documentoQueryMock;

    @InjectMocks
    private DocumentoService service;

    private UUID idDocumento;
    private Documento documento;

    @BeforeEach
    void setUp() {
        idDocumento = UUID.randomUUID();
        documento = new Documento();
        documento.setIdDocumento(idDocumento);
    }

    // ==========================================
    // PRUEBAS PARA findRange
    // ==========================================

    @Test
    @DisplayName("findRange - Éxito al obtener rango con padres e hijos")
    void findRange_Success() {
        List<Documento> lista = List.of(documento);
        when(emMock.createNamedQuery("Documento.findRangePadresHijos", Documento.class))
                .thenReturn(documentoQueryMock);
        when(documentoQueryMock.setFirstResult(0)).thenReturn(documentoQueryMock);
        when(documentoQueryMock.setMaxResults(10)).thenReturn(documentoQueryMock);
        when(documentoQueryMock.getResultList()).thenReturn(lista);

        List<Documento> resultado = service.findRange(0, 10);

        assertEquals(lista, resultado);
        verify(documentoQueryMock, times(1)).setFirstResult(0);
        verify(documentoQueryMock, times(1)).setMaxResults(10);
    }

    @Test
    @DisplayName("findRange - Parámetros negativos (Lanza IllegalArgumentException)")
    void findRange_NegativeParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.findRange(-1, 10));
        verify(emMock, never()).createNamedQuery(anyString(), eq(Documento.class));
    }

    // ==========================================
    // PRUEBAS PARA buscar
    // ==========================================

    @Test
    @DisplayName("buscar - Éxito al encontrar documento por UUID")
    void buscar_Success() {
        when(emMock.createNamedQuery("Documento.buscarPadresHijos", Documento.class))
                .thenReturn(documentoQueryMock);
        when(documentoQueryMock.setParameter("id", idDocumento)).thenReturn(documentoQueryMock);
        when(documentoQueryMock.getResultList()).thenReturn(List.of(documento));

        Object resultado = service.buscar(idDocumento);

        assertEquals(documento, resultado);
        verify(documentoQueryMock, times(1)).setParameter("id", idDocumento);
    }

    @Test
    @DisplayName("buscar - UUID nulo (Lanza IllegalArgumentException)")
    void buscar_NullUuid_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.buscar(null));
        verify(emMock, never()).createNamedQuery(anyString(), eq(Documento.class));
    }
}