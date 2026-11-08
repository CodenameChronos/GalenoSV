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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoResourceTest {

    @Mock
    private ConsultaProcedimientoService conspServiceMock;

    @Mock
    private ConsultaService consultaServiceMock;

    @Mock
    private ProcedimientoService procedimientoServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ConsultaProcedimientoResource resource;

    private UUID idConsulta;
    private UUID idProcedimiento;
    private UUID idConsultaProcedimiento;

    private Consulta consulta;
    private Procedimiento procedimiento;
    private ConsultaProcedimiento consultaProcedimiento;

    @BeforeEach
    void setUp() {
        idConsulta = UUID.randomUUID();
        idProcedimiento = UUID.randomUUID();
        idConsultaProcedimiento = UUID.randomUUID();

        consulta = new Consulta();
        consulta.setIdConsulta(idConsulta);

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);

        consultaProcedimiento = new ConsultaProcedimiento();
        consultaProcedimiento.setIdConsultaProcedimiento(idConsultaProcedimiento);
        consultaProcedimiento.setIdConsulta(consulta);
        consultaProcedimiento.setIdProcedimiento(procedimiento);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        doNothing().when(conspServiceMock).crear(any(ConsultaProcedimiento.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/procedimiento/" + idConsultaProcedimiento));

        Response response = resource.create(uriInfoMock, idConsulta, consultaProcedimiento);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(conspServiceMock, times(1)).crear(consultaProcedimiento);
    }

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Cuerpo nulo o sin procedimiento (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = resource.create(uriInfoMock, idConsulta, null);
        Response r2 = resource.create(uriInfoMock, idConsulta, new ConsultaProcedimiento()); // sin idProcedimiento

        assertEquals(422, r1.getStatus());
        assertEquals("consp", r1.getHeaderString("Wrong-Param"));

        assertEquals(422, r2.getStatus());
        assertEquals("consp", r2.getHeaderString("Wrong-Param"));
        verify(consultaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Consulta no encontrada (Retorna 404)")
    void create_ConsultaNotFound_Returns404() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idConsulta, consultaProcedimiento);

        assertEquals(404, response.getStatus());
        assertEquals(idConsulta.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Procedimiento no encontrado (Retorna 404)")
    void create_ProcedimientoNotFound_Returns404() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idConsulta, consultaProcedimiento);

        assertEquals(404, response.getStatus());
        assertEquals("idProcedimiento", response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void create_ReglaNegocioException_Returns409() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        when(procedimientoServiceMock.buscar(idProcedimiento)).thenReturn(procedimiento);
        doThrow(new ReglaNegocioException("Violación de regla de negocio")).when(conspServiceMock).crear(any());

        Response response = resource.create(uriInfoMock, idConsulta, consultaProcedimiento);

        assertEquals(409, response.getStatus());
        assertEquals("Violación de regla de negocio", response.getEntity());
    }

    @Test
    @DisplayName("POST /consulta/{idConsulta}/procedimiento - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenThrow(new RuntimeException("Error de base de datos"));

        Response response = resource.create(uriInfoMock, idConsulta, consultaProcedimiento);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(consultaProcedimiento);

        Response response = resource.findById(idConsulta, idConsultaProcedimiento);

        assertEquals(200, response.getStatus());
        assertEquals(consultaProcedimiento, response.getEntity());
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = resource.findById(idConsulta, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento/{id} - No encontrado o de otra consulta (Retorna 404)")
    void findById_NotFoundOrDifferentParent_Returns404() throws Exception {
        UUID otraConsultaId = UUID.randomUUID();
        Consulta otraConsulta = new Consulta();
        otraConsulta.setIdConsulta(otraConsultaId);

        ConsultaProcedimiento ajeno = new ConsultaProcedimiento();
        ajeno.setIdConsulta(otraConsulta);

        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(null); // Caso nulo
        when(conspServiceMock.buscar(idProcedimiento)).thenReturn(ajeno);     // Caso de otra consulta

        Response r1 = resource.findById(idConsulta, idConsultaProcedimiento);
        Response r2 = resource.findById(idConsulta, idProcedimiento);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenThrow(new RuntimeException("Error"));

        Response response = resource.findById(idConsulta, idConsultaProcedimiento);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento - Éxito con rango (Retorna 200 y conteo Integer)")
    void findByRange_Success() throws Exception {
        List<ConsultaProcedimiento> lista = List.of(consultaProcedimiento);
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        when(conspServiceMock.listarPorConsulta(idConsulta, 0, 10)).thenReturn(lista);
        when(conspServiceMock.contarPorConsulta(idConsulta)).thenReturn(1L); // Conteo Integer

        Response response = resource.findByRange(idConsulta, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = resource.findByRange(idConsulta, -1, 10);
        Response r2 = resource.findByRange(idConsulta, 0, 0);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento - Consulta padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(null);

        Response response = resource.findByRange(idConsulta, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idConsulta.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /consulta/{idConsulta}/procedimiento - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenThrow(new RuntimeException("Error"));

        Response response = resource.findByRange(idConsulta, 0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /consulta/{idConsulta}/procedimiento/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(consultaProcedimiento);
        doNothing().when(conspServiceMock).eliminar(consultaProcedimiento);

        Response response = resource.delete(idConsulta, idConsultaProcedimiento);

        assertEquals(204, response.getStatus());
        verify(conspServiceMock, times(1)).eliminar(consultaProcedimiento);
    }

    @Test
    @DisplayName("DELETE /consulta/{idConsulta}/procedimiento/{id} - No encontrado o de otra consulta (Retorna 404)")
    void delete_NotFoundOrDifferentParent_Returns404() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(null);

        Response response = resource.delete(idConsulta, idConsultaProcedimiento);

        assertEquals(404, response.getStatus());
        assertEquals(idConsultaProcedimiento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(conspServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /consulta/{idConsulta}/procedimiento/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(consultaProcedimiento);
        doThrow(new ReglaNegocioException("Tiene dependencias activas")).when(conspServiceMock).eliminar(consultaProcedimiento);

        Response response = resource.delete(idConsulta, idConsultaProcedimiento);

        assertEquals(409, response.getStatus());
        assertEquals("Tiene dependencias activas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /consulta/{idConsulta}/procedimiento/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(conspServiceMock.buscar(idConsultaProcedimiento)).thenReturn(consultaProcedimiento);
        doThrow(new RuntimeException("Error inesperado")).when(conspServiceMock).eliminar(consultaProcedimiento);

        Response response = resource.delete(idConsulta, idConsultaProcedimiento);

        assertEquals(500, response.getStatus());
    }
}