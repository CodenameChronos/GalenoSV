package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExamenTipoExamenServiceTest {
    
    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<ExamenTipoExamen> eteQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @Mock
    private Query deleteQueryMock;

    @InjectMocks
    private ExamenTipoExamenService service;

    private UUID idExamen;
    private UUID idTipoExamen;
    private UUID idExamenTipoExamen;

    private Examen examen;
    private TipoExamen tipoExamen;
    private ExamenTipoExamen relacion;

    @BeforeEach
    void setUp() {
        idExamen = UUID.randomUUID();
        idTipoExamen = UUID.randomUUID();
        idExamenTipoExamen = UUID.randomUUID();

        examen = new Examen();
        examen.setIdExamen(idExamen);

        tipoExamen = new TipoExamen();
        tipoExamen.setIdTipoExamen(idTipoExamen);

        relacion = new ExamenTipoExamen();
        relacion.setIdExamenTipoExamen(idExamenTipoExamen);
        relacion.setIdExamen(examen);
        relacion.setIdTipoExamen(tipoExamen);
    }

    // ==========================================
    // PRUEBAS PARA findByIdExamen
    // ==========================================

    @Test
    @DisplayName("findByIdExamen - Éxito al obtener tipos asociados a un examen")
    void findByIdExamen_Success() {
        List<ExamenTipoExamen> lista = List.of(relacion);
        when(emMock.createNamedQuery("ExamenTipoExamen.findByIdExamen", ExamenTipoExamen.class))
                .thenReturn(eteQueryMock);
        when(eteQueryMock.setParameter("idExamen", idExamen)).thenReturn(eteQueryMock);
        when(eteQueryMock.setFirstResult(0)).thenReturn(eteQueryMock);
        when(eteQueryMock.setMaxResults(10)).thenReturn(eteQueryMock);
        when(eteQueryMock.getResultList()).thenReturn(lista);

        List<ExamenTipoExamen> resultado = service.findByIdExamen(idExamen, 0, 10);

        assertEquals(lista, resultado);
        verify(eteQueryMock, times(1)).setParameter("idExamen", idExamen);
    }

    @Test
    @DisplayName("findByIdExamen - UUID nulo (Retorna lista vacía sin consultar BD)")
    void findByIdExamen_NullUuid_ReturnsEmptyList() {
        List<ExamenTipoExamen> resultado = service.findByIdExamen(null, 0, 10);

        assertTrue(resultado.isEmpty());
        verify(emMock, never()).createNamedQuery(anyString(), eq(ExamenTipoExamen.class));
    }

    // ==========================================
    // PRUEBAS PARA findRange
    // ==========================================

    @Test
    @DisplayName("findRange - Éxito al obtener rango con padres e hijos")
    void findRange_Success() {
        List<ExamenTipoExamen> lista = List.of(relacion);
        when(emMock.createNamedQuery("ExamenTipoExamen.findRangePadresHijos", ExamenTipoExamen.class))
                .thenReturn(eteQueryMock);
        when(eteQueryMock.setFirstResult(0)).thenReturn(eteQueryMock);
        when(eteQueryMock.setMaxResults(10)).thenReturn(eteQueryMock);
        when(eteQueryMock.getResultList()).thenReturn(lista);

        List<ExamenTipoExamen> resultado = service.findRange(0, 10);

        assertEquals(lista, resultado);
        verify(eteQueryMock, times(1)).setFirstResult(0);
        verify(eteQueryMock, times(1)).setMaxResults(10);
    }

    @Test
    @DisplayName("findRange - Parámetros negativos (Lanza IllegalArgumentException)")
    void findRange_NegativeParams_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.findRange(-1, 10));
        verify(emMock, never()).createNamedQuery(anyString(), eq(ExamenTipoExamen.class));
    }

    // ==========================================
    // PRUEBAS PARA buscar
    // ==========================================

    @Test
    @DisplayName("buscar - Éxito al encontrar relación por UUID")
    void buscar_Success() {
        when(emMock.createNamedQuery("ExamenTipoExamen.buscarPadresHijos", ExamenTipoExamen.class))
                .thenReturn(eteQueryMock);
        when(eteQueryMock.setParameter("id", idExamenTipoExamen)).thenReturn(eteQueryMock);
        when(eteQueryMock.getResultList()).thenReturn(List.of(relacion));

        Object resultado = service.buscar(idExamenTipoExamen);

        assertEquals(relacion, resultado);
        verify(eteQueryMock, times(1)).setParameter("id", idExamenTipoExamen);
    }

    @Test
    @DisplayName("buscar - UUID nulo (Lanza IllegalArgumentException)")
    void buscar_NullUuid_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> service.buscar(null));
        verify(emMock, never()).createNamedQuery(anyString(), eq(ExamenTipoExamen.class));
    }

    // ==========================================
    // PRUEBAS PARA sincronizarTipos
    // ==========================================

    @Test
    @DisplayName("sincronizarTipos - Éxito al agregar tipos nuevos y eliminar sobrantes")
    void sincronizarTipos_Success() {
        TipoExamen tipoObsoleto = new TipoExamen();
        tipoObsoleto.setIdTipoExamen(UUID.randomUUID());

        ExamenTipoExamen relacionObsoleta = new ExamenTipoExamen();
        relacionObsoleta.setIdExamen(examen);
        relacionObsoleta.setIdTipoExamen(tipoObsoleto);

        when(emMock.createQuery(anyString(), eq(ExamenTipoExamen.class))).thenReturn(eteQueryMock);
        when(eteQueryMock.setParameter("idExamen", idExamen)).thenReturn(eteQueryMock);
        when(eteQueryMock.getResultList()).thenReturn(List.of(relacionObsoleta));
        when(emMock.getReference(Examen.class, idExamen)).thenReturn(examen);
        when(emMock.getReference(TipoExamen.class, idTipoExamen)).thenReturn(tipoExamen);

        service.sincronizarTipos(examen, List.of(tipoExamen));

        verify(emMock, times(1)).remove(relacionObsoleta);
        verify(emMock, times(1)).persist(any(ExamenTipoExamen.class));
    }

    @Test
    @DisplayName("sincronizarTipos - Examen nulo o sin ID (Lanza IllegalArgumentException)")
    void sincronizarTipos_NullExamen_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> service.sincronizarTipos(null, List.of(tipoExamen)));
        verify(emMock, never()).persist(any());
    }

    // ==========================================
    // PRUEBAS PARA eliminarTipos
    // ==========================================

    @Test
    @DisplayName("eliminarTipos - Éxito al ejecutar borrado masivo por examen")
    void eliminarTipos_Success() {
        when(emMock.createQuery(anyString())).thenReturn(deleteQueryMock);
        when(deleteQueryMock.setParameter("idExamen", idExamen)).thenReturn(deleteQueryMock);
        when(deleteQueryMock.executeUpdate()).thenReturn(2);

        service.eliminarTipos(idExamen);

        verify(deleteQueryMock, times(1)).executeUpdate();
    }

    @Test
    @DisplayName("eliminarTipos - UUID nulo (No ejecuta consulta)")
    void eliminarTipos_NullUuid_DoesNothing() {
        service.eliminarTipos(null);

        verify(emMock, never()).createQuery(anyString());
    }

    // ==========================================
    // PRUEBAS PARA existeRelacion
    // ==========================================

    @Test
    @DisplayName("existeRelacion - Éxito cuando existe la relación (Retorna true)")
    void existeRelacion_Exists_ReturnsTrue() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idExamen", idExamen)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idTipoExamen", idTipoExamen)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        boolean resultado = service.existeRelacion(idExamen, idTipoExamen);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("existeRelacion - Relación inexistente (Retorna false)")
    void existeRelacion_NotExists_ReturnsFalse() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idExamen", idExamen)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idTipoExamen", idTipoExamen)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(0L);

        boolean resultado = service.existeRelacion(idExamen, idTipoExamen);

        assertFalse(resultado);
    }

    // ==========================================
    // PRUEBAS PARA contarPorIdExamen
    // ==========================================

    @Test
    @DisplayName("contarPorIdExamen - Éxito al retornar conteo")
    void contarPorIdExamen_Success() {
        when(emMock.createNamedQuery("ExamenTipoExamen.countByIdExamen", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idExamen", idExamen)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(5L);

        int resultado = service.contarPorIdExamen(idExamen);

        assertEquals(5, resultado);
    }

    @Test
    @DisplayName("contarPorIdExamen - UUID nulo (Retorna 0 sin consultar BD)")
    void contarPorIdExamen_NullUuid_ReturnsZero() {
        int resultado = service.contarPorIdExamen(null);

        assertEquals(0, resultado);
        verify(emMock, never()).createNamedQuery(anyString(), eq(Long.class));
    }
    
}
