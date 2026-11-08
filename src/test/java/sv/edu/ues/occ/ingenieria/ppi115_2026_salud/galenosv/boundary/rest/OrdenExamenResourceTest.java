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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenExamenResourceTest {

    @Mock
    private OrdenExamenService oeServiceMock;

    @Mock
    private ConsultaProcedimientoPasoService cppServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private OrdenExamenResource ordenExamenResource;

    private UUID idConsultaProcedimientoPaso;
    private UUID idOrdenExamen;
    private ConsultaProcedimientoPaso cpp;
    private OrdenExamen ordenExamen;

    @BeforeEach
    void setUp() {
        idConsultaProcedimientoPaso = UUID.randomUUID();
        idOrdenExamen = UUID.randomUUID();

        cpp = new ConsultaProcedimientoPaso();
        cpp.setIdConsultaProcedimientoPaso(idConsultaProcedimientoPaso);

        ordenExamen = new OrdenExamen();
        ordenExamen.setIdOrdenExamen(idOrdenExamen);
        ordenExamen.setIdConsultaProcedimientoPaso(cpp);
        ordenExamen.setIndicaciones("Ayuno de 8 horas");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /consultaProcedimientoPaso/{id}/ordenExamen - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenReturn(cpp);
        doNothing().when(oeServiceMock).crear(any(OrdenExamen.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/ordenExamen/" + idOrdenExamen));

        Response response = ordenExamenResource.create(uriInfoMock, idConsultaProcedimientoPaso, ordenExamen);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(oeServiceMock, times(1)).crear(ordenExamen);
    }

    @Test
    @DisplayName("POST /consultaProcedimientoPaso/{id}/ordenExamen - Cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = ordenExamenResource.create(uriInfoMock, idConsultaProcedimientoPaso, null);

        assertEquals(422, response.getStatus());
        assertEquals("oe", response.getHeaderString("Wrong-Param"));
        verify(cppServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /consultaProcedimientoPaso/{id}/ordenExamen - Paso no encontrado (Retorna 404)")
    void create_CppNotFound_Returns404() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenReturn(null);

        Response response = ordenExamenResource.create(uriInfoMock, idConsultaProcedimientoPaso, ordenExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idConsultaProcedimientoPaso.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(oeServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /consultaProcedimientoPaso/{id}/ordenExamen - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void create_ReglaNegocioException_Returns409() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenReturn(cpp);
        doThrow(new ReglaNegocioException("Violación de regla de negocio")).when(oeServiceMock).crear(any());

        Response response = ordenExamenResource.create(uriInfoMock, idConsultaProcedimientoPaso, ordenExamen);

        assertEquals(409, response.getStatus());
        assertEquals("Violación de regla de negocio", response.getEntity());
    }

    @Test
    @DisplayName("POST /consultaProcedimientoPaso/{id}/ordenExamen - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenThrow(new RuntimeException("Error en BD"));

        Response response = ordenExamenResource.create(uriInfoMock, idConsultaProcedimientoPaso, ordenExamen);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);

        Response response = ordenExamenResource.findById(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(200, response.getStatus());
        assertEquals(ordenExamen, response.getEntity());
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - UUID de orden nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = ordenExamenResource.findById(idConsultaProcedimientoPaso, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(oeServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - No encontrado o de otro paso (Retorna 404)")
    void findById_NotFoundOrDifferentCpp_Returns404() throws Exception {
        UUID otroCppId = UUID.randomUUID();
        ConsultaProcedimientoPaso otroCpp = new ConsultaProcedimientoPaso();
        otroCpp.setIdConsultaProcedimientoPaso(otroCppId);

        OrdenExamen ordenAjena = new OrdenExamen();
        ordenAjena.setIdOrdenExamen(idOrdenExamen);
        ordenAjena.setIdConsultaProcedimientoPaso(otroCpp);

        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(null); // Caso nulo
        Response r1 = ordenExamenResource.findById(idConsultaProcedimientoPaso, idOrdenExamen);

        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenAjena); // Caso de otro paso
        Response r2 = ordenExamenResource.findById(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idOrdenExamen.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idOrdenExamen.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = ordenExamenResource.findById(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<OrdenExamen> lista = List.of(ordenExamen);
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenReturn(cpp);
        when(oeServiceMock.findRangeByConsultaProcedimientoPaso(idConsultaProcedimientoPaso, 0, 10)).thenReturn(lista);
        when(oeServiceMock.contarByConsultaProcedimientoPaso(idConsultaProcedimientoPaso)).thenReturn(1); // Conteo Integer

        Response response = ordenExamenResource.findByRange(idConsultaProcedimientoPaso, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = ordenExamenResource.findByRange(idConsultaProcedimientoPaso, -1, 10); // first < 0
        Response r2 = ordenExamenResource.findByRange(idConsultaProcedimientoPaso, 0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(cppServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen - Paso padre no encontrado (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenReturn(null);

        Response response = ordenExamenResource.findByRange(idConsultaProcedimientoPaso, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idConsultaProcedimientoPaso.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /consultaProcedimientoPaso/{id}/ordenExamen - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(cppServiceMock.buscar(idConsultaProcedimientoPaso)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = ordenExamenResource.findByRange(idConsultaProcedimientoPaso, 0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        doNothing().when(oeServiceMock).eliminar(ordenExamen);

        Response response = ordenExamenResource.delete(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(204, response.getStatus());
        verify(oeServiceMock, times(1)).eliminar(ordenExamen);
    }

    @Test
    @DisplayName("DELETE /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - No encontrado o de otro paso (Retorna 404)")
    void delete_NotFoundOrDifferentCpp_Returns404() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(null);

        Response response = ordenExamenResource.delete(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idOrdenExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(oeServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        doThrow(new ReglaNegocioException("La orden tiene resultados asociados")).when(oeServiceMock).eliminar(ordenExamen);

        Response response = ordenExamenResource.delete(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(409, response.getStatus());
        assertEquals("La orden tiene resultados asociados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /consultaProcedimientoPaso/{id}/ordenExamen/{idOrden} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(oeServiceMock.buscar(idOrdenExamen)).thenReturn(ordenExamen);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(oeServiceMock).eliminar(ordenExamen);

        Response response = ordenExamenResource.delete(idConsultaProcedimientoPaso, idOrdenExamen);

        assertEquals(500, response.getStatus());
    }
}