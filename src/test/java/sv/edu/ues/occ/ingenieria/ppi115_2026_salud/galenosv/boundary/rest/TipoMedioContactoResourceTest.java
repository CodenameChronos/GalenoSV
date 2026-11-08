package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoResourceTest {

    @Mock
    private TipoMedioContactoService tipoMedioContactoServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private TipoMedioContactoResource tipoMedioContactoResource;

    private UUID idTipoMedioContacto;
    private TipoMedioContacto tipoMedioContacto;

    @BeforeEach
    void setUp() {
        idTipoMedioContacto = UUID.randomUUID();
        tipoMedioContacto = new TipoMedioContacto();
        tipoMedioContacto.setIdTipoMedioContacto(idTipoMedioContacto);
        tipoMedioContacto.setNombre("Correo Electrónico");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /tipoMedioContacto - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(validatorMock.validate(any(TipoMedioContacto.class))).thenReturn(Collections.emptySet());
        doNothing().when(tipoMedioContactoServiceMock).crear(any(TipoMedioContacto.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/tipoMedioContacto/" + idTipoMedioContacto));

        Response response = tipoMedioContactoResource.create(uriInfoMock, tipoMedioContacto);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(tipoMedioContactoServiceMock, times(1)).crear(tipoMedioContacto);
    }

    @Test
    @DisplayName("POST /tipoMedioContacto - Cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = tipoMedioContactoResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("tipoMedioContacto", response.getHeaderString("Wrong-Param"));
        verify(validatorMock, never()).validate(any());
    }

    @Test
    @DisplayName("POST /tipoMedioContacto - Fallo por validación de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() {
        ConstraintViolation<TipoMedioContacto> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("nombre");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<TipoMedioContacto>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(TipoMedioContacto.class))).thenReturn(violaciones);

        Response response = tipoMedioContactoResource.create(uriInfoMock, tipoMedioContacto);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(tipoMedioContactoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /tipoMedioContacto - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(validatorMock.validate(any(TipoMedioContacto.class))).thenReturn(Collections.emptySet());
        doThrow(new RuntimeException("Error en BD")).when(tipoMedioContactoServiceMock).crear(any());

        Response response = tipoMedioContactoResource.create(uriInfoMock, tipoMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /tipoMedioContacto/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);

        Response response = tipoMedioContactoResource.findById(idTipoMedioContacto);

        assertEquals(200, response.getStatus());
        assertEquals(tipoMedioContacto, response.getEntity());
    }

    @Test
    @DisplayName("GET /tipoMedioContacto/{id} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = tipoMedioContactoResource.findById(null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(tipoMedioContactoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /tipoMedioContacto/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(null);

        Response response = tipoMedioContactoResource.findById(idTipoMedioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoMedioContacto.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /tipoMedioContacto/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = tipoMedioContactoResource.findById(idTipoMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /tipoMedioContacto - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<TipoMedioContacto> lista = List.of(tipoMedioContacto);
        when(tipoMedioContactoServiceMock.findRange(0, 10)).thenReturn(lista);
        when(tipoMedioContactoServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = tipoMedioContactoResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /tipoMedioContacto - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = tipoMedioContactoResource.findByRange(-1, 10); // first < 0
        Response r2 = tipoMedioContactoResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(tipoMedioContactoServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /tipoMedioContacto - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(tipoMedioContactoServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = tipoMedioContactoResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /tipoMedioContacto/{id} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);
        when(validatorMock.validate(any(TipoMedioContacto.class))).thenReturn(Collections.emptySet());
        doNothing().when(tipoMedioContactoServiceMock).actualizar(any(TipoMedioContacto.class));

        Response response = tipoMedioContactoResource.update(idTipoMedioContacto, tipoMedioContacto);

        assertEquals(204, response.getStatus());
        verify(tipoMedioContactoServiceMock, times(1)).actualizar(tipoMedioContacto);
    }

    @Test
    @DisplayName("PUT /tipoMedioContacto/{id} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = tipoMedioContactoResource.update(idTipoMedioContacto, null);

        assertEquals(422, response.getStatus());
        assertEquals("tipoMedioContacto", response.getHeaderString("Wrong-Param"));
        verify(tipoMedioContactoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /tipoMedioContacto/{id} - No encontrado (Retorna 404)")
    void update_NotFound() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(null);

        Response response = tipoMedioContactoResource.update(idTipoMedioContacto, tipoMedioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoMedioContacto.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoMedioContactoServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /tipoMedioContacto/{id} - Fallo por validación (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);

        ConstraintViolation<TipoMedioContacto> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("nombre");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<TipoMedioContacto>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(TipoMedioContacto.class))).thenReturn(violaciones);

        Response response = tipoMedioContactoResource.update(idTipoMedioContacto, tipoMedioContacto);

        assertEquals(422, response.getStatus());
        verify(tipoMedioContactoServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /tipoMedioContacto/{id} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = tipoMedioContactoResource.update(idTipoMedioContacto, tipoMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /tipoMedioContacto/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);
        doNothing().when(tipoMedioContactoServiceMock).eliminar(tipoMedioContacto);

        Response response = tipoMedioContactoResource.delete(idTipoMedioContacto);

        assertEquals(204, response.getStatus());
        verify(tipoMedioContactoServiceMock, times(1)).eliminar(tipoMedioContacto);
    }

    @Test
    @DisplayName("DELETE /tipoMedioContacto/{id} - No encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(null);

        Response response = tipoMedioContactoResource.delete(idTipoMedioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoMedioContacto.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoMedioContactoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /tipoMedioContacto/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);
        doThrow(new ReglaNegocioException("El tipo de medio tiene registros asociados")).when(tipoMedioContactoServiceMock).eliminar(tipoMedioContacto);

        Response response = tipoMedioContactoResource.delete(idTipoMedioContacto);

        assertEquals(409, response.getStatus());
        assertEquals("El tipo de medio tiene registros asociados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /tipoMedioContacto/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(tipoMedioContactoServiceMock.buscar(idTipoMedioContacto)).thenReturn(tipoMedioContacto);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(tipoMedioContactoServiceMock).eliminar(tipoMedioContacto);

        Response response = tipoMedioContactoResource.delete(idTipoMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}