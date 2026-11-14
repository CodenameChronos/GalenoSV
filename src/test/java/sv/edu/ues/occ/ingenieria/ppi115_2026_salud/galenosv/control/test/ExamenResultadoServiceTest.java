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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<ExamenResultado> resultadoQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private ExamenResultadoService service;

    private UUID idOrdenExamen;
    private ExamenResultado examenResultado;

    @BeforeEach
    void setUp() {
        idOrdenExamen = UUID.randomUUID();
        examenResultado = new ExamenResultado();
    }

    // ==========================================
    // PRUEBAS PARA findRangeByOrdenExamen
    // ==========================================

    @Test
    @DisplayName("findRangeByOrdenExamen - Éxito al obtener resultados paginados")
    void findRangeByOrdenExamen_Success() {
        List<ExamenResultado> lista = List.of(examenResultado);
        when(emMock.createNamedQuery("ExamenResultado.findRangeByOrdenExamen", ExamenResultado.class))
                .thenReturn(resultadoQueryMock);
        when(resultadoQueryMock.setParameter("orden", idOrdenExamen)).thenReturn(resultadoQueryMock);
        when(resultadoQueryMock.setFirstResult(0)).thenReturn(resultadoQueryMock);
        when(resultadoQueryMock.setMaxResults(10)).thenReturn(resultadoQueryMock);
        when(resultadoQueryMock.getResultList()).thenReturn(lista);

        List<ExamenResultado> resultado = service.findRangeByOrdenExamen(idOrdenExamen, 0, 10);

        assertEquals(lista, resultado);
        verify(resultadoQueryMock, times(1)).setParameter("orden", idOrdenExamen);
    }

    @Test
    @DisplayName("findRangeByOrdenExamen - Fallo en consulta (Propaga excepción)")
    void findRangeByOrdenExamen_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ExamenResultado.findRangeByOrdenExamen", ExamenResultado.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class,
                () -> service.findRangeByOrdenExamen(idOrdenExamen, 0, 10));
    }

    // ==========================================
    // PRUEBAS PARA contarByOrdenExamen
    // ==========================================

    @Test
    @DisplayName("contarByOrdenExamen - Éxito al contar resultados por orden")
    void contarByOrdenExamen_Success() {
        when(emMock.createNamedQuery("ExamenResultado.countByOrdenExamen", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("orden", idOrdenExamen)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(3L);

        int resultado = service.contarByOrdenExamen(idOrdenExamen);

        assertEquals(3, resultado);
    }

    @Test
    @DisplayName("contarByOrdenExamen - Fallo en consulta (Propaga excepción)")
    void contarByOrdenExamen_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ExamenResultado.countByOrdenExamen", Long.class))
                .thenThrow(new RuntimeException("Error en conteo"));

        assertThrows(RuntimeException.class,
                () -> service.contarByOrdenExamen(idOrdenExamen));
    }
}