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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoExamenResourceTest {

    @Mock
    private TipoExamenService tipoExamenServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private TipoExamenResource tipoExamenResource;

    private UUID idTipoExamen;
    private TipoExamen tipoExamen;

    @BeforeEach
    void setUp() {
        idTipoExamen = UUID.randomUUID();
        tipoExamen = new TipoExamen();
        tipoExamen.setIdTipoExamen(idTipoExamen);
        tipoExamen.setNombre("Laboratorio Clínico");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /tipoExamen - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        doNothing().when(tipoExamenServiceMock).crear(any(TipoExamen.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/tipoExamen/" + idTipoExamen));

        Response response = tipoExamenResource.create(uriInfoMock, tipoExamen);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(tipoExamen.getActivo()); // Verifica asignación de activo = true por defecto
        verify(tipoExamenServiceMock, times(1)).crear(tipoExamen);
    }

    @Test
    @DisplayName("POST /tipoExamen - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = tipoExamenResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("tipoExamen", response.getHeaderString("Wrong-Param"));
        verify(tipoExamenServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /tipoExamen - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        doThrow(new RuntimeException("Error en BD")).when(tipoExamenServiceMock).crear(any());

        Response response = tipoExamenResource.create(uriInfoMock, tipoExamen);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /tipoExamen/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);

        Response response = tipoExamenResource.findById(idTipoExamen);

        assertEquals(200, response.getStatus());
        assertEquals(tipoExamen, response.getEntity());
    }

    @Test
    @DisplayName("GET /tipoExamen/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = tipoExamenResource.findById(null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Wrong-Param"));
        verify(tipoExamenServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /tipoExamen/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(null);

        Response response = tipoExamenResource.findById(idTipoExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /tipoExamen/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = tipoExamenResource.findById(idTipoExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /tipoExamen - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<TipoExamen> lista = List.of(tipoExamen);
        when(tipoExamenServiceMock.findRange(0, 10)).thenReturn(lista);
        when(tipoExamenServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = tipoExamenResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /tipoExamen - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = tipoExamenResource.findByRange(-1, 10); // first < 0
        Response r2 = tipoExamenResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(tipoExamenServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /tipoExamen - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(tipoExamenServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = tipoExamenResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /tipoExamen/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        doNothing().when(tipoExamenServiceMock).eliminar(tipoExamen);

        Response response = tipoExamenResource.delete(idTipoExamen);

        assertEquals(204, response.getStatus());
        verify(tipoExamenServiceMock, times(1)).eliminar(tipoExamen);
    }

    @Test
    @DisplayName("DELETE /tipoExamen/{id} - No encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(null);

        Response response = tipoExamenResource.delete(idTipoExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoExamenServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /tipoExamen/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        doThrow(new ReglaNegocioException("El tipo de examen está asociado a exámenes activos")).when(tipoExamenServiceMock).eliminar(tipoExamen);

        Response response = tipoExamenResource.delete(idTipoExamen);

        assertEquals(409, response.getStatus());
        assertEquals("El tipo de examen está asociado a exámenes activos", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /tipoExamen/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(tipoExamenServiceMock.buscar(idTipoExamen)).thenReturn(tipoExamen);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(tipoExamenServiceMock).eliminar(tipoExamen);

        Response response = tipoExamenResource.delete(idTipoExamen);

        assertEquals(500, response.getStatus());
    }
}