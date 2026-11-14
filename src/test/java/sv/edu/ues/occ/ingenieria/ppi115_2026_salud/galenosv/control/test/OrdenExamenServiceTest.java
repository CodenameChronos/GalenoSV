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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<OrdenExamen> ordenQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private OrdenExamenService service;

    private UUID idConsPP;
    private OrdenExamen ordenExamen;

    @BeforeEach
    void setUp() {
        idConsPP = UUID.randomUUID();
        ordenExamen = new OrdenExamen();
    }

    // ==========================================
    // PRUEBAS PARA findRangeByConsultaProcedimientoPaso
    // ==========================================

    @Test
    @DisplayName("findRangeByConsultaProcedimientoPaso - Éxito al obtener órdenes paginadas")
    void findRangeByConsultaProcedimientoPaso_Success() {
        List<OrdenExamen> lista = List.of(ordenExamen);
        when(emMock.createNamedQuery("OrdenExamen.findByConsPP", OrdenExamen.class))
                .thenReturn(ordenQueryMock);
        when(ordenQueryMock.setParameter("conspp", idConsPP)).thenReturn(ordenQueryMock);
        when(ordenQueryMock.setFirstResult(0)).thenReturn(ordenQueryMock);
        when(ordenQueryMock.setMaxResults(10)).thenReturn(ordenQueryMock);
        when(ordenQueryMock.getResultList()).thenReturn(lista);

        List<OrdenExamen> resultado = service.findRangeByConsultaProcedimientoPaso(idConsPP, 0, 10);

        assertEquals(lista, resultado);
        verify(ordenQueryMock, times(1)).setParameter("conspp", idConsPP);
    }

    @Test
    @DisplayName("findRangeByConsultaProcedimientoPaso - Fallo en consulta (Propaga excepción)")
    void findRangeByConsultaProcedimientoPaso_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("OrdenExamen.findByConsPP", OrdenExamen.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class,
                () -> service.findRangeByConsultaProcedimientoPaso(idConsPP, 0, 10));
    }

    // ==========================================
    // PRUEBAS PARA contarByConsultaProcedimientoPaso
    // ==========================================

    @Test
    @DisplayName("contarByConsultaProcedimientoPaso - Éxito al contar órdenes")
    void contarByConsultaProcedimientoPaso_Success() {
        when(emMock.createNamedQuery("OrdenExamen.countByConsPP", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("conspp", idConsPP)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(6L);

        int resultado = service.contarByConsultaProcedimientoPaso(idConsPP);

        assertEquals(6, resultado);
    }

    @Test
    @DisplayName("contarByConsultaProcedimientoPaso - Fallo en consulta (Propaga excepción)")
    void contarByConsultaProcedimientoPaso_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("OrdenExamen.countByConsPP", Long.class))
                .thenThrow(new RuntimeException("Error en conteo"));

        assertThrows(RuntimeException.class,
                () -> service.contarByConsultaProcedimientoPaso(idConsPP));
    }
}