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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<ConsultaProcedimientoPaso> cppQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private ConsultaProcedimientoPasoService service;

    private UUID idConsultaProcedimiento;
    private ConsultaProcedimientoPaso paso;

    @BeforeEach
    void setUp() {
        idConsultaProcedimiento = UUID.randomUUID();
        paso = new ConsultaProcedimientoPaso();
    }

    // ==========================================
    // PRUEBAS PARA listarPorConsultaProcedimiento
    // ==========================================

    @Test
    @DisplayName("listarPorConsultaProcedimiento - Éxito al obtener pasos paginados")
    void listarPorConsultaProcedimiento_Success() {
        List<ConsultaProcedimientoPaso> lista = List.of(paso);
        when(emMock.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                ConsultaProcedimientoPaso.class)).thenReturn(cppQueryMock);
        when(cppQueryMock.setParameter("idConsultaProcedimiento", idConsultaProcedimiento))
                .thenReturn(cppQueryMock);
        when(cppQueryMock.setFirstResult(0)).thenReturn(cppQueryMock);
        when(cppQueryMock.setMaxResults(10)).thenReturn(cppQueryMock);
        when(cppQueryMock.getResultList()).thenReturn(lista);

        List<ConsultaProcedimientoPaso> resultado =
                service.listarPorConsultaProcedimiento(idConsultaProcedimiento, 0, 10);

        assertEquals(lista, resultado);
        verify(cppQueryMock, times(1))
                .setParameter("idConsultaProcedimiento", idConsultaProcedimiento);
    }

    @Test
    @DisplayName("listarPorConsultaProcedimiento - Fallo en consulta (Propaga excepción)")
    void listarPorConsultaProcedimiento_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ConsultaProcedimientoPaso.listarPorConsultaProcedimiento",
                ConsultaProcedimientoPaso.class)).thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class,
                () -> service.listarPorConsultaProcedimiento(idConsultaProcedimiento, 0, 10));
    }

    // ==========================================
    // PRUEBAS PARA contarPorConsultaProcedimiento
    // ==========================================

    @Test
    @DisplayName("contarPorConsultaProcedimiento - Éxito al contar pasos ejecutados")
    void contarPorConsultaProcedimiento_Success() {
        when(emMock.createNamedQuery("ConsultaProcedimientoPaso.contarPorConsultaProcedimiento", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idConsp", idConsultaProcedimiento)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(4L);

        int resultado = service.contarPorConsultaProcedimiento(idConsultaProcedimiento);

        assertEquals(4, resultado);
    }

    @Test
    @DisplayName("contarPorConsultaProcedimiento - Fallo en consulta (Propaga excepción)")
    void contarPorConsultaProcedimiento_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ConsultaProcedimientoPaso.contarPorConsultaProcedimiento", Long.class))
                .thenThrow(new RuntimeException("Error en conteo"));

        assertThrows(RuntimeException.class,
                () -> service.contarPorConsultaProcedimiento(idConsultaProcedimiento));
    }
}