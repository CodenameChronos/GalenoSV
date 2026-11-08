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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoResourceTest {

    @Mock
    private ExamenResultadoService erServiceMock;

    @Mock
    private OrdenExamenService oeServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ExamenResultadoResource examenResultadoResource;

    private UUID idOrdenExamen;
    private UUID idExamenResultado;
    private OrdenExamen ordenExamen;
    private ExamenResultado examenResultado;

    @BeforeEach
    void setUp() {
        idOrdenExamen = UUID.randomUUID();
        idExamenResultado = UUID.randomUUID();

        ordenExamen = new OrdenExamen();
        ordenExamen.setIdOrdenExamen(idOrdenExamen);

        examenResultado = new ExamenResultado();
        examenResultado.setIdExamenResultado(idExamenResultado);
        examenResultado.setIdOrdenExamen(ordenExamen);
        examenResultado.setResultado("Normal");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /ordenExamen/{id}/resultado - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        doNothing().when(erServiceMock).crear(any(ExamenResultado.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/resultado/" + idExamenResultado));

        Response response = examenResultadoResource.create(uriInfoMock, idOrdenExamen, examenResultado);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(erServiceMock, times(1)).crear(examenResultado);
    }

    @Test
    @DisplayName("POST /ordenExamen/{id}/resultado - Cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = examenResultadoResource.create(uriInfoMock, idOrdenExamen, null);

        assertEquals(422, response.getStatus());
        assertEquals("er", response.getHeaderString("Wrong-Param"));
        verify(oeServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /ordenExamen/{id}/resultado - Orden de examen no encontrada (Retorna 404)")
    void create_OrdenNotFound_Returns404() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(null);

        Response response = examenResultadoResource.create(uriInfoMock, idOrdenExamen, examenResultado);

        assertEquals(404, response.getStatus());
        assertEquals(idOrdenExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(erServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /ordenExamen/{id}/resultado - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void create_ReglaNegocioException_Returns409() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        doThrow(new ReglaNegocioException("La orden ya tiene un resultado registrado")).when(erServiceMock).crear(any());

        Response response = examenResultadoResource.create(uriInfoMock, idOrdenExamen, examenResultado);

        assertEquals(409, response.getStatus());
        assertEquals("La orden ya tiene un resultado registrado", response.getEntity());
    }

    @Test
    @DisplayName("POST /ordenExamen/{id}/resultado - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenThrow(new RuntimeException("Error en BD"));

        Response response = examenResultadoResource.create(uriInfoMock, idOrdenExamen, examenResultado);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado/{idResultado} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenReturn(examenResultado);

        Response response = examenResultadoResource.findById(idOrdenExamen, idExamenResultado);

        assertEquals(200, response.getStatus());
        assertEquals(examenResultado, response.getEntity());
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado/{idResultado} - UUID de resultado nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = examenResultadoResource.findById(idOrdenExamen, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(erServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado/{idResultado} - No encontrado o de otra orden (Retorna 404)")
    void findById_NotFoundOrDifferentOrden_Returns404() throws Exception {
        UUID otraOrdenId = UUID.randomUUID();
        OrdenExamen otraOrden = new OrdenExamen();
        otraOrden.setIdOrdenExamen(otraOrdenId);

        ExamenResultado resultadoAjeno = new ExamenResultado();
        resultadoAjeno.setIdExamenResultado(idExamenResultado);
        resultadoAjeno.setIdOrdenExamen(otraOrden);

        when(erServiceMock.buscar(idExamenResultado)).thenReturn(null); // Caso nulo
        Response r1 = examenResultadoResource.findById(idOrdenExamen, idExamenResultado);

        when(erServiceMock.buscar(idExamenResultado)).thenReturn(resultadoAjeno); // Caso de otra orden
        Response r2 = examenResultadoResource.findById(idOrdenExamen, idExamenResultado);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idExamenResultado.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idExamenResultado.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado/{idResultado} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = examenResultadoResource.findById(idOrdenExamen, idExamenResultado);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<ExamenResultado> lista = List.of(examenResultado);
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        when(erServiceMock.findRangeByOrdenExamen(idOrdenExamen, 0, 10)).thenReturn(lista);
        when(erServiceMock.contarByOrdenExamen(idOrdenExamen)).thenReturn(1); // Conteo tipo Integer

        Response response = examenResultadoResource.findByRange(idOrdenExamen, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = examenResultadoResource.findByRange(idOrdenExamen, -1, 10); // first < 0
        Response r2 = examenResultadoResource.findByRange(idOrdenExamen, 0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(oeServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado - Orden de examen padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(null);

        Response response = examenResultadoResource.findByRange(idOrdenExamen, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idOrdenExamen.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /ordenExamen/{id}/resultado - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = examenResultadoResource.findByRange(idOrdenExamen, 0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /ordenExamen/{id}/resultado/{idResultado} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenReturn(examenResultado);
        doNothing().when(erServiceMock).eliminar(examenResultado);

        Response response = examenResultadoResource.delete(idOrdenExamen, idExamenResultado);

        assertEquals(204, response.getStatus());
        verify(erServiceMock, times(1)).eliminar(examenResultado);
    }

    @Test
    @DisplayName("DELETE /ordenExamen/{id}/resultado/{idResultado} - No encontrado o de otra orden (Retorna 404)")
    void delete_NotFoundOrDifferentOrden_Returns404() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenReturn(null);

        Response response = examenResultadoResource.delete(idOrdenExamen, idExamenResultado);

        assertEquals(404, response.getStatus());
        assertEquals(idExamenResultado.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(erServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /ordenExamen/{id}/resultado/{idResultado} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenReturn(examenResultado);
        doThrow(new ReglaNegocioException("El resultado no se puede eliminar")).when(erServiceMock).eliminar(examenResultado);

        Response response = examenResultadoResource.delete(idOrdenExamen, idExamenResultado);

        assertEquals(409, response.getStatus());
        assertEquals("El resultado no se puede eliminar", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /ordenExamen/{id}/resultado/{idResultado} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(erServiceMock.buscar(idExamenResultado)).thenReturn(examenResultado);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(erServiceMock).eliminar(examenResultado);

        Response response = examenResultadoResource.delete(idOrdenExamen, idExamenResultado);

        assertEquals(500, response.getStatus());
    }
}