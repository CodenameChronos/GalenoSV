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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcedimientoPasoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<ProcedimientoPaso> pasoQueryMock;

    @Mock
    private TypedQuery<ProcedimientoPasoExamen> pasoExamenQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @Mock
    private Query deleteQueryMock;

    @InjectMocks
    private ProcedimientoPasoService service;

    private UUID idProcedimiento;
    private UUID idPasoPadre;
    private UUID idPasoNuevo;
    private UUID idExamen;

    private Procedimiento procedimiento;
    private ProcedimientoPaso pasoPadre;
    private ProcedimientoPaso pasoNuevo;
    private Examen examen;

    @BeforeEach
    void setUp() {
        idProcedimiento = UUID.randomUUID();
        idPasoPadre = UUID.randomUUID();
        idPasoNuevo = UUID.randomUUID();
        idExamen = UUID.randomUUID();

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);

        pasoPadre = new ProcedimientoPaso();
        pasoPadre.setIdProcedimientoPaso(idPasoPadre);
        pasoPadre.setIdProcedimiento(procedimiento);
        pasoPadre.setNombre("Paso Inicial");
        pasoPadre.setIndicaFin(false);

        pasoNuevo = new ProcedimientoPaso();
        pasoNuevo.setIdProcedimientoPaso(idPasoNuevo);
        pasoNuevo.setNombre("Paso Siguiente");
        pasoNuevo.setIndicaFin(true);

        examen = new Examen();
        examen.setIdExamen(idExamen);
    }

    // ==========================================
    // PRUEBAS PARA buscarPorNombre
    // ==========================================
    @Test
    @DisplayName("buscarPorNombre - Éxito al buscar pasos activos por texto")
    void buscarPorNombre_Success() {
        List<ProcedimientoPaso> lista = List.of(pasoPadre);
        when(emMock.createNamedQuery("ProcedimientoPaso.findActiveByNombre", ProcedimientoPaso.class))
                .thenReturn(pasoQueryMock);
        when(pasoQueryMock.setParameter("nombre", "%inicial%")).thenReturn(pasoQueryMock);
        when(pasoQueryMock.setMaxResults(10)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.getResultList()).thenReturn(lista);

        List<ProcedimientoPaso> resultado = service.buscarPorNombre("  Inicial ", 10);

        assertEquals(lista, resultado);
        verify(pasoQueryMock, times(1)).setParameter("nombre", "%inicial%");
        verify(pasoQueryMock, times(1)).setMaxResults(10);
    }

    @Test
    @DisplayName("buscarPorNombre - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombre_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombre(null, 10));
    }

    // ==========================================
    // PRUEBAS PARA listarPorProcedimiento
    // ==========================================
    @Test
    @DisplayName("listarPorProcedimiento - Éxito al obtener pasos del procedimiento")
    void listarPorProcedimiento_Success() {
        List<ProcedimientoPaso> lista = List.of(pasoPadre);
        when(emMock.createNamedQuery("ProcedimientoPaso.findByProcedimiento", ProcedimientoPaso.class))
                .thenReturn(pasoQueryMock);
        when(pasoQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.getResultList()).thenReturn(lista);

        List<ProcedimientoPaso> resultado = service.listarPorProcedimiento(idProcedimiento);

        assertEquals(lista, resultado);
        verify(pasoQueryMock, times(1)).setParameter("idProcedimiento", idProcedimiento);
    }

    @Test
    @DisplayName("listarPorProcedimiento - Fallo en consulta (Propaga excepción)")
    void listarPorProcedimiento_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ProcedimientoPaso.findByProcedimiento", ProcedimientoPaso.class))
                .thenThrow(new RuntimeException("Error en consulta"));

        assertThrows(RuntimeException.class, () -> service.listarPorProcedimiento(idProcedimiento));
    }

    // ==========================================
    // PRUEBAS PARA crearPasoInicial
    // ==========================================
    @Test
    @DisplayName("crearPasoInicial - Éxito cuando el procedimiento no tiene paso raíz")
    void crearPasoInicial_Success() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countPasoSinPadre", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(0L);

        when(emMock.createQuery(anyString(), eq(ProcedimientoPasoExamen.class)))
                .thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.setParameter("idPaso", idPasoNuevo)).thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.getResultList()).thenReturn(Collections.emptyList());
        when(emMock.getReference(Examen.class, idExamen)).thenReturn(examen);

        service.crearPasoInicial(pasoNuevo, procedimiento, List.of(examen));

        assertEquals(procedimiento, pasoNuevo.getIdProcedimiento());
        verify(emMock, times(1)).persist(pasoNuevo);
        verify(emMock, times(1)).persist(any(ProcedimientoPasoExamen.class));
    }

    @Test
    @DisplayName("crearPasoInicial - Ya existe paso inicial (Lanza ReglaNegocioException)")
    void crearPasoInicial_AlreadyExists_ThrowsReglaNegocioException() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countPasoSinPadre", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        assertThrows(ReglaNegocioException.class,
                () -> service.crearPasoInicial(pasoNuevo, procedimiento, List.of(examen)));
        verify(emMock, never()).persist(any());
    }

    // ==========================================
    // PRUEBAS PARA crearPasoHijo
    // ==========================================
    @Test
    @DisplayName("crearPasoHijo - Éxito al encadenar nuevo paso y secuencia")
    void crearPasoHijo_Success() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countHijos", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPaso", idPasoPadre)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(0L);

        when(emMock.createQuery(anyString(), eq(ProcedimientoPasoExamen.class)))
                .thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.setParameter("idPaso", idPasoNuevo)).thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.getResultList()).thenReturn(Collections.emptyList());

        service.crearPasoHijo(pasoNuevo, pasoPadre, null);

        assertEquals(procedimiento, pasoNuevo.getIdProcedimiento());
        verify(emMock, times(1)).persist(pasoNuevo);
        verify(emMock, times(1)).persist(any(ProcedimientoPasoSecuencia.class));
    }

    @Test
    @DisplayName("crearPasoHijo - Paso padre indica fin de rama (Lanza ReglaNegocioException)")
    void crearPasoHijo_ParentIsEnd_ThrowsReglaNegocioException() {
        pasoPadre.setIndicaFin(true);

        assertThrows(ReglaNegocioException.class,
                () -> service.crearPasoHijo(pasoNuevo, pasoPadre, List.of(examen)));
        verify(emMock, never()).persist(any());
    }

    // ==========================================
    // PRUEBAS PARA actualizarPaso
    // ==========================================
    @Test
    @DisplayName("actualizarPaso - Éxito al actualizar datos y sincronizar exámenes")
    void actualizarPaso_Success() {
        when(emMock.find(ProcedimientoPaso.class, idPasoNuevo)).thenReturn(pasoNuevo);
        when(emMock.createQuery(anyString(), eq(ProcedimientoPasoExamen.class)))
                .thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.setParameter("idPaso", idPasoNuevo)).thenReturn(pasoExamenQueryMock);
        when(pasoExamenQueryMock.getResultList()).thenReturn(Collections.emptyList());

        ProcedimientoPaso cambios = new ProcedimientoPaso();
        cambios.setIdProcedimientoPaso(idPasoNuevo);
        cambios.setNombre("Nombre Actualizado");
        cambios.setIndicaFin(false);

        service.actualizarPaso(cambios, Collections.emptyList());

        assertEquals("Nombre Actualizado", pasoNuevo.getNombre());
        assertFalse(pasoNuevo.getIndicaFin());
        verify(emMock, times(1)).find(ProcedimientoPaso.class, idPasoNuevo);
    }

    @Test
    @DisplayName("actualizarPaso - Paso no existe en BD (Lanza ReglaNegocioException)")
    void actualizarPaso_NotFound_ThrowsReglaNegocioException() {
        when(emMock.find(ProcedimientoPaso.class, idPasoNuevo)).thenReturn(null);

        assertThrows(ReglaNegocioException.class,
                () -> service.actualizarPaso(pasoNuevo, List.of(examen)));
    }

    // ==========================================
    // PRUEBAS PARA eliminarPaso
    // ==========================================
    @Test
    @DisplayName("eliminarPaso - Éxito al eliminar paso hoja, secuencia y exámenes")
    void eliminarPaso_Success() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countHijos", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPaso", idPasoNuevo)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(0L);

        when(emMock.createQuery(anyString())).thenReturn(deleteQueryMock);
        when(deleteQueryMock.setParameter("idPaso", idPasoNuevo)).thenReturn(deleteQueryMock);
        when(deleteQueryMock.executeUpdate()).thenReturn(1);

        service.eliminarPaso(idPasoNuevo);

        verify(deleteQueryMock, times(3)).executeUpdate();
    }

    @Test
    @DisplayName("eliminarPaso - Paso tiene hijos siguientes (Lanza ReglaNegocioException)")
    void eliminarPaso_HasChildren_ThrowsReglaNegocioException() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countHijos", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPaso", idPasoPadre)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        assertThrows(ReglaNegocioException.class, () -> service.eliminarPaso(idPasoPadre));
        verify(deleteQueryMock, never()).executeUpdate();
    }

    // ==========================================
    // PRUEBAS PARA obtenerPasoInicial
    // ==========================================
    @Test
    @DisplayName("obtenerPasoInicial - Éxito al retornar paso raíz")
    void obtenerPasoInicial_Success() {
        when(emMock.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(pasoQueryMock);
        when(pasoQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.setMaxResults(1)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.getResultList()).thenReturn(List.of(pasoPadre));

        ProcedimientoPaso resultado = service.obtenerPasoInicial(idProcedimiento);

        assertEquals(pasoPadre, resultado);
    }

    @Test
    @DisplayName("obtenerPasoInicial - Sin paso inicial definido (Retorna null)")
    void obtenerPasoInicial_EmptyResult_ReturnsNull() {
        when(emMock.createQuery(anyString(), eq(ProcedimientoPaso.class))).thenReturn(pasoQueryMock);
        when(pasoQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.setMaxResults(1)).thenReturn(pasoQueryMock);
        when(pasoQueryMock.getResultList()).thenReturn(Collections.emptyList());

        ProcedimientoPaso resultado = service.obtenerPasoInicial(idProcedimiento);

        assertNull(resultado);
    }

    // ==========================================
    // PRUEBAS PARA contarHijos
    // ==========================================
    @Test
    @DisplayName("contarHijos - Éxito al contar pasos hijos")
    void contarHijos_Success() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countHijos", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPaso", idPasoPadre)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        long resultado = service.contarHijos(idPasoPadre);

        assertEquals(1L, resultado);
    }

    @Test
    @DisplayName("contarHijos - Fallo en consulta (Propaga excepción)")
    void contarHijos_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countHijos", Long.class))
                .thenThrow(new RuntimeException("Error de BD"));

        assertThrows(RuntimeException.class, () -> service.contarHijos(idPasoPadre));
    }

    // ==========================================
    // PRUEBAS PARA contarPasosSinPadre
    // ==========================================
    @Test
    @DisplayName("contarPasosSinPadre - Éxito al contar pasos raíz")
    void contarPasosSinPadre_Success() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countPasoSinPadre", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        long resultado = service.contarPasosSinPadre(idProcedimiento);

        assertEquals(1L, resultado);
    }

    @Test
    @DisplayName("contarPasosSinPadre - Fallo en consulta (Lanza IllegalStateException)")
    void contarPasosSinPadre_QueryError_ThrowsIllegalStateException() {
        when(emMock.createNamedQuery("ProcedimientoPaso.countPasoSinPadre", Long.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(IllegalStateException.class, () -> service.contarPasosSinPadre(idProcedimiento));
    }

}
