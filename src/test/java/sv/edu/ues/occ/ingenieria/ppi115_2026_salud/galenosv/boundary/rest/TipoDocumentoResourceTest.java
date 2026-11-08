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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoDocumentoResourceTest {

    @Mock
    private TipoDocumentoService tipoDocumentoServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private TipoDocumentoResource tipoDocumentoResource;

    private UUID idTipoDocumento;
    private TipoDocumento tipoDocumento;

    @BeforeEach
    void setUp() {
        idTipoDocumento = UUID.randomUUID();
        tipoDocumento = new TipoDocumento();
        tipoDocumento.setIdTipoDocumento(idTipoDocumento);
        tipoDocumento.setNombre("DUI");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /tipoDocumento - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        doNothing().when(tipoDocumentoServiceMock).crear(any(TipoDocumento.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/tipoDocumento/" + idTipoDocumento));

        Response response = tipoDocumentoResource.create(uriInfoMock, tipoDocumento);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(tipoDocumento.getActivo()); // Verifica que asigne activo = true por defecto
        verify(tipoDocumentoServiceMock, times(1)).crear(tipoDocumento);
    }

    @Test
    @DisplayName("POST /tipoDocumento - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = tipoDocumentoResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("tdoc", response.getHeaderString("Wrong-Param"));
        verify(tipoDocumentoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /tipoDocumento - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        doThrow(new RuntimeException("Error en BD")).when(tipoDocumentoServiceMock).crear(any());

        Response response = tipoDocumentoResource.create(uriInfoMock, tipoDocumento);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /tipoDocumento/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(tipoDocumento);

        Response response = tipoDocumentoResource.findById(idTipoDocumento);

        assertEquals(200, response.getStatus());
        assertEquals(tipoDocumento, response.getEntity());
    }

    @Test
    @DisplayName("GET /tipoDocumento/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = tipoDocumentoResource.findById(null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Wrong-Param"));
        verify(tipoDocumentoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /tipoDocumento/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(null);

        Response response = tipoDocumentoResource.findById(idTipoDocumento);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoDocumento.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /tipoDocumento/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = tipoDocumentoResource.findById(idTipoDocumento);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /tipoDocumento - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<TipoDocumento> lista = List.of(tipoDocumento);
        when(tipoDocumentoServiceMock.findRange(0, 10)).thenReturn(lista);
        when(tipoDocumentoServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = tipoDocumentoResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /tipoDocumento - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = tipoDocumentoResource.findByRange(-1, 10); // first < 0
        Response r2 = tipoDocumentoResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(tipoDocumentoServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /tipoDocumento - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(tipoDocumentoServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = tipoDocumentoResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /tipoDocumento/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(tipoDocumento);
        doNothing().when(tipoDocumentoServiceMock).eliminar(tipoDocumento);

        Response response = tipoDocumentoResource.delete(idTipoDocumento);

        assertEquals(204, response.getStatus());
        verify(tipoDocumentoServiceMock, times(1)).eliminar(tipoDocumento);
    }

    @Test
    @DisplayName("DELETE /tipoDocumento/{id} - No encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(null);

        Response response = tipoDocumentoResource.delete(idTipoDocumento);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoDocumento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoDocumentoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /tipoDocumento/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(tipoDocumento);
        doThrow(new ReglaNegocioException("El tipo de documento tiene registros asociados")).when(tipoDocumentoServiceMock).eliminar(tipoDocumento);

        Response response = tipoDocumentoResource.delete(idTipoDocumento);

        assertEquals(409, response.getStatus());
        assertEquals("El tipo de documento tiene registros asociados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /tipoDocumento/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(tipoDocumentoServiceMock.buscar(idTipoDocumento)).thenReturn(tipoDocumento);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(tipoDocumentoServiceMock).eliminar(tipoDocumento);

        Response response = tipoDocumentoResource.delete(idTipoDocumento);

        assertEquals(500, response.getStatus());
    }
}