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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<MedioContacto> medioQueryMock;

    @InjectMocks
    private MedioContactoService service;

    private UUID idMedioContacto;
    private MedioContacto medioContacto;

    @BeforeEach
    void setUp() {
        idMedioContacto = UUID.randomUUID();
        medioContacto = new MedioContacto();
        medioContacto.setIdMedioContacto(idMedioContacto);
    }

    // ==========================================
    // PRUEBAS PARA findRange
    // ==========================================
    @Test
    @DisplayName("findRange - Éxito al obtener rango con padres e hijos")
    void findRange_Success() {
        List<MedioContacto> lista = List.of(medioContacto);
        when(emMock.createNamedQuery("MedioContacto.findRangePadresHijos", MedioContacto.class))
                .thenReturn(medioQueryMock);
        when(medioQueryMock.setFirstResult(0)).thenReturn(medioQueryMock);
        when(medioQueryMock.setMaxResults(10)).thenReturn(medioQueryMock);
        when(medioQueryMock.getResultList()).thenReturn(lista);

        List<MedioContacto> resultado = service.findRange(0, 10);

        assertEquals(lista, resultado);
        verify(medioQueryMock, times(1)).setFirstResult(0);
    }

    @Test
    @DisplayName("findRange - Parámetros negativos (Lanza IllegalArgumentException)")
    void findRange_NegativeParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.findRange(-1, 10));
        verify(emMock, never()).createNamedQuery(anyString(), eq(MedioContacto.class));
    }

    // ==========================================
    // PRUEBAS PARA buscar
    // ==========================================
    @Test
    @DisplayName("buscar - Éxito al encontrar medio de contacto por UUID")
    void buscar_Success() {
        when(emMock.createNamedQuery("MedioContacto.buscarPadresHijos", MedioContacto.class))
                .thenReturn(medioQueryMock);
        when(medioQueryMock.setParameter("id", idMedioContacto)).thenReturn(medioQueryMock);
        when(medioQueryMock.getResultList()).thenReturn(List.of(medioContacto));

        Object resultado = service.buscar(idMedioContacto);

        assertEquals(medioContacto, resultado);
        verify(medioQueryMock, times(1)).setParameter("id", idMedioContacto);
    }

    @Test
    @DisplayName("buscar - UUID nulo (Lanza IllegalArgumentException)")
    void buscar_NullUuid_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.buscar(null));
        verify(emMock, never()).createNamedQuery(anyString(), eq(MedioContacto.class));
    }
}
