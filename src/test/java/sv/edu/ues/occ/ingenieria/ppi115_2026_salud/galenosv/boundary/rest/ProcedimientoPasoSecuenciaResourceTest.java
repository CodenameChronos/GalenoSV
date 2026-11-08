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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaResourceTest {

    @Mock
    private ProcedimientoPasoSecuenciaService secuenciaServiceMock;

    @Mock
    private ProcedimientoPasoService pasoServiceMock;

    @Mock
    private ProcedimientoService procedimientoServiceMock;

    @Mock
    private Validator validatorMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ProcedimientoPasoSecuenciaResource recurso;

    private UUID idProcedimiento;
    private UUID idPasoPadre;
    private UUID idPasoHijo;
    private UUID idSecuencia;

    private Procedimiento procedimiento;
    private ProcedimientoPaso pasoPadre;
    private ProcedimientoPaso pasoHijo;
    private ProcedimientoPasoSecuencia secuencia;

    @BeforeEach
    void setUp() {
        idProcedimiento = UUID.randomUUID();
        idPasoPadre = UUID.randomUUID();
        idPasoHijo = UUID.randomUUID();
        idSecuencia = UUID.randomUUID();

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);

        pasoPadre = new ProcedimientoPaso();
        pasoPadre.setIdProcedimientoPaso(idPasoPadre);
        pasoPadre.setIdProcedimiento(procedimiento);
        pasoPadre.setIndicaFin(false);

        pasoHijo = new ProcedimientoPaso();
        pasoHijo.setIdProcedimientoPaso(idPasoHijo);
        pasoHijo.setIdProcedimiento(procedimiento);

        secuencia = new ProcedimientoPasoSecuencia();
        secuencia.setIdProcedimientoPasoSecuencia(idSecuencia);
        secuencia.setIdProcedimientoPasoReferencia(idPasoPadre);
        secuencia.setIdProcedimientoPaso(pasoHijo);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(pasoServiceMock.buscar(idPasoHijo)).thenReturn(pasoHijo);
        when(pasoServiceMock.buscar(idPasoPadre)).thenReturn(pasoPadre);
        when(pasoServiceMock.contarHijos(any(UUID.class))).thenReturn(0L); // <-- Solución al NullPointerException
        when(secuenciaServiceMock.listarPorProcedimiento(idProcedimiento)).thenReturn(Collections.emptyList());
        when(validatorMock.validate(any(ProcedimientoPasoSecuencia.class))).thenReturn(Collections.emptySet());
        doNothing().when(secuenciaServiceMock).crear(any(ProcedimientoPasoSecuencia.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/secuencia/" + idSecuencia));

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(secuenciaServiceMock, times(1)).crear(secuencia);
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Cuerpo nulo o incompleto (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = recurso.create(uriInfoMock, idProcedimiento, null);

        ProcedimientoPasoSecuencia secSinPadre = new ProcedimientoPasoSecuencia();
        secSinPadre.setIdProcedimientoPasoReferencia(null);
        secSinPadre.setIdProcedimientoPaso(pasoHijo);
        Response r2 = recurso.create(uriInfoMock, idProcedimiento, secSinPadre);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("secuencia", r1.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Procedimiento no encontrado (Retorna 404)")
    void create_ProcedimientoNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(pasoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Hijo no encontrado o ajeno (Retorna 404)")
    void create_HijoNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(pasoServiceMock.buscar(idPasoHijo)).thenReturn(null);

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(404, response.getStatus());
        assertEquals(idPasoHijo.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Padre no encontrado o ajeno (Retorna 404)")
    void create_PadreNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(pasoServiceMock.buscar(idPasoHijo)).thenReturn(pasoHijo);
        when(pasoServiceMock.buscar(idPasoPadre)).thenReturn(null);

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(404, response.getStatus());
        assertEquals(idPasoPadre.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Conflicto: Padre indica fin de rama (Retorna 409)")
    void create_ConflictPadreFin_Returns409() throws Exception {
        pasoPadre.setIndicaFin(true);
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(pasoServiceMock.buscar(idPasoHijo)).thenReturn(pasoHijo);
        when(pasoServiceMock.buscar(idPasoPadre)).thenReturn(pasoPadre);
        when(validatorMock.validate(any(ProcedimientoPasoSecuencia.class))).thenReturn(Collections.emptySet());

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(409, response.getStatus());
        assertTrue(response.getEntity().toString().contains("indica fin de rama"));
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Fallo por validación de Bean Validation (Retorna 422)")
    void create_ValidationError_Returns422() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(pasoServiceMock.buscar(idPasoHijo)).thenReturn(pasoHijo);
        when(pasoServiceMock.buscar(idPasoPadre)).thenReturn(pasoPadre);

        ConstraintViolation<ProcedimientoPasoSecuencia> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("tipoSecuencia");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe ser nulo");

        Set<ConstraintViolation<ProcedimientoPasoSecuencia>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ProcedimientoPasoSecuencia.class))).thenReturn(violaciones);

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(422, response.getStatus());
        assertNotNull(response.getEntity());
        verify(secuenciaServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /procedimiento/{id}/secuencia - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenThrow(new RuntimeException("Error en BD"));

        Response response = recurso.create(uriInfoMock, idProcedimiento, secuencia);

        assertEquals(500, response.getStatus());
        assertEquals("Error en BD", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia/{idSecuencia} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);

        Response response = recurso.findById(idProcedimiento, idSecuencia);

        assertEquals(200, response.getStatus());
        assertEquals(secuencia, response.getEntity());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia/{idSecuencia} - ID nulo (Retorna 422)")
    void findById_NullId_Returns422() {
        Response response = recurso.findById(idProcedimiento, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(secuenciaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia/{idSecuencia} - No encontrado o de otro procedimiento (Retorna 404)")
    void findById_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        UUID otroProcId = UUID.randomUUID();
        Procedimiento otroProc = new Procedimiento();
        otroProc.setIdProcedimiento(otroProcId);

        ProcedimientoPaso pasoAjeno = new ProcedimientoPaso();
        pasoAjeno.setIdProcedimiento(otroProc);

        ProcedimientoPasoSecuencia secAjena = new ProcedimientoPasoSecuencia();
        secAjena.setIdProcedimientoPaso(pasoAjeno);

        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(null);
        Response r1 = recurso.findById(idProcedimiento, idSecuencia);

        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secAjena);
        Response r2 = recurso.findById(idProcedimiento, idSecuencia);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idSecuencia.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idSecuencia.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia/{idSecuencia} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(secuenciaServiceMock.buscar(idSecuencia)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = recurso.findById(idProcedimiento, idSecuencia);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia - Éxito con rango y conteo Integer por criterio (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<ProcedimientoPasoSecuencia> lista = List.of(secuencia);
        String propiedad = "idProcedimientoPaso.idProcedimiento.idProcedimiento";

        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        when(secuenciaServiceMock.findRangeByCriterio(eq(propiedad), eq(idProcedimiento), eq(0), eq(10))).thenReturn(lista);
        when(secuenciaServiceMock.contarByCriterio(eq(propiedad), eq(idProcedimiento))).thenReturn(1);

        Response response = recurso.findByRange(idProcedimiento, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = recurso.findByRange(idProcedimiento, -1, 10);
        Response r2 = recurso.findByRange(idProcedimiento, 0, 0);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia - Procedimiento padre no encontrado (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = recurso.findByRange(idProcedimiento, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id}/secuencia - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = recurso.findByRange(idProcedimiento, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA PUT (update)
    // ==========================================

    @Test
    @DisplayName("PUT /procedimiento/{id}/secuencia/{idSecuencia} - Éxito (Retorna 204 No Content)")
    void update_Success() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);
        when(validatorMock.validate(any(ProcedimientoPasoSecuencia.class))).thenReturn(Collections.emptySet());
        doNothing().when(secuenciaServiceMock).actualizar(any(ProcedimientoPasoSecuencia.class));

        Response response = recurso.update(idProcedimiento, idSecuencia, secuencia);

        assertEquals(204, response.getStatus());
        verify(secuenciaServiceMock, times(1)).actualizar(secuencia);
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/secuencia/{idSecuencia} - Cuerpo nulo (Retorna 422)")
    void update_NullBody_Returns422() {
        Response response = recurso.update(idProcedimiento, idSecuencia, null);

        assertEquals(422, response.getStatus());
        assertEquals("secuencia", response.getHeaderString("Wrong-Param"));
        verify(secuenciaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/secuencia/{idSecuencia} - No encontrado o de otro procedimiento (Retorna 404)")
    void update_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(null);

        Response response = recurso.update(idProcedimiento, idSecuencia, secuencia);

        assertEquals(404, response.getStatus());
        assertEquals(idSecuencia.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(secuenciaServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/secuencia/{idSecuencia} - Fallo por validación (Retorna 422)")
    void update_ValidationError_Returns422() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);

        ConstraintViolation<ProcedimientoPasoSecuencia> violationMock = mock(ConstraintViolation.class);
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("tipoSecuencia");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);
        when(violationMock.getMessage()).thenReturn("no debe estar vacío");

        Set<ConstraintViolation<ProcedimientoPasoSecuencia>> violaciones = Set.of(violationMock);
        when(validatorMock.validate(any(ProcedimientoPasoSecuencia.class))).thenReturn(violaciones);

        Response response = recurso.update(idProcedimiento, idSecuencia, secuencia);

        assertEquals(422, response.getStatus());
        verify(secuenciaServiceMock, never()).actualizar(any());
    }

    @Test
    @DisplayName("PUT /procedimiento/{id}/secuencia/{idSecuencia} - Excepción general (Retorna 500)")
    void update_Exception_Returns500() throws Exception {
        when(secuenciaServiceMock.buscar(idSecuencia)).thenThrow(new RuntimeException("Error en actualización"));

        Response response = recurso.update(idProcedimiento, idSecuencia, secuencia);

        assertEquals(500, response.getStatus());
        assertEquals("Error en actualización", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /procedimiento/{id}/secuencia/{idSecuencia} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);
        doNothing().when(secuenciaServiceMock).eliminar(secuencia);

        Response response = recurso.delete(idProcedimiento, idSecuencia);

        assertEquals(204, response.getStatus());
        verify(secuenciaServiceMock, times(1)).eliminar(secuencia);
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/secuencia/{idSecuencia} - No encontrado o de otro procedimiento (Retorna 404)")
    void delete_NotFoundOrDifferentProcedimiento_Returns404() throws Exception {
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(null);

        Response response = recurso.delete(idProcedimiento, idSecuencia);

        assertEquals(404, response.getStatus());
        assertEquals(idSecuencia.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(secuenciaServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/secuencia/{idSecuencia} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);
        doThrow(new ReglaNegocioException("La secuencia está protegida")).when(secuenciaServiceMock).eliminar(secuencia);

        Response response = recurso.delete(idProcedimiento, idSecuencia);

        assertEquals(409, response.getStatus());
        assertEquals("La secuencia está protegida", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id}/secuencia/{idSecuencia} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        secuencia.setIdProcedimientoPaso(pasoHijo);
        when(secuenciaServiceMock.buscar(idSecuencia)).thenReturn(secuencia);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(secuenciaServiceMock).eliminar(secuencia);

        Response response = recurso.delete(idProcedimiento, idSecuencia);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}