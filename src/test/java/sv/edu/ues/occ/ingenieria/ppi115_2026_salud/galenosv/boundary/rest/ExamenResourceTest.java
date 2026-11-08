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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResourceTest {

    @Mock
    private ExamenService examenServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ExamenResource examenResource;

    private UUID idExamen;
    private Examen examen;

    @BeforeEach
    void setUp() {
        idExamen = UUID.randomUUID();
        examen = new Examen();
        examen.setIdExamen(idExamen);
        examen.setNombre("Hemograma Completo");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /examen - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(validatorMock.validate(any(Examen.class))).thenReturn(Collections.emptySet());
        doNothing().when(examenServiceMock).crear(any(Examen.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/examen/" + idExamen));

        Response response = examenResource.create(uriInfoMock, examen);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(examenServiceMock, times(1)).crear(examen);
    }

    @Test
    @DisplayName("POST /examen - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = examenResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("examen", response.getHeaderString("Wrong-Param"));
        verify(validatorMock, never()).validate(any());
    }

    @Test
    @DisplayName("POST /examen - Fallo por validaciones de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() {
        // Creamos un mock con los retornos explícitos para evitar NullPointerException en getPropertyPath() y getMessage()
        ConstraintViolation<Examen> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("nombre");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<Examen>> violaciones = new HashSet<>();
        violaciones.add(violationMock);

        when(validatorMock.validate(any(Examen.class))).thenReturn(violaciones);

        Response response = examenResource.create(uriInfoMock, examen);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(examenServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /examen - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(validatorMock.validate(any(Examen.class))).thenReturn(Collections.emptySet());
        doThrow(new RuntimeException("Error en BD")).when(examenServiceMock).crear(any());

        Response response = examenResource.create(uriInfoMock, examen);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /examen/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);

        Response response = examenResource.findById(idExamen);

        assertEquals(200, response.getStatus());
        assertEquals(examen, response.getEntity());
    }

    @Test
    @DisplayName("GET /examen/{id} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = examenResource.findById(null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(examenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /examen/{id} - No encontrado (Retorna 404)")
    void findById_NotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = examenResource.findById(idExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /examen/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = examenResource.findById(idExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /examen - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Examen> lista = List.of(examen);
        when(examenServiceMock.findRange(0, 10)).thenReturn(lista);
        when(examenServiceMock.contar()).thenReturn(1);

        Response response = examenResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /examen - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = examenResource.findByRange(-1, 10);
        Response r2 = examenResource.findByRange(0, 0);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(examenServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /examen - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(examenServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = examenResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /examen/{id} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(validatorMock.validate(any(Examen.class))).thenReturn(Collections.emptySet());
        doNothing().when(examenServiceMock).actualizar(any(Examen.class));

        Response response = examenResource.update(idExamen, examen);

        assertEquals(204, response.getStatus());
        verify(examenServiceMock, times(1)).actualizar(examen);
    }

    @Test
    @DisplayName("PUT /examen/{id} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = examenResource.update(idExamen, null);

        assertEquals(422, response.getStatus());
        assertEquals("examen", response.getHeaderString("Wrong-Param"));
        verify(examenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /examen/{id} - No encontrado (Retorna 404)")
    void update_NotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = examenResource.update(idExamen, examen);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(examenServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /examen/{id} - Fallo por validaciones (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);

        ConstraintViolation<Examen> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("nombre");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<Examen>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(Examen.class))).thenReturn(violaciones);

        Response response = examenResource.update(idExamen, examen);

        assertEquals(422, response.getStatus());
        verify(examenServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /examen/{id} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = examenResource.update(idExamen, examen);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /examen/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        doNothing().when(examenServiceMock).eliminar(examen);

        Response response = examenResource.delete(idExamen);

        assertEquals(204, response.getStatus());
        verify(examenServiceMock, times(1)).eliminar(examen);
    }

    @Test
    @DisplayName("DELETE /examen/{id} - No encontrado (Retorna 404)")
    void delete_NotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = examenResource.delete(idExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(examenServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /examen/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        doThrow(new ReglaNegocioException("El examen tiene órdenes asociadas")).when(examenServiceMock).eliminar(examen);

        Response response = examenResource.delete(idExamen);

        assertEquals(409, response.getStatus());
        assertEquals("El examen tiene órdenes asociadas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /examen/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(examenServiceMock).eliminar(examen);

        Response response = examenResource.delete(idExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}