package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ModelHandler;

@ExtendWith(MockitoExtension.class)
class ModelHandlerTest {

    /**
     * Model mínimo que solo existe para poder instanciar la clase abstracta.
     */
    static class ModeloPrueba extends ModelHandler<Clinica> {

        private static final long serialVersionUID = 1L;

        private final transient ParentServiceInterface<Clinica> dao;

        ModeloPrueba(ParentServiceInterface<Clinica> dao) {
            super(Clinica.class);
            this.dao = dao;
        }

        @Override
        public Clinica getRegistroById(String id) {
            return null;
        }

        @Override
        public Object getIdByRegistro(Clinica registro) {
            return registro != null ? registro.getIdClinica() : null;
        }

        @Override
        public ParentServiceInterface<Clinica> getDAO() {
            return dao;
        }

        @Override
        public Clinica instanciarRegistro() {
            return new Clinica();
        }
    }

    @Mock
    private ParentServiceInterface<Clinica> daoMock;

    @Mock
    private ActionEvent actionEventMock;

    @Mock
    private SelectEvent<Clinica> selectEventMock;

    private ModeloPrueba modelo;
    private Clinica sampleClinica;
    private FacesTestSupport faces;

    @BeforeEach
    void setUp() {
        faces = FacesTestSupport.abrir();
        modelo = new ModeloPrueba(daoMock);
        sampleClinica = new Clinica(UUID.randomUUID(), "Clínica Central");
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA init
    // ==========================================

    @Test
    @DisplayName("init - Éxito (Estado NINGUNO y registro actual nuevo)")
    void init_Success() {
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.init();

        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertNotNull(modelo.getRegistroActual());
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Estado CREAR con un registro nuevo)")
    void nuevo_Success() {
        Clinica anterior = new Clinica();
        modelo.setRegistroActual(anterior);

        modelo.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertNotNull(modelo.getRegistroActual());
        assertNotSame(anterior, modelo.getRegistroActual());
    }

    // ==========================================
    // PRUEBAS PARA seleccionar
    // ==========================================

    @Test
    @DisplayName("seleccionar - Éxito (Estado MODIFICAR con el registro elegido)")
    void seleccionar_Success() {
        when(selectEventMock.getObject()).thenReturn(sampleClinica);

        modelo.seleccionar(selectEventMock);

        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
        assertSame(sampleClinica, modelo.getRegistroActual());
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================

    @Test
    @DisplayName("guardarHandler - Éxito (CREAR llama crear, MODIFICAR llama actualizar)")
    void guardarHandler_Success() {
        modelo.setRegistroActual(sampleClinica);

        modelo.setEstado(ESTADO_CRUD.CREAR);
        modelo.guardarHandler(actionEventMock);

        verify(daoMock, times(1)).crear(sampleClinica);
        verify(daoMock, never()).actualizar(any());
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertEquals(FacesMessage.SEVERITY_INFO, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("mensaje.exito"), faces.ultimoMensaje().getSummary());

        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        modelo.guardarHandler(actionEventMock);

        verify(daoMock, times(1)).actualizar(sampleClinica);
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertEquals(2, faces.mensajes().size());
    }

    @Test
    @DisplayName("guardarHandler - Fallos (Error en crear o actualizar muestra mensaje y conserva el estado)")
    void guardarHandler_Failures() {
        doThrow(new IllegalStateException("Database error")).when(daoMock).crear(any());
        doThrow(new IllegalArgumentException("Dato inválido")).when(daoMock).actualizar(any());
        modelo.setRegistroActual(sampleClinica);

        // Falla al crear
        modelo.setEstado(ESTADO_CRUD.CREAR);
        assertDoesNotThrow(() -> modelo.guardarHandler(actionEventMock));

        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("mensaje.error"), faces.ultimoMensaje().getSummary());

        // Falla al actualizar
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);
        assertDoesNotThrow(() -> modelo.guardarHandler(actionEventMock));

        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals(2, faces.mensajes().size());
    }

    // ==========================================
    // PRUEBAS PARA eliminarHandler
    // ==========================================

    @Test
    @DisplayName("eliminarHandler - Éxito (Elimina el registro actual y vuelve a NINGUNO)")
    void eliminarHandler_Success() {
        modelo.setRegistroActual(sampleClinica);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.eliminarHandler(actionEventMock);

        verify(daoMock, times(1)).eliminar(sampleClinica);
        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertEquals(FacesMessage.SEVERITY_INFO, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("mensaje.exito"), faces.ultimoMensaje().getSummary());
    }

    @Test
    @DisplayName("eliminarHandler - Fallos (Cualquier excepción del DAO se relanza como IllegalStateException)")
    void eliminarHandler_Failures() {
        ReglaNegocioException reglaNegocio = new ReglaNegocioException("La clínica tiene registros asociados");
        IllegalArgumentException argumentoInvalido = new IllegalArgumentException("Registro nulo");
        doThrow(reglaNegocio).doThrow(argumentoInvalido).when(daoMock).eliminar(any());
        modelo.setRegistroActual(sampleClinica);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        IllegalStateException primera = assertThrows(IllegalStateException.class,
                () -> modelo.eliminarHandler(actionEventMock));
        IllegalStateException segunda = assertThrows(IllegalStateException.class,
                () -> modelo.eliminarHandler(actionEventMock));

        assertSame(reglaNegocio, primera.getCause());
        assertSame(argumentoInvalido, segunda.getCause());
        assertEquals(ESTADO_CRUD.MODIFICAR, modelo.getEstado());
        assertTrue(faces.mensajes().isEmpty());
    }

    // ==========================================
    // PRUEBAS PARA cancelarHandler
    // ==========================================

    @Test
    @DisplayName("cancelarHandler - Éxito (Vuelve a NINGUNO con un registro limpio)")
    void cancelarHandler_Success() {
        modelo.setRegistroActual(sampleClinica);
        modelo.setEstado(ESTADO_CRUD.MODIFICAR);

        modelo.cancelarHandler(actionEventMock);

        assertEquals(ESTADO_CRUD.NINGUNO, modelo.getEstado());
        assertNotSame(sampleClinica, modelo.getRegistroActual());
        assertNull(modelo.getRegistroActual().getIdClinica());
    }

    // ==========================================
    // PRUEBAS PARA contar
    // ==========================================

    @Test
    @DisplayName("contar - Éxito (Sin filtro usa contar, con filtro usa contarByCriterio)")
    void contar_Success() {
        when(daoMock.contar()).thenReturn(7);
        when(daoMock.contarByCriterio("nombre", "Central")).thenReturn(2);

        assertEquals(7, modelo.contar());

        modelo.setFiltro("nombre", "Central");
        assertEquals(2, modelo.contar());

        modelo.limpiarFiltro();
        assertEquals(7, modelo.contar());
    }

    @Test
    @DisplayName("contar - Fallos (Excepción del DAO se relanza como IllegalStateException)")
    void contar_Failures() {
        RuntimeException errorSinFiltro = new RuntimeException("Query failed");
        RuntimeException errorConFiltro = new RuntimeException("Criterio inválido");
        when(daoMock.contar()).thenThrow(errorSinFiltro);
        when(daoMock.contarByCriterio("nombre", "Central")).thenThrow(errorConFiltro);

        IllegalStateException sinFiltro = assertThrows(IllegalStateException.class, () -> modelo.contar());
        assertSame(errorSinFiltro, sinFiltro.getCause());

        modelo.setFiltro("nombre", "Central");
        IllegalStateException conFiltro = assertThrows(IllegalStateException.class, () -> modelo.contar());
        assertSame(errorConFiltro, conFiltro.getCause());
    }

    // ==========================================
    // PRUEBAS PARA buscarRegistros / setFiltro / limpiarFiltro
    // ==========================================

    @Test
    @DisplayName("buscarRegistros - Éxito (Sin filtro usa findRange, con filtro usa findRangeByCriterio)")
    void buscarRegistros_Success() {
        List<Clinica> todas = List.of(sampleClinica);
        List<Clinica> filtradas = List.of();
        when(daoMock.findRange(0, 10)).thenReturn(todas);
        when(daoMock.findRangeByCriterio("nombre", "Central", 0, 10)).thenReturn(filtradas);

        assertSame(todas, modelo.buscarRegistros(0, 10));

        modelo.setFiltro("nombre", "Central");
        assertSame(filtradas, modelo.buscarRegistros(0, 10));

        modelo.limpiarFiltro();
        assertSame(todas, modelo.buscarRegistros(0, 10));
    }

    @Test
    @DisplayName("buscarRegistros - Fallos (La excepción del DAO se propaga)")
    void buscarRegistros_Failures() {
        when(daoMock.findRange(0, 10)).thenThrow(new IllegalStateException("Query failed"));
        when(daoMock.findRangeByCriterio("nombre", "Central", 0, 10))
                .thenThrow(new IllegalArgumentException("Propiedad inválida"));

        assertThrows(IllegalStateException.class, () -> modelo.buscarRegistros(0, 10));

        modelo.setFiltro("nombre", "Central");
        assertThrows(IllegalArgumentException.class, () -> modelo.buscarRegistros(0, 10));
    }

    // ==========================================
    // PRUEBAS PARA obtenerRegistros
    // ==========================================

    @Test
    @DisplayName("obtenerRegistros - Éxito (Carga la lista de registros desde el DAO)")
    void obtenerRegistros_Success() {
        List<Clinica> lista = List.of(sampleClinica);
        when(daoMock.findRange(0, 10)).thenReturn(lista);

        modelo.obtenerRegistros(0, 10);

        assertSame(lista, modelo.getRegistros());
    }

    @Test
    @DisplayName("obtenerRegistros - Fallos (La excepción del DAO se propaga y no hay registros)")
    void obtenerRegistros_Failures() {
        when(daoMock.findRange(0, 10)).thenThrow(new IllegalStateException("Query failed"));

        assertThrows(IllegalStateException.class, () -> modelo.obtenerRegistros(0, 10));

        assertNull(modelo.getRegistros());
    }

    // ==========================================
    // PRUEBAS PARA el reinicio de inputs (resetInputs, privado, se ejecuta desde nuevo)
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito reinicia los inputs del panel pnlCrear")
    void resetInputs_Success() {
        UIComponent panel = mock(UIComponent.class);
        UIInput input = mock(UIInput.class);
        when(panel.getId()).thenReturn("pnlCrear");
        when(panel.getFacetsAndChildren()).thenAnswer(inv -> List.<UIComponent>of(input).iterator());
        when(input.getFacetsAndChildren()).thenAnswer(inv -> Collections.<UIComponent>emptyIterator());
        faces.agregarHijoALaRaiz(panel);

        modelo.nuevo();

        verify(input, times(1)).resetValue();
    }

    @Test
    @DisplayName("nuevo - Fallos: si no existe el panel pnlCrear no reinicia nada ni lanza error")
    void resetInputs_Failures() {
        UIComponent otroPanel = mock(UIComponent.class);
        UIInput input = mock(UIInput.class);
        when(otroPanel.getId()).thenReturn("pnlOtro");
        when(otroPanel.getFacetsAndChildren()).thenAnswer(inv -> List.<UIComponent>of(input).iterator());
        when(input.getFacetsAndChildren()).thenAnswer(inv -> Collections.<UIComponent>emptyIterator());
        faces.agregarHijoALaRaiz(otroPanel);

        assertDoesNotThrow(() -> modelo.nuevo());

        verify(input, never()).resetValue();
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
    }

    // ==========================================
    // PRUEBAS PARA getters y setters
    // ==========================================

    @Test
    @DisplayName("getters y setters - Éxito")
    void gettersAndSetters_Success() {
        modelo.setRegistroActual(sampleClinica);
        modelo.setEstado(ESTADO_CRUD.CREAR);

        assertSame(sampleClinica, modelo.getRegistroActual());
        assertEquals(ESTADO_CRUD.CREAR, modelo.getEstado());
        assertEquals(Clinica.class, modelo.getEntity());
        assertNull(modelo.getRegistros());
    }
}
