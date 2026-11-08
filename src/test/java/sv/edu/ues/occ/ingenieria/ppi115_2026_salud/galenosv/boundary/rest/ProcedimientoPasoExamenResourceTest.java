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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenResourceTest {

    @Mock
    private ProcedimientoPasoExamenService ppeServiceMock;

    @Mock
    private ProcedimientoPasoService ppServiceMock;

    @Mock
    private ExamenService exServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ProcedimientoPasoExamenResource recurso;

    private UUID idExamen;
    private UUID idPaso;
    private UUID idPasoExamen;

    private Examen examen;
    private ProcedimientoPaso paso;
    private ProcedimientoPasoExamen procedimientoPasoExamen;

    @BeforeEach
    void setUp() {
        idExamen = UUID.randomUUID();
        idPaso = UUID.randomUUID();
        idPasoExamen = UUID.randomUUID();

        examen = new Examen();
        examen.setIdExamen(idExamen);

        paso = new ProcedimientoPaso();
        paso.setIdProcedimientoPaso(idPaso);

        procedimientoPasoExamen = new ProcedimientoPasoExamen();
        procedimientoPasoExamen.setIdProcedimientoPasoExamen(idPasoExamen);
        procedimientoPasoExamen.setIdExamen(examen);
        procedimientoPasoExamen.setIdProcedimientoPaso(paso);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /examen/{idExamen}/paso - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(exServiceMock.buscar(idExamen)).thenReturn(examen);
        when(ppServiceMock.buscar(idPaso)).thenReturn(paso);
        doNothing().when(ppeServiceMock).crear(any(ProcedimientoPasoExamen.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/paso/" + idPasoExamen));

        Response response = recurso.create(uriInfoMock, idExamen, procedimientoPasoExamen);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(ppeServiceMock, times(1)).crear(procedimientoPasoExamen);
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/paso - Cuerpo nulo o sin paso (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = recurso.create(uriInfoMock, idExamen, null);

        ProcedimientoPasoExamen ppeSinPaso = new ProcedimientoPasoExamen();
        ppeSinPaso.setIdProcedimientoPaso(null);
        Response r2 = recurso.create(uriInfoMock, idExamen, ppeSinPaso);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("ppe", r1.getHeaderString("Wrong-Param"));
        verify(exServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/paso - Examen no encontrado (Retorna 404)")
    void create_ExamenNotFound_Returns404() throws Exception {
        when(exServiceMock.buscar(idExamen)).thenReturn(null);

        Response response = recurso.create(uriInfoMock, idExamen, procedimientoPasoExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(ppServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/paso - ProcedimientoPaso no encontrado (Retorna 404)")
    void create_PasoNotFound_Returns404() throws Exception {
        when(exServiceMock.buscar(idExamen)).thenReturn(examen);
        when(ppServiceMock.buscar(idPaso)).thenReturn(null);

        Response response = recurso.create(uriInfoMock, idExamen, procedimientoPasoExamen);

        assertEquals(404, response.getStatus());
        assertEquals("idPaso", response.getHeaderString("Entity-Not-Found"));
        verify(ppeServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /examen/{idExamen}/paso - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(exServiceMock.buscar(idExamen)).thenThrow(new RuntimeException("Error en BD"));

        Response response = recurso.create(uriInfoMock, idExamen, procedimientoPasoExamen);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /examen/{idExamen}/paso/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(procedimientoPasoExamen);

        Response response = recurso.findById(idExamen, idPasoExamen);

        assertEquals(200, response.getStatus());
        assertEquals(procedimientoPasoExamen, response.getEntity());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/paso/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = recurso.findById(idExamen, null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Entity-Not-Found"));
        verify(ppeServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/paso/{id} - No encontrado o de otro examen (Retorna 404)")
    void findById_NotFoundOrDifferentExamen_Returns404() throws Exception {
        UUID otroExamenId = UUID.randomUUID();
        Examen otroExamen = new Examen();
        otroExamen.setIdExamen(otroExamenId);

        ProcedimientoPasoExamen ajeno = new ProcedimientoPasoExamen();
        ajeno.setIdProcedimientoPasoExamen(idPasoExamen);
        ajeno.setIdExamen(otroExamen);

        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(null); // Caso nulo
        Response r1 = recurso.findById(idExamen, idPasoExamen);

        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(ajeno); // Caso de otro examen
        Response r2 = recurso.findById(idExamen, idPasoExamen);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idPasoExamen.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idPasoExamen.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/paso/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = recurso.findById(idExamen, idPasoExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /examen/{idExamen}/paso - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<ProcedimientoPasoExamen> lista = List.of(procedimientoPasoExamen);
        when(ppeServiceMock.findRange(0, 10)).thenReturn(lista);
        when(ppeServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = recurso.findByRange(idExamen, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/paso - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = recurso.findByRange(idExamen, -1, 10); // first < 0
        Response r2 = recurso.findByRange(idExamen, 0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(ppeServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /examen/{idExamen}/paso - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(ppeServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = recurso.findByRange(idExamen, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /examen/{idExamen}/paso/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(procedimientoPasoExamen);
        doNothing().when(ppeServiceMock).eliminar(procedimientoPasoExamen);

        Response response = recurso.delete(idExamen, idPasoExamen);

        assertEquals(204, response.getStatus());
        verify(ppeServiceMock, times(1)).eliminar(procedimientoPasoExamen);
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/paso/{id} - No encontrado o de otro examen (Retorna 404)")
    void delete_NotFoundOrDifferentExamen_Returns404() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(null);

        Response response = recurso.delete(idExamen, idPasoExamen);

        assertEquals(404, response.getStatus());
        assertEquals(idPasoExamen.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(ppeServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/paso/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(procedimientoPasoExamen);
        doThrow(new ReglaNegocioException("El registro tiene dependencias activas")).when(ppeServiceMock).eliminar(procedimientoPasoExamen);

        Response response = recurso.delete(idExamen, idPasoExamen);

        assertEquals(409, response.getStatus());
        assertEquals("El registro tiene dependencias activas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /examen/{idExamen}/paso/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(ppeServiceMock.buscar(idPasoExamen)).thenReturn(procedimientoPasoExamen);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(ppeServiceMock).eliminar(procedimientoPasoExamen);

        Response response = recurso.delete(idExamen, idPasoExamen);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}