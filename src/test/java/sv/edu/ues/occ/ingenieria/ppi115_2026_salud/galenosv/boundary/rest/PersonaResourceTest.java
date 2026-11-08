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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaResourceTest {

    @Mock
    private PersonaService personaServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private PersonaResource personaResource;

    private UUID idPersona;
    private Persona persona;

    @BeforeEach
    void setUp() {
        idPersona = UUID.randomUUID();
        persona = new Persona();
        persona.setIdPersona(idPersona);
        persona.setNombres("Juan");
        persona.setApellidos("Pérez");
        persona.setFechaCreacion(new Date());
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /persona - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(validatorMock.validate(any(Persona.class))).thenReturn(Collections.emptySet());
        doNothing().when(personaServiceMock).crear(any(Persona.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/persona/" + idPersona));

        Response response = personaResource.create(uriInfoMock, persona);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(personaServiceMock, times(1)).crear(persona);
    }

    @Test
    @DisplayName("POST /persona - Cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = personaResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("persona", response.getHeaderString("Wrong-Param"));
        verify(validatorMock, never()).validate(any());
    }

    @Test
    @DisplayName("POST /persona - Fallo por validación de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() {
        ConstraintViolation<Persona> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("nombres");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<Persona>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(Persona.class))).thenReturn(violaciones);

        Response response = personaResource.create(uriInfoMock, persona);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(personaServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /persona - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(validatorMock.validate(any(Persona.class))).thenReturn(Collections.emptySet());
        doThrow(new RuntimeException("Error en BD")).when(personaServiceMock).crear(any());

        Response response = personaResource.create(uriInfoMock, persona);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);

        Response response = personaResource.findById(idPersona);

        assertEquals(200, response.getStatus());
        assertEquals(persona, response.getEntity());
    }

    @Test
    @DisplayName("GET /persona/{id} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = personaResource.findById(null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{id} - No encontrado (Retorna 404)")
    void findById_NotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = personaResource.findById(idPersona);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = personaResource.findById(idPersona);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /persona - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Persona> lista = List.of(persona);
        when(personaServiceMock.findRange(0, 10)).thenReturn(lista);
        when(personaServiceMock.contar()).thenReturn(1); // Conteo Integer

        Response response = personaResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /persona - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = personaResource.findByRange(-1, 10); // first < 0
        Response r2 = personaResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /persona - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(personaServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = personaResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /persona/{id} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(validatorMock.validate(any(Persona.class))).thenReturn(Collections.emptySet());
        doNothing().when(personaServiceMock).actualizar(any(Persona.class));

        Response response = personaResource.update(idPersona, persona);

        assertEquals(204, response.getStatus());
        verify(personaServiceMock, times(1)).actualizar(persona);
    }

    @Test
    @DisplayName("PUT /persona/{id} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = personaResource.update(idPersona, null);

        assertEquals(422, response.getStatus());
        assertEquals("persona", response.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /persona/{id} - No encontrado (Retorna 404)")
    void update_NotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = personaResource.update(idPersona, persona);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(personaServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /persona/{id} - Fallo por validación (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);

        ConstraintViolation<Persona> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("apellidos");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<Persona>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(Persona.class))).thenReturn(violaciones);

        Response response = personaResource.update(idPersona, persona);

        assertEquals(422, response.getStatus());
        verify(personaServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /persona/{id} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = personaResource.update(idPersona, persona);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /persona/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        doNothing().when(personaServiceMock).eliminar(persona);

        Response response = personaResource.delete(idPersona);

        assertEquals(204, response.getStatus());
        verify(personaServiceMock, times(1)).eliminar(persona);
    }

    @Test
    @DisplayName("DELETE /persona/{id} - No encontrado (Retorna 404)")
    void delete_NotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = personaResource.delete(idPersona);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(personaServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /persona/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        doThrow(new ReglaNegocioException("La persona tiene documentos vinculados")).when(personaServiceMock).eliminar(persona);

        Response response = personaResource.delete(idPersona);

        assertEquals(409, response.getStatus());
        assertEquals("La persona tiene documentos vinculados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /persona/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(personaServiceMock).eliminar(persona);

        Response response = personaResource.delete(idPersona);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}