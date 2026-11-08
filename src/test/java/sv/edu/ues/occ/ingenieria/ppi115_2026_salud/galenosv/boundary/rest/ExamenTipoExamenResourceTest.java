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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenResourceTest {

    @Mock
    private ExamenTipoExamenService serviceMock;

    @Mock
    private ExamenService examenServiceMock;

    @Mock
    private TipoExamenService tipoExamenServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ExamenTipoExamenResource resource;

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
        relacion.setObservaciones("Observación de prueba");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        when(serviceMock.existeRelacion(idExamen, idTipoExamen)).thenReturn(false);
        when(validatorMock.validate(any(ExamenTipoExamen.class))).thenReturn(Collections.emptySet());
        doNothing().when(serviceMock).crear(any(ExamenTipoExamen.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/tipoExamen/" + idExamenTipoExamen));

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(serviceMock, times(1)).crear(relacion);
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Cuerpo nulo o sin tipo examen (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = resource.create(uriInfoMock, idExamen, null);
        
        ExamenTipoExamen relSinTipo = new ExamenTipoExamen();
        relSinTipo.setIdTipoExamen(null);
        Response r2 = resource.create(uriInfoMock, idExamen, relSinTipo);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("relacion", r1.getHeaderString("Wrong-Param"));
        verify(examenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Examen no encontrado (Retorna 404)")
    void create_ExamenNotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoExamenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - TipoExamen no encontrado (Retorna 404)")
    void create_TipoExamenNotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Relación ya existe (Retorna 409 Conflict)")
    void create_RelationExists_Returns409() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        when(serviceMock.existeRelacion(idExamen, idTipoExamen)).thenReturn(true);

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(409, response.getStatus());
        assertEquals("El examen ya tiene asignado ese tipo de examen.", response.getEntity());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Fallo por validaciones de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        when(serviceMock.existeRelacion(idExamen, idTipoExamen)).thenReturn(false);

        ConstraintViolation<ExamenTipoExamen> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("observaciones");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe ser nulo");

        Set<ConstraintViolation<ExamenTipoExamen>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ExamenTipoExamen.class))).thenReturn(violaciones);

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(serviceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/tipoExamen - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenThrow(new RuntimeException("Error en BD"));

        Response response = resource.create(uriInfoMock, idExamen, relacion);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);

        Response response = resource.findById(idExamen, idExamenTipoExamen);

        assertEquals(200, response.getStatus());
        assertEquals(relacion, response.getEntity());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen/{id} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = resource.findById(idExamen, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(serviceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen/{id} - No encontrado o de otro examen (Retorna 404)")
    void findById_NotFoundOrDifferentExamen_Returns404() throws Exception {
        UUID otroExamenId = UUID.randomUUID();
        Examen otroExamen = new Examen();
        otroExamen.setIdExamen(otroExamenId);

        ExamenTipoExamen relAjena = new ExamenTipoExamen();
        relAjena.setIdExamenTipoExamen(idExamenTipoExamen);
        relAjena.setIdExamen(otroExamen);

        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(null); // Caso nulo
        Response r1 = resource.findById(idExamen, idExamenTipoExamen);

        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relAjena); // Caso de otro examen
        Response r2 = resource.findById(idExamen, idExamenTipoExamen);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idExamenTipoExamen.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idExamenTipoExamen.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = resource.findById(idExamen, idExamenTipoExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<ExamenTipoExamen> lista = List.of(relacion);
        when(examenServiceMock.buscar(idExamen)).thenReturn(examen);
        when(serviceMock.findByIdExamen(eq(idExamen), eq(0), eq(10))).thenReturn(lista);
        when(serviceMock.contarPorIdExamen(idExamen)).thenReturn(1); // Conteo Integer

        Response response = resource.findByRange(idExamen, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = resource.findByRange(idExamen, -1, 10);
        Response r2 = resource.findByRange(idExamen, 0, 0);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(examenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen - Examen padre no encontrado (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = resource.findByRange(idExamen, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/tipoExamen - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(examenServiceMock.buscar(idExamen)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = resource.findByRange(idExamen, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /examen/{idExamen}/tipoExamen/{id} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);
        when(validatorMock.validate(any(ExamenTipoExamen.class))).thenReturn(Collections.emptySet());
        doNothing().when(serviceMock).actualizar(any(ExamenTipoExamen.class));

        Response response = resource.update(idExamen, idExamenTipoExamen, relacion);

        assertEquals(204, response.getStatus());
        verify(serviceMock, times(1)).actualizar(relacion);
    }

    @Test
    @DisplayName("PUT /examen/{idExamen}/tipoExamen/{id} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = resource.update(idExamen, idExamenTipoExamen, null);

        assertEquals(422, response.getStatus());
        assertEquals("relacion", response.getHeaderString("Wrong-Param"));
        verify(serviceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /examen/{idExamen}/tipoExamen/{id} - No encontrado o de otro examen (Retorna 404)")
    void update_NotFoundOrDifferentExamen_Returns404() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(null);

        Response response = resource.update(idExamen, idExamenTipoExamen, relacion);

        assertEquals(404, response.getStatus());
        assertEquals(idExamenTipoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(serviceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /examen/{idExamen}/tipoExamen/{id} - Fallo por validación (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);

        ConstraintViolation<ExamenTipoExamen> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("observaciones");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<ExamenTipoExamen>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ExamenTipoExamen.class))).thenReturn(violaciones);

        Response response = resource.update(idExamen, idExamenTipoExamen, relacion);

        assertEquals(422, response.getStatus());
        verify(serviceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /examen/{idExamen}/tipoExamen/{id} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = resource.update(idExamen, idExamenTipoExamen, relacion);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /examen/{idExamen}/tipoExamen/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);
        doNothing().when(serviceMock).eliminar(relacion);

        Response response = resource.delete(idExamen, idExamenTipoExamen);

        assertEquals(204, response.getStatus());
        verify(serviceMock, times(1)).eliminar(relacion);
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/tipoExamen/{id} - No encontrado o de otro examen (Retorna 404)")
    void delete_NotFoundOrDifferentExamen_Returns404() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(null);

        Response response = resource.delete(idExamen, idExamenTipoExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idExamenTipoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(serviceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/tipoExamen/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);
        doThrow(new ReglaNegocioException("La relación tiene dependencias")).when(serviceMock).eliminar(relacion);

        Response response = resource.delete(idExamen, idExamenTipoExamen);

        assertEquals(409, response.getStatus());
        assertEquals("La relación tiene dependencias", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/tipoExamen/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(serviceMock.buscar(idExamenTipoExamen)).thenReturn(relacion);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(serviceMock).eliminar(relacion);

        Response response = resource.delete(idExamen, idExamenTipoExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}