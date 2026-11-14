package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<DummyEntity> typedQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @Mock
    private Query queryMock;

    @InjectMocks
    private DummyParentService service;

    private UUID idDummy;
    private DummyEntity dummyEntity;

    static class DummyEntity {

        UUID idDummyEntity;
    }

    static class DummyParentService extends ParentService<DummyEntity> {

        private EntityManager em;

        DummyParentService() {
            super(DummyEntity.class);
        }

        @Override
        public EntityManager getEntityManager() {
            return em;
        }
    }

    @BeforeEach
    void setUp() {
        idDummy = UUID.randomUUID();
        dummyEntity = new DummyEntity();
        dummyEntity.idDummyEntity = idDummy;
    }

    // ==========================================
    // PRUEBAS PARA crear
    // ==========================================
    @Test
    @DisplayName("crear - Éxito al persistir entidad válida")
    void crear_Success() {
        doNothing().when(emMock).persist(dummyEntity);

        service.crear(dummyEntity);

        verify(emMock, times(1)).persist(dummyEntity);
    }

    @Test
    @DisplayName("crear - Registro nulo (Lanza IllegalArgumentException)")
    void crear_NullEntity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.crear(null));
        verify(emMock, never()).persist(any());
    }

    // ==========================================
    // PRUEBAS PARA actualizar
    // ==========================================
    @Test
    @DisplayName("actualizar - Éxito al ejecutar merge")
    void actualizar_Success() {
        when(emMock.merge(dummyEntity)).thenReturn(dummyEntity);

        service.actualizar(dummyEntity);

        verify(emMock, times(1)).merge(dummyEntity);
    }

    @Test
    @DisplayName("actualizar - Registro nulo (Lanza IllegalArgumentException)")
    void actualizar_NullEntity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizar(null));
        verify(emMock, never()).merge(any());
    }

    // ==========================================
    // PRUEBAS PARA eliminar
    // ==========================================
    @Test
    @DisplayName("eliminar - Éxito al remover entidad y hacer flush")
    void eliminar_Success() {
        when(emMock.contains(dummyEntity)).thenReturn(false);
        when(emMock.merge(dummyEntity)).thenReturn(dummyEntity);
        doNothing().when(emMock).remove(dummyEntity);
        doNothing().when(emMock).flush();

        service.eliminar(dummyEntity);

        verify(emMock, times(1)).remove(dummyEntity);
        verify(emMock, times(1)).flush();
    }

    @Test
    @DisplayName("eliminar - Violación de llave foránea (Lanza ReglaNegocioException)")
    void eliminar_ForeignKeyViolation_ThrowsReglaNegocioException() {
        when(emMock.contains(dummyEntity)).thenReturn(true);
        SQLException sqlEx = new SQLException("Violación de integridad referencial", "23503");
        doThrow(new RuntimeException(sqlEx)).when(emMock).flush();

        assertThrows(ReglaNegocioException.class, () -> service.eliminar(dummyEntity));
        verify(emMock, times(1)).remove(dummyEntity);
    }

    // ==========================================
    // PRUEBAS PARA buscar
    // ==========================================
    @Test
    @DisplayName("buscar - Éxito al encontrar entidad por UUID")
    void buscar_Success() {
        when(emMock.find(DummyEntity.class, idDummy)).thenReturn(dummyEntity);

        Object resultado = service.buscar(idDummy);

        assertNotNull(resultado);
        assertEquals(dummyEntity, resultado);
        verify(emMock, times(1)).find(DummyEntity.class, idDummy);
    }

    @Test
    @DisplayName("buscar - UUID nulo (Lanza IllegalArgumentException)")
    void buscar_NullId_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.buscar(null));
        verify(emMock, never()).find(any(), any());
    }

    // ==========================================
    // PRUEBAS PARA listarTodos
    // ==========================================
    @Test
    @DisplayName("listarTodos - Éxito al obtener lista completa")
    void listarTodos_Success() {
        List<DummyEntity> lista = List.of(dummyEntity);
        when(emMock.createNamedQuery(anyString())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        List<DummyEntity> resultado = service.listarTodos();

        assertEquals(lista, resultado);
        verify(emMock, times(1)).createNamedQuery(anyString());
    }

    @Test
    @DisplayName("listarTodos - Fallo en consulta (Lanza RuntimeException)")
    void listarTodos_QueryError_ThrowsException() {
        when(emMock.createNamedQuery(anyString())).thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class, () -> service.listarTodos());
    }

    // ==========================================
    // PRUEBAS PARA findRange
    // ==========================================
    @Test
    @DisplayName("findRange - Éxito al obtener rango paginado")
    void findRange_Success() {
        List<DummyEntity> lista = List.of(dummyEntity);
        when(emMock.createQuery(anyString(), eq(DummyEntity.class))).thenReturn(typedQueryMock);
        when(typedQueryMock.setFirstResult(0)).thenReturn(typedQueryMock);
        when(typedQueryMock.setMaxResults(10)).thenReturn(typedQueryMock);
        when(typedQueryMock.getResultList()).thenReturn(lista);

        List<DummyEntity> resultado = service.findRange(0, 10);

        assertEquals(lista, resultado);
        verify(typedQueryMock, times(1)).setFirstResult(0);
        verify(typedQueryMock, times(1)).setMaxResults(10);
    }

    @Test
    @DisplayName("findRange - Parámetros negativos (Lanza IllegalArgumentException)")
    void findRange_NegativeParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.findRange(-1, 10));
        verify(emMock, never()).createQuery(anyString(), eq(DummyEntity.class));
    }

    // ==========================================
    // PRUEBAS PARA findRangeByCriterio
    // ==========================================
    @Test
    @DisplayName("findRangeByCriterio - Éxito al filtrar por propiedad")
    void findRangeByCriterio_Success() {
        List<DummyEntity> lista = List.of(dummyEntity);
        when(emMock.createQuery(anyString(), eq(DummyEntity.class))).thenReturn(typedQueryMock);
        when(typedQueryMock.setParameter("valor", "ACTIVO")).thenReturn(typedQueryMock);
        when(typedQueryMock.setFirstResult(0)).thenReturn(typedQueryMock);
        when(typedQueryMock.setMaxResults(5)).thenReturn(typedQueryMock);
        when(typedQueryMock.getResultList()).thenReturn(lista);

        List<DummyEntity> resultado = service.findRangeByCriterio("estado", "ACTIVO", 0, 5);

        assertEquals(lista, resultado);
        verify(typedQueryMock, times(1)).setParameter("valor", "ACTIVO");
    }

    @Test
    @DisplayName("findRangeByCriterio - Parámetros negativos (Lanza IllegalArgumentException)")
    void findRangeByCriterio_NegativeParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findRangeByCriterio("estado", "ACTIVO", 0, -5));
        verify(emMock, never()).createQuery(anyString(), eq(DummyEntity.class));
    }

    // ==========================================
    // PRUEBAS PARA contarByCriterio
    // ==========================================
    @Test
    @DisplayName("contarByCriterio - Éxito al contar registros filtrados")
    void contarByCriterio_Success() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("valor", true)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(4L);

        int resultado = service.contarByCriterio("activo", true);

        assertEquals(4, resultado);
        verify(countQueryMock, times(1)).setParameter("valor", true);
    }

    @Test
    @DisplayName("contarByCriterio - Fallo en consulta (Propaga excepción)")
    void contarByCriterio_QueryError_ThrowsException() {
        when(emMock.createQuery(anyString(), eq(Long.class)))
                .thenThrow(new IllegalArgumentException("Propiedad inválida"));

        assertThrows(IllegalArgumentException.class,
                () -> service.contarByCriterio("propiedadInexistente", 1));
    }

    // ==========================================
    // PRUEBAS PARA contar
    // ==========================================
    @Test
    @DisplayName("contar - Éxito al obtener conteo total")
    void contar_Success() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(12L);

        int resultado = service.contar();

        assertEquals(12, resultado);
        verify(countQueryMock, times(1)).getSingleResult();
    }

    @Test
    @DisplayName("contar - Fallo en consulta (Propaga excepción)")
    void contar_QueryError_ThrowsException() {
        when(emMock.createQuery(anyString(), eq(Long.class)))
                .thenThrow(new RuntimeException("Error de conexión"));

        assertThrows(RuntimeException.class, () -> service.contar());
    }

}
