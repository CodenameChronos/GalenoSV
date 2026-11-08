package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

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
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoResourceTest {

    @Mock
    private ProcedimientoService procedimientoServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ProcedimientoResource procedimientoResource;

    private UUID idProcedimiento;
    private Procedimiento procedimiento;

    @BeforeEach
    void setUp() {
        idProcedimiento = UUID.randomUUID();
        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);
        procedimiento.setNombre("Procedimiento Quirúrgico Menor");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /procedimiento - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        doNothing().when(procedimientoServiceMock).crear(any(Procedimiento.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/procedimiento/" + idProcedimiento));

        Response response = procedimientoResource.create(uriInfoMock, procedimiento);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(procedimiento.getActivo()); // Verifica que asigne activo = true por defecto
        verify(procedimientoServiceMock, times(1)).crear(procedimiento);
    }

    @Test
    @DisplayName("POST /procedimiento - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = procedimientoResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("tdoc", response.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /procedimiento - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        doThrow(new RuntimeException("Error en BD")).when(procedimientoServiceMock).crear(any());

        Response response = procedimientoResource.create(uriInfoMock, procedimiento);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);

        Response response = procedimientoResource.findById(idProcedimiento);

        assertEquals(200, response.getStatus());
        assertEquals(procedimiento, response.getEntity());
    }

    @Test
    @DisplayName("GET /procedimiento/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = procedimientoResource.findById(null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /procedimiento/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = procedimientoResource.findById(idProcedimiento);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /procedimiento/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = procedimientoResource.findById(idProcedimiento);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /procedimiento - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Procedimiento> lista = List.of(procedimiento);
        when(procedimientoServiceMock.findRange(0, 10)).thenReturn(lista);
        when(procedimientoServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = procedimientoResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /procedimiento - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = procedimientoResource.findByRange(-1, 10); // first < 0
        Response r2 = procedimientoResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(procedimientoServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /procedimiento - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = procedimientoResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /procedimiento/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        doNothing().when(procedimientoServiceMock).eliminar(procedimiento);

        Response response = procedimientoResource.delete(idProcedimiento);

        assertEquals(204, response.getStatus());
        verify(procedimientoServiceMock, times(1)).eliminar(procedimiento);
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id} - No encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = procedimientoResource.delete(idProcedimiento);

        assertEquals(404, response.getStatus());
        assertEquals(idProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(procedimientoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        doThrow(new ReglaNegocioException("El procedimiento tiene pasos vinculados")).when(procedimientoServiceMock).eliminar(procedimiento);

        Response response = procedimientoResource.delete(idProcedimiento);

        assertEquals(409, response.getStatus());
        assertEquals("El procedimiento tiene pasos vinculados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /procedimiento/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(procedimientoServiceMock).eliminar(procedimiento);

        Response response = procedimientoResource.delete(idProcedimiento);

        assertEquals(500, response.getStatus());
    }
}