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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoResourceTest {

    @Mock
    private ConsultaProcedimientoPasoService consppServiceMock;

    @Mock
    private ConsultaProcedimientoService conspServiceMock;

    @Mock
    private ProcedimientoPasoService ppServiceMock;

    @Mock
    private PersonaRolService personaRolServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ConsultaProcedimientoPasoResource resource;

    private UUID idConsultaProc;
    private UUID idPasoPaso;
    private UUID idPersonaRol;
    private UUID idConsPaso;

    private ConsultaProcedimiento consultaProc;
    private Procedimiento procedimiento;
    private ProcedimientoPaso paso;
    private ConsultaProcedimientoPaso consPaso;
    private PersonaRol personaRol;

    @BeforeEach
    void setUp() {
        idConsultaProc = UUID.randomUUID();
        idPasoPaso = UUID.randomUUID();
        idPersonaRol = UUID.randomUUID();
        idConsPaso = UUID.randomUUID();

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(UUID.randomUUID());

        consultaProc = new ConsultaProcedimiento();
        consultaProc.setIdConsultaProcedimiento(idConsultaProc);
        consultaProc.setIdProcedimiento(procedimiento);

        paso = new ProcedimientoPaso();
        paso.setIdProcedimientoPaso(idPasoPaso);
        paso.setIdProcedimiento(procedimiento);

        personaRol = new PersonaRol();
        personaRol.setIdPersonaRol(idPersonaRol);

        consPaso = new ConsultaProcedimientoPaso();
        consPaso.setIdConsultaProcedimientoPaso(idConsPaso);
        consPaso.setIdProcedimientoPaso(paso);
        consPaso.setIdConsultaProcedimiento(consultaProc);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================
    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Éxito con personaRol opcional")
    void create_Success() throws Exception {
        consPaso.setIdPersonaRol(personaRol);

        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(paso);
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        doNothing().when(consppServiceMock).crear(any(ConsultaProcedimientoPaso.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/paso/" + idConsPaso));

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(consppServiceMock, times(1)).crear(consPaso);
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Fallo cuerpo nulo o sin paso (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = resource.create(uriInfoMock, idConsultaProc, null);
        Response r2 = resource.create(uriInfoMock, idConsultaProc, new ConsultaProcedimientoPaso());

        assertEquals(422, r1.getStatus());
        assertEquals("conspp", r1.getHeaderString("Wrong-Param"));

        assertEquals(422, r2.getStatus());
        assertEquals("conspp", r2.getHeaderString("Wrong-Param"));
        verify(conspServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Consulta padre no encontrada (Retorna 404)")
    void create_ConsultaNotFound_Returns404() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(404, response.getStatus());
        assertEquals(idConsultaProc.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Paso no encontrado (Retorna 404)")
    void create_PasoNotFound_Returns404() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(null);

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(404, response.getStatus());
        assertEquals("idProcedimientoPaso", response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - El paso no pertenece al procedimiento (Retorna 409 Conflict)")
    void create_PasoDoesNotBelongToProcedure_Returns409() throws Exception {
        Procedimiento otroProcedimiento = new Procedimiento();
        otroProcedimiento.setIdProcedimiento(UUID.randomUUID());

        ProcedimientoPaso pasoAjeno = new ProcedimientoPaso();
        pasoAjeno.setIdProcedimientoPaso(idPasoPaso);
        pasoAjeno.setIdProcedimiento(otroProcedimiento);

        consPaso.setIdProcedimientoPaso(pasoAjeno);

        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(pasoAjeno);

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(409, response.getStatus());
        assertEquals("El paso no pertenece al procedimiento aplicado en la consulta", response.getEntity());
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - PersonaRol con ID nulo (Retorna 422)")
    void create_PersonaRolNullId_Returns422() throws Exception {
        PersonaRol rolInvalido = new PersonaRol();
        rolInvalido.setIdPersonaRol(null);
        consPaso.setIdPersonaRol(rolInvalido);

        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(paso);

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(422, response.getStatus());
        assertEquals("idPersonaRol", response.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - PersonaRol no encontrada (Retorna 404)")
    void create_PersonaRolNotFound_Returns404() throws Exception {
        consPaso.setIdPersonaRol(personaRol);

        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(paso);
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(null); // Retorna null para disparar el 404

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(404, response.getStatus());
        assertEquals("idPersonaRol", response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Excepción ReglaNegocio (Retorna 409)")
    void create_ReglaNegocioException_Returns409() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(ppServiceMock.buscar(idPasoPaso)).thenReturn(paso);
        doThrow(new ReglaNegocioException("Regla violada")).when(consppServiceMock).crear(any());

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(409, response.getStatus());
        assertEquals("Regla violada", response.getEntity());
    }

    @Test
    @DisplayName("POST /consultaProcedimiento/{id}/paso - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenThrow(new RuntimeException("DB error"));

        Response response = resource.create(uriInfoMock, idConsultaProc, consPaso);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================
    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(consPaso);

        Response response = resource.findById(idConsultaProc, idConsPaso);

        assertEquals(200, response.getStatus());
        assertEquals(consPaso, response.getEntity());
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = resource.findById(idConsultaProc, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso/{id} - No encontrado o de otro procedimiento (Retorna 404)")
    void findById_NotFoundOrDifferentParent_Returns404() throws Exception {
        UUID otroIdPadre = UUID.randomUUID();
        ConsultaProcedimiento otroPadre = new ConsultaProcedimiento();
        otroPadre.setIdConsultaProcedimiento(otroIdPadre);

        ConsultaProcedimientoPaso pasoAjeno = new ConsultaProcedimientoPaso();
        pasoAjeno.setIdConsultaProcedimiento(otroPadre);

        // Mock para cuando no existe (retorna null)
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(null);
        // Mock para cuando pertenece a otro padre distinto al de la ruta
        when(consppServiceMock.buscar(idPasoPaso)).thenReturn(pasoAjeno);

        Response r1 = resource.findById(idConsultaProc, idConsPaso);
        Response r2 = resource.findById(idConsultaProc, idPasoPaso);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenThrow(new RuntimeException("Error"));

        Response response = resource.findById(idConsultaProc, idConsPaso);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================
    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso - Éxito con rango (Retorna 200 y conteo Integer)")
    void findByRange_Success() throws Exception {
        List<ConsultaProcedimientoPaso> lista = List.of(consPaso);
        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(consultaProc);
        when(consppServiceMock.listarPorConsultaProcedimiento(idConsultaProc, 0, 10)).thenReturn(lista);
        when(consppServiceMock.contarPorConsultaProcedimiento(idConsultaProc)).thenReturn(1);

        Response response = resource.findByRange(idConsultaProc, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = resource.findByRange(idConsultaProc, -1, 10);
        Response r2 = resource.findByRange(idConsultaProc, 0, 0);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso - Consulta padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenReturn(null);

        Response response = resource.findByRange(idConsultaProc, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idConsultaProc.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /consultaProcedimiento/{id}/paso - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(conspServiceMock.buscar(idConsultaProc)).thenThrow(new RuntimeException("Error"));

        Response response = resource.findByRange(idConsultaProc, 0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================
    @Test
    @DisplayName("DELETE /consultaProcedimiento/{id}/paso/{id} - Éxito (Retorna 204)")
    void delete_Success() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(consPaso);
        doNothing().when(consppServiceMock).eliminar(consPaso);

        Response response = resource.delete(idConsultaProc, idConsPaso);

        assertEquals(204, response.getStatus());
        verify(consppServiceMock, times(1)).eliminar(consPaso);
    }

    @Test
    @DisplayName("DELETE /consultaProcedimiento/{id}/paso/{id} - No encontrado o de otro padre (Retorna 404)")
    void delete_NotFoundOrDifferentParent_Returns404() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(null);

        Response response = resource.delete(idConsultaProc, idConsPaso);

        assertEquals(404, response.getStatus());
        assertEquals(idConsPaso.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(consppServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /consultaProcedimiento/{id}/paso/{id} - Excepción ReglaNegocio (Retorna 409)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(consPaso);
        doThrow(new ReglaNegocioException("Tiene dependencias")).when(consppServiceMock).eliminar(consPaso);

        Response response = resource.delete(idConsultaProc, idConsPaso);

        assertEquals(409, response.getStatus());
        assertEquals("Tiene dependencias", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /consultaProcedimiento/{id}/paso/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(consppServiceMock.buscar(idConsPaso)).thenReturn(consPaso);
        doThrow(new RuntimeException("Error")).when(consppServiceMock).eliminar(consPaso);

        Response response = resource.delete(idConsultaProc, idConsPaso);

        assertEquals(500, response.getStatus());
    }
}