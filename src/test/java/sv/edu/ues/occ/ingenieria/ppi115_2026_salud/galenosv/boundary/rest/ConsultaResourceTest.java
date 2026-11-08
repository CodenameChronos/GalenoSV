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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaResourceTest {

    @Mock
    private ConsultaService consultaServiceMock;

    @Mock
    private PersonaRolService personaRolServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ConsultaResource consultaResource;

    private UUID idPersonaRol;
    private UUID idConsulta;
    private PersonaRol personaRol;
    private Consulta consulta;

    @BeforeEach
    void setUp() {
        idPersonaRol = UUID.randomUUID();
        idConsulta = UUID.randomUUID();

        personaRol = new PersonaRol();
        personaRol.setIdPersonaRol(idPersonaRol);

        consulta = new Consulta();
        consulta.setIdConsulta(idConsulta);
        consulta.setIdPersonaRol(personaRol);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /persona-rol/{id}/consulta - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        doNothing().when(consultaServiceMock).crear(any(Consulta.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/consulta/" + idConsulta));

        Response response = consultaResource.create(uriInfoMock, idPersonaRol, consulta);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(consultaServiceMock, times(1)).crear(consulta);
    }

    @Test
    @DisplayName("POST /persona-rol/{id}/consulta - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = consultaResource.create(uriInfoMock, idPersonaRol, null);

        assertEquals(422, response.getStatus());
        assertEquals("consulta", response.getHeaderString("Wrong-Param"));
        verify(personaRolServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona-rol/{id}/consulta - PersonaRol no encontrada (Retorna 404)")
    void create_PersonaRolNotFound_Returns404() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(null);

        Response response = consultaResource.create(uriInfoMock, idPersonaRol, consulta);

        assertEquals(404, response.getStatus());
        assertEquals(idPersonaRol.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(consultaServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /persona-rol/{id}/consulta - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenThrow(new RuntimeException("Error BD"));

        Response response = consultaResource.create(uriInfoMock, idPersonaRol, consulta);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta/{idConsulta} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);

        Response response = consultaResource.findById(idPersonaRol, idConsulta);

        assertEquals(200, response.getStatus());
        assertEquals(consulta, response.getEntity());
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta/{idConsulta} - ID de consulta nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = consultaResource.findById(idPersonaRol, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(consultaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta/{idConsulta} - No encontrada o de otro PersonaRol (Retorna 404)")
    void findById_NotFoundOrDifferentParent_Returns404() throws Exception {
        UUID otroIdRol = UUID.randomUUID();
        PersonaRol otroRol = new PersonaRol();
        otroRol.setIdPersonaRol(otroIdRol);

        Consulta consultaAjena = new Consulta();
        consultaAjena.setIdConsulta(idConsulta);
        consultaAjena.setIdPersonaRol(otroRol);

        when(consultaServiceMock.buscar(idConsulta)).thenReturn(null); // Caso nula
        Response r1 = consultaResource.findById(idPersonaRol, idConsulta);

        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consultaAjena); // Caso de otro rol padre
        Response r2 = consultaResource.findById(idPersonaRol, idConsulta);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idConsulta.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idConsulta.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta/{idConsulta} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenThrow(new RuntimeException("Error conexión"));

        Response response = consultaResource.findById(idPersonaRol, idConsulta);

        assertEquals(500, response.getStatus());
        assertEquals("Error conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Consulta> lista = List.of(consulta);
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        when(consultaServiceMock.findRangeByCriterio(eq("idPersonaRol"), eq(personaRol), eq(0), eq(10))).thenReturn(lista);
        when(consultaServiceMock.contarByCriterio(eq("idPersonaRol"), eq(personaRol))).thenReturn(1); // Conteo Integer

        Response response = consultaResource.findByRange(idPersonaRol, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = consultaResource.findByRange(idPersonaRol, -1, 10); // first < 0
        Response r2 = consultaResource.findByRange(idPersonaRol, 0, 0);   // max <= 0
        Response r3 = consultaResource.findByRange(idPersonaRol, 0, 51);  // max > 50

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals(422, r3.getStatus());
        assertEquals("primero,max", r1.getHeaderString("Wrong-Param"));
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta - PersonaRol padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(null);

        Response response = consultaResource.findByRange(idPersonaRol, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idPersonaRol.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona-rol/{id}/consulta - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenThrow(new RuntimeException("Fallo en criterio"));

        Response response = consultaResource.findByRange(idPersonaRol, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en criterio", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /persona-rol/{id}/consulta/{idConsulta} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        doNothing().when(consultaServiceMock).eliminar(consulta);

        Response response = consultaResource.delete(idPersonaRol, idConsulta);

        assertEquals(204, response.getStatus());
        verify(consultaServiceMock, times(1)).eliminar(consulta);
    }

    @Test
    @DisplayName("DELETE /persona-rol/{id}/consulta/{idConsulta} - No encontrada o de otro rol (Retorna 404)")
    void delete_NotFoundOrDifferentParent_Returns404() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(null);

        Response response = consultaResource.delete(idPersonaRol, idConsulta);

        assertEquals(404, response.getStatus());
        assertEquals(idConsulta.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(consultaServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /persona-rol/{id}/consulta/{idConsulta} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        doThrow(new ReglaNegocioException("La consulta tiene procedimientos vinculados")).when(consultaServiceMock).eliminar(consulta);

        Response response = consultaResource.delete(idPersonaRol, idConsulta);

        assertEquals(409, response.getStatus());
        assertEquals("La consulta tiene procedimientos vinculados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /persona-rol/{id}/consulta/{idConsulta} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(consultaServiceMock.buscar(idConsulta)).thenReturn(consulta);
        doThrow(new RuntimeException("Error inesperado en borrado")).when(consultaServiceMock).eliminar(consulta);

        Response response = consultaResource.delete(idPersonaRol, idConsulta);

        assertEquals(500, response.getStatus());
    }
}