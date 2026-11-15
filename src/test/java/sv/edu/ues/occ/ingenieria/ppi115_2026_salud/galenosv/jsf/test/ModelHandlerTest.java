package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;


import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ModelHandler;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.util.Mensajes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelHandlerTest {

    @Mock
    private ParentServiceInterface<DummyEntity> daoMock;

    @Mock
    private FacesContext facesContextMock;

    @Mock
    private UIViewRoot viewRootMock;

    @Mock
    private ActionEvent actionEventMock;

    @Mock
    private SelectEvent<DummyEntity> selectEventMock;

    private MockedStatic<FacesContext> facesContextStaticMock;
    private MockedStatic<Mensajes> mensajesStaticMock;

    private DummyModelHandler handler;
    private DummyEntity dummyEntity;

    static class DummyEntity {
        UUID id;
    }

    static class DummyModelHandler extends ModelHandler<DummyEntity> {

        private final ParentServiceInterface<DummyEntity> dao;
        boolean fallarAlInstanciar = false;

        DummyModelHandler(ParentServiceInterface<DummyEntity> dao) {
            super(DummyEntity.class);
            this.dao = dao;
        }

        @Override
        public DummyEntity getRegistroById(String id) {
            DummyEntity d = new DummyEntity();
            d.id = UUID.fromString(id);
            return d;
        }

        @Override
        public Object getIdByRegistro(DummyEntity registro) {
            return registro != null ? registro.id : null;
        }

        @Override
        public ParentServiceInterface<DummyEntity> getDAO() {
            return dao;
        }

        @Override
        public DummyEntity instanciarRegistro() {
            if (fallarAlInstanciar) {
                throw new RuntimeException("Error al instanciar entidad");
            }
            return new DummyEntity();
        }
    }

    @BeforeEach
    void setUp() {
        dummyEntity = new DummyEntity();
        dummyEntity.id = UUID.randomUUID();
        handler = new DummyModelHandler(daoMock);

        facesContextStaticMock = mockStatic(FacesContext.class);
        mensajesStaticMock = mockStatic(Mensajes.class);

        facesContextStaticMock.when(FacesContext::getCurrentInstance).thenReturn(facesContextMock);
        lenient().when(facesContextMock.getViewRoot()).thenReturn(viewRootMock);
        lenient().when(viewRootMock.getId()).thenReturn("j_id1");
        lenient().when(viewRootMock.getFacetsAndChildren()).thenReturn(Collections.emptyIterator());
    }

    @AfterEach
    void tearDown() {
        facesContextStaticMock.close();
        mensajesStaticMock.close();
    }

    // ==========================================
    // PRUEBAS PARA setFiltro
    // ==========================================
    @Test
    @DisplayName("setFiltro - Éxito al asignar propiedad y valor de filtro")
    void setFiltro_Success() {
        handler.setFiltro("estado", "ACTIVO");
        when(daoMock.findRangeByCriterio("estado", "ACTIVO", 0, 10)).thenReturn(List.of(dummyEntity));

        List<DummyEntity> resultado = handler.buscarRegistros(0, 10);

        assertEquals(1, resultado.size());
        verify(daoMock, times(1)).findRangeByCriterio("estado", "ACTIVO", 0, 10);
    }

    @Test
    @DisplayName("setFiltro - Propiedad nula (Desactiva el filtrado por criterio)")
    void setFiltro_NullProperty_FallsBackToDefaultRange() {
        handler.setFiltro(null, "ACTIVO");
        when(daoMock.findRange(0, 10)).thenReturn(List.of(dummyEntity));

        handler.buscarRegistros(0, 10);

        verify(daoMock, never()).findRangeByCriterio(any(), any(), anyInt(), anyInt());
        verify(daoMock, times(1)).findRange(0, 10);
    }

    // ==========================================
    // PRUEBAS PARA limpiarFiltro
    // ==========================================
    @Test
    @DisplayName("limpiarFiltro - Éxito al restablecer filtros activos")
    void limpiarFiltro_Success() {
        handler.setFiltro("nombre", "Juan");
        handler.limpiarFiltro();
        when(daoMock.findRange(0, 5)).thenReturn(List.of(dummyEntity));

        handler.buscarRegistros(0, 5);

        verify(daoMock, times(1)).findRange(0, 5);
        verify(daoMock, never()).findRangeByCriterio(any(), any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("limpiarFiltro - Ejecución cuando no existe filtro previo")
    void limpiarFiltro_AlreadyNull_RemainsClean() {
        assertDoesNotThrow(() -> handler.limpiarFiltro());
        when(daoMock.findRange(0, 5)).thenReturn(Collections.emptyList());

        List<DummyEntity> resultado = handler.buscarRegistros(0, 5);

        assertTrue(resultado.isEmpty());
        verify(daoMock, times(1)).findRange(0, 5);
    }

    // ==========================================
    // PRUEBAS PARA buscarRegistros
    // ==========================================
    @Test
    @DisplayName("buscarRegistros - Éxito al buscar sin filtro usando findRange")
    void buscarRegistros_WithoutFilter_Success() {
        List<DummyEntity> lista = List.of(dummyEntity);
        when(daoMock.findRange(0, 10)).thenReturn(lista);

        List<DummyEntity> resultado = handler.buscarRegistros(0, 10);

        assertEquals(lista, resultado);
        verify(daoMock, times(1)).findRange(0, 10);
    }

    @Test
    @DisplayName("buscarRegistros - Fallo en DAO al buscar por criterio (Propaga excepción)")
    void buscarRegistros_WithFilterDaoError_ThrowsException() {
        handler.setFiltro("invalido", 123);
        when(daoMock.findRangeByCriterio("invalido", 123, 0, 10))
                .thenThrow(new IllegalArgumentException("Propiedad inválida"));

        assertThrows(IllegalArgumentException.class, () -> handler.buscarRegistros(0, 10));
    }

    // ==========================================
    // PRUEBAS PARA init
    // ==========================================
    @Test
    @DisplayName("init - Éxito al inicializar estado NINGUNO y registro actual")
    void init_Success() {
        handler.setEstado(ESTADO_CRUD.CREAR);

        handler.init();

        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertNotNull(handler.getRegistroActual());
    }

    @Test
    @DisplayName("init - Fallo al instanciar registro inicial (Propaga RuntimeException)")
    void init_InstantiationError_ThrowsException() {
        handler.fallarAlInstanciar = true;

        assertThrows(RuntimeException.class, () -> handler.init());
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================
    @Test
    @DisplayName("nuevo - Éxito al preparar estado CREAR y nueva instancia")
    void nuevo_Success() {
        handler.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, handler.getEstado());
        assertNotNull(handler.getRegistroActual());
    }

    @Test
    @DisplayName("nuevo - Fallo cuando FacesContext es nulo al reiniciar inputs")
    void nuevo_NullFacesContext_ThrowsNullPointerException() {
        facesContextStaticMock.when(FacesContext::getCurrentInstance).thenReturn(null);

        assertThrows(NullPointerException.class, () -> handler.nuevo());
    }

    // ==========================================
    // PRUEBAS PARA seleccionar
    // ==========================================
    @Test
    @DisplayName("seleccionar - Éxito al asignar registro seleccionado y estado MODIFICAR")
    void seleccionar_Success() {
        when(selectEventMock.getObject()).thenReturn(dummyEntity);

        handler.seleccionar(selectEventMock);

        assertEquals(ESTADO_CRUD.MODIFICAR, handler.getEstado());
        assertEquals(dummyEntity, handler.getRegistroActual());
    }

    @Test
    @DisplayName("seleccionar - Evento nulo (Lanza NullPointerException)")
    void seleccionar_NullEvent_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> handler.seleccionar(null));
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================
    @Test
    @DisplayName("guardarHandler - Éxito al crear registro en estado CREAR")
    void guardarHandler_CreateSuccess() {
        handler.setEstado(ESTADO_CRUD.CREAR);
        handler.setRegistroActual(dummyEntity);
        doNothing().when(daoMock).crear(dummyEntity);

        handler.guardarHandler(actionEventMock);

        verify(daoMock, times(1)).crear(dummyEntity);
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        mensajesStaticMock.verify(() -> Mensajes.exito("mensaje.exito"), times(1));
    }

    @Test
    @DisplayName("guardarHandler - Fallo en DAO al actualizar (Captura excepción y muestra mensaje de error)")
    void guardarHandler_DaoException_ShowsErrorMessage() {
        handler.setEstado(ESTADO_CRUD.MODIFICAR);
        handler.setRegistroActual(dummyEntity);
        doThrow(new RuntimeException("Error DB")).when(daoMock).actualizar(dummyEntity);

        assertDoesNotThrow(() -> handler.guardarHandler(actionEventMock));

        assertEquals(ESTADO_CRUD.MODIFICAR, handler.getEstado());
        mensajesStaticMock.verify(() -> Mensajes.error("mensaje.error"), times(1));
    }

    // ==========================================
    // PRUEBAS PARA eliminarHandler
    // ==========================================
    @Test
    @DisplayName("eliminarHandler - Éxito al eliminar registro y reiniciar estado")
    void eliminarHandler_Success() {
        handler.setEstado(ESTADO_CRUD.MODIFICAR);
        handler.setRegistroActual(dummyEntity);
        doNothing().when(daoMock).eliminar(dummyEntity);

        handler.eliminarHandler(actionEventMock);

        verify(daoMock, times(1)).eliminar(dummyEntity);
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        mensajesStaticMock.verify(() -> Mensajes.exito("mensaje.exito"), times(1));
    }

    @Test
    @DisplayName("eliminarHandler - Fallo en DAO al eliminar (Lanza IllegalStateException)")
    void eliminarHandler_DaoException_ThrowsIllegalStateException() {
        handler.setRegistroActual(dummyEntity);
        doThrow(new RuntimeException("Violación FK")).when(daoMock).eliminar(dummyEntity);

        assertThrows(IllegalStateException.class, () -> handler.eliminarHandler(actionEventMock));
        mensajesStaticMock.verify(() -> Mensajes.exito(anyString()), never());
    }

    // ==========================================
    // PRUEBAS PARA cancelarHandler
    // ==========================================
    @Test
    @DisplayName("cancelarHandler - Éxito al cancelar operación y reiniciar estado a NINGUNO")
    void cancelarHandler_Success() {
        handler.setEstado(ESTADO_CRUD.MODIFICAR);
        handler.setRegistroActual(dummyEntity);

        handler.cancelarHandler(actionEventMock);

        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertNotNull(handler.getRegistroActual());
        assertNotEquals(dummyEntity, handler.getRegistroActual());
    }

    @Test
    @DisplayName("cancelarHandler - Fallo cuando FacesContext no está disponible")
    void cancelarHandler_NullFacesContext_ThrowsNullPointerException() {
        facesContextStaticMock.when(FacesContext::getCurrentInstance).thenReturn(null);

        assertThrows(NullPointerException.class, () -> handler.cancelarHandler(actionEventMock));
    }

    // ==========================================
    // PRUEBAS PARA contar
    // ==========================================
    @Test
    @DisplayName("contar - Éxito al contar registros sin filtro")
    void contar_Success() {
        when(daoMock.contar()).thenReturn(15);

        int total = handler.contar();

        assertEquals(15, total);
        verify(daoMock, times(1)).contar();
    }

    @Test
    @DisplayName("contar - Fallo en DAO al contar por criterio (Lanza IllegalStateException)")
    void contar_DaoException_ThrowsIllegalStateException() {
        handler.setFiltro("activo", true);
        when(daoMock.contarByCriterio("activo", true))
                .thenThrow(new RuntimeException("Error de consulta"));

        assertThrows(IllegalStateException.class, () -> handler.contar());
    }

    // ==========================================
    // PRUEBAS PARA obtenerRegistros
    // ==========================================
    @Test
    @DisplayName("obtenerRegistros - Éxito al cargar lista paginada de registros")
    void obtenerRegistros_Success() {
        List<DummyEntity> lista = List.of(dummyEntity);
        when(daoMock.findRange(0, 10)).thenReturn(lista);

        handler.obtenerRegistros(0, 10);

        assertEquals(lista, handler.getRegistros());
        verify(daoMock, times(1)).findRange(0, 10);
    }

    @Test
    @DisplayName("obtenerRegistros - Fallo en DAO al obtener rango (Propaga excepción)")
    void obtenerRegistros_DaoError_ThrowsException() {
        when(daoMock.findRange(-1, 10)).thenThrow(new IllegalArgumentException("Rango inválido"));

        assertThrows(IllegalArgumentException.class, () -> handler.obtenerRegistros(-1, 10));
    }

}