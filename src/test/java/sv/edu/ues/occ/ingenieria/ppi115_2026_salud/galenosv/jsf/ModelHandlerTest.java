package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.event.ActionEvent;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;

public class ModelHandlerTest {

    @Mock
    private DAOInterface<String> dao;

    private ModelHandlerImpl handler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ModelHandlerImpl(String.class, dao);
    }

    @Test
    public void testNuevo() {
        handler.nuevo();

        assertEquals("Nuevo registro", handler.getRegistroActual());
        assertEquals(ESTADO_CRUD.CREAR, handler.getEstado());
    }

    @Test
    public void testInit() {
        List<String> registros = List.of("A", "B");

        when(dao.contar()).thenReturn(2);
        when(dao.findRange(0, 2)).thenReturn(registros);

        handler.init();

        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertEquals(registros, handler.getRegistros());
        assertEquals("Nuevo registro", handler.getRegistroActual());

        verify(dao).contar();
        verify(dao).findRange(0, 2);
    }

    @Test
    public void testSeleccionar() {
        handler.seleccionar("Registro existente");

        assertEquals("Registro existente", handler.getRegistroActual());
        assertEquals(ESTADO_CRUD.MODIFICAR, handler.getEstado());
    }

    @Test
    public void testGetRegistroActual() {
        handler.nuevo();

        assertEquals("Nuevo registro", handler.getRegistroActual());
    }

    @Test
    public void testEliminarHandler() {
        handler.seleccionar("Registro a eliminar");

        when(dao.contar()).thenReturn(1);
        when(dao.findRange(0, 1)).thenReturn(List.of("Otro registro"));

        handler.eliminarHandler(null);

        verify(dao).eliminar("Registro a eliminar");
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertEquals(List.of("Otro registro"), handler.getRegistros());
    }

    @Test
    public void testObtenerRegistros_int_int() {
        List<String> registros = List.of("A", "B", "C");

        when(dao.findRange(5, 10)).thenReturn(registros);

        handler.obtenerRegistros(5, 10);

        assertEquals(registros, handler.getRegistros());
        verify(dao).findRange(5, 10);
    }

    @Test
    public void testObtenerRegistros_0args() {
        List<String> registros = List.of("A", "B");

        when(dao.contar()).thenReturn(2);
        when(dao.findRange(0, 2)).thenReturn(registros);

        handler.obtenerRegistros();

        assertEquals(registros, handler.getRegistros());

        verify(dao).contar();
        verify(dao).findRange(0, 2);
    }

    @Test
    public void testSetEstado() {
        handler.setEstado(ESTADO_CRUD.MODIFICAR);

        assertEquals(ESTADO_CRUD.MODIFICAR, handler.getEstado());
    }

    @Test
    public void testGuardarHandler() {
        handler.nuevo();

        when(dao.contar()).thenReturn(1);
        when(dao.findRange(0, 1)).thenReturn(List.of("Nuevo registro"));

        handler.guardarHandler(null);

        verify(dao).crear("Nuevo registro");
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertEquals(List.of("Nuevo registro"), handler.getRegistros());
    }

    @Test
    public void testGuardarHandlerModificar() {
        handler.seleccionar("Registro existente");

        when(dao.contar()).thenReturn(1);
        when(dao.findRange(0, 1)).thenReturn(List.of("Registro existente"));

        handler.guardarHandler(null);

        verify(dao).actualizar("Registro existente");
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
        assertEquals(List.of("Registro existente"), handler.getRegistros());
    }

    @Test
    public void testGetEntity() {
        assertEquals(String.class, handler.getEntity());
    }

    @Test
    public void testGetEstado() {
        assertEquals(ESTADO_CRUD.NINGUNO, handler.getEstado());
    }

    @Test
    public void testGetRegistros() {
        List<String> registros = List.of("A", "B");

        when(dao.contar()).thenReturn(2);
        when(dao.findRange(0, 2)).thenReturn(registros);

        handler.obtenerRegistros();

        assertEquals(registros, handler.getRegistros());
    }

    @Test
    public void testContar() {
        when(dao.contar()).thenReturn(5);

        assertEquals(5, handler.contar());

        verify(dao).contar();
    }

    @Test
    public void testGetDAO() {
        assertSame(dao, handler.getDAO());
    }

    @Test
    public void testInstanciarRegistro() {
        assertEquals("Nuevo registro", handler.instanciarRegistro());
    }

    /**
     * Implementación concreta de ModelHandler utilizada únicamente para
     * realizar las pruebas unitarias.
     */
    private static class ModelHandlerImpl extends ModelHandler<String> {

        private final DAOInterface<String> dao;

        public ModelHandlerImpl(Class<String> entity, DAOInterface<String> dao) {
            super(entity);
            this.dao = dao;
        }

        @Override
        public DAOInterface<String> getDAO() {
            return dao;
        }

        @Override
        public String instanciarRegistro() {
            return "Nuevo registro";
        }
    }
}
