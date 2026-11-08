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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoResourceTest {

    @Mock
    private ProcedimientoPasoService procedimientoPasoServiceMock;

    @Mock
    private ProcedimientoService procedimientoServiceMock;

    @Mock
    private RolService rolServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ProcedimientoPasoResource procedimientoPasoResource;

    private UUID idProcedimiento;
    private UUID idPaso;
    private UUID idRol;

    private Procedimiento procedimiento;
    private Rol rol;
    private ProcedimientoPaso procedimientoPaso;

    @BeforeEach
    void setUp() {
        idProcedimiento = UUID.randomUUID();
        idPaso = UUID.randomUUID();
        idRol = UUID.randomUUID();

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);

        rol = new Rol();
        rol.setIdRol(idRol);

        procedimientoPaso = new ProcedimientoPaso();
        procedimientoPaso.setIdProcedimientoPaso(idPaso);
        procedimientoPaso.setIdProcedimiento(procedimiento);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        procedimientoPaso.setIdRol(rol);
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        when(validatorMock.validate(any(ProcedimientoPaso.class))).thenReturn(Collections.emptySet());
        doNothing().when(procedimientoPasoServiceMock).crear(any(ProcedimientoPaso.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/paso/" + idPaso));

        Response response = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, procedimientoPaso);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(procedimientoPasoServiceMock, times(1)).crear(procedimientoPaso);
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, null);

        assertEquals(422, response.getStatus());
        assertEquals("paso", response.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Procedimiento no encontrado (Retorna 404)")
    void create_ProcedimientoNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, procedimientoPaso);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(procedimientoPasoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Rol ID nulo o no encontrado (Retorna 422 o 404)")
    void create_InvalidRol_ReturnsError() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);

        // Caso 1: idRol interno nulo -> 422
        ProcedimientoPaso pasoRolNuloId = new ProcedimientoPaso();
        Rol rolSinId = new Rol();
        rolSinId.setIdRol(null);
        pasoRolNuloId.setIdRol(rolSinId);

        Response r1 = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, pasoRolNuloId);
        assertEquals(422, r1.getStatus());
        assertEquals("idRol", r1.getHeaderString("Wrong-Param"));

        // Caso 2: Rol no encontrado en BD -> 404
        procedimientoPaso.setIdRol(rol);
        when(rolServiceMock.buscar(idRol)).thenReturn(null);

        Response r2 = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, procedimientoPaso);
        assertEquals(404, r2.getStatus());
        assertEquals(idRol.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Fallo por validación de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);

        ConstraintViolation<ProcedimientoPaso> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("orden");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe ser nulo");

        Set<ConstraintViolation<ProcedimientoPaso>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ProcedimientoPaso.class))).thenReturn(violaciones);

        Response response = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, procedimientoPaso);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(procedimientoPasoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/paso - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenThrow(new RuntimeException("Error en BD"));

        Response response = procedimientoPasoResource.create(uriInfoMock, idProcedimiento, procedimientoPaso);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento/{id}/paso/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);

        Response response = procedimientoPasoResource.findById(idProcedimiento, idPaso);

        assertEquals(200, response.getStatus());
        assertEquals(procedimientoPaso, response.getEntity());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso/{id} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = procedimientoPasoResource.findById(idProcedimiento, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(procedimientoPasoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso/{id} - No encontrado o de otro procedimiento (Retorna 404)")
    void findById_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        UUID otroProcedimientoId = UUID.randomUUID();
        Procedimiento otroProcedimiento = new Procedimiento();
        otroProcedimiento.setIdProcedimiento(otroProcedimientoId);

        ProcedimientoPaso pasoAjeno = new ProcedimientoPaso();
        pasoAjeno.setIdProcedimientoPaso(idPaso);
        pasoAjeno.setIdProcedimiento(otroProcedimiento);

        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(null); // Caso nulo
        Response r1 = procedimientoPasoResource.findById(idProcedimiento, idPaso);

        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(pasoAjeno); // Caso de otro procedimiento
        Response r2 = procedimientoPasoResource.findById(idProcedimiento, idPaso);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idPaso.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idPaso.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = procedimientoPasoResource.findById(idProcedimiento, idPaso);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento/{id}/paso - Éxito con rango y conteo Integer por criterio (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<ProcedimientoPaso> lista = List.of(procedimientoPaso);
        String propiedad = "idProcedimiento.idProcedimiento";

        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(procedimientoPasoServiceMock.findRangeByCriterio(eq(propiedad), eq(idProcedimiento), eq(0), eq(10))).thenReturn(lista);
        when(procedimientoPasoServiceMock.contarByCriterio(eq(propiedad), eq(idProcedimiento))).thenReturn(1); // Conteo Integer

        Response response = procedimientoPasoResource.findByRange(idProcedimiento, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = procedimientoPasoResource.findByRange(idProcedimiento, -1, 10); // first < 0
        Response r2 = procedimientoPasoResource.findByRange(idProcedimiento, 0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso - Procedimiento padre no encontrado (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = procedimientoPasoResource.findByRange(idProcedimiento, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/paso - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = procedimientoPasoResource.findByRange(idProcedimiento, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /procedimiento/{id}/paso/{id} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);
        when(validatorMock.validate(any(ProcedimientoPaso.class))).thenReturn(Collections.emptySet());
        doNothing().when(procedimientoPasoServiceMock).actualizar(any(ProcedimientoPaso.class));

        Response response = procedimientoPasoResource.update(idProcedimiento, idPaso, procedimientoPaso);

        assertEquals(204, response.getStatus());
        verify(procedimientoPasoServiceMock, times(1)).actualizar(procedimientoPaso);
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/paso/{id} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = procedimientoPasoResource.update(idProcedimiento, idPaso, null);

        assertEquals(422, response.getStatus());
        assertEquals("paso", response.getHeaderString("Wrong-Param"));
        verify(procedimientoPasoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/paso/{id} - No encontrado o de otro procedimiento (Retorna 404)")
    void update_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(null);

        Response response = procedimientoPasoResource.update(idProcedimiento, idPaso, procedimientoPaso);

        assertEquals(404, response.getStatus());
        assertEquals(idPaso.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(procedimientoPasoServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/paso/{id} - Fallo por validación (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);

        ConstraintViolation<ProcedimientoPaso> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("indicaciones");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<ProcedimientoPaso>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ProcedimientoPaso.class))).thenReturn(violaciones);

        Response response = procedimientoPasoResource.update(idProcedimiento, idPaso, procedimientoPaso);

        assertEquals(422, response.getStatus());
        verify(procedimientoPasoServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/paso/{id} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = procedimientoPasoResource.update(idProcedimiento, idPaso, procedimientoPaso);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /procedimiento/{id}/paso/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);
        doNothing().when(procedimientoPasoServiceMock).eliminar(procedimientoPaso);

        Response response = procedimientoPasoResource.delete(idProcedimiento, idPaso);

        assertEquals(204, response.getStatus());
        verify(procedimientoPasoServiceMock, times(1)).eliminar(procedimientoPaso);
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/paso/{id} - No encontrado o de otro procedimiento (Retorna 404)")
    void delete_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(null);

        Response response = procedimientoPasoResource.delete(idProcedimiento, idPaso);

        assertEquals(404, response.getStatus());
        assertEquals(idPaso.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(procedimientoPasoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/paso/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);
        doThrow(new ReglaNegocioException("El paso tiene dependencias activas")).when(procedimientoPasoServiceMock).eliminar(procedimientoPaso);

        Response response = procedimientoPasoResource.delete(idProcedimiento, idPaso);

        assertEquals(409, response.getStatus());
        assertEquals("El paso tiene dependencias activas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/paso/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(procedimientoPasoServiceMock.buscar(idPaso)).thenReturn(procedimientoPaso);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(procedimientoPasoServiceMock).eliminar(procedimientoPaso);

        Response response = procedimientoPasoResource.delete(idProcedimiento, idPaso);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}