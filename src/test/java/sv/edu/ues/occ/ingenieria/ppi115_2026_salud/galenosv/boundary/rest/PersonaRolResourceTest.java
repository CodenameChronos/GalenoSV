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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaRolResourceTest {

    @Mock
    private PersonaRolService personaRolServiceMock;

    @Mock
    private PersonaService personaServiceMock;

    @Mock
    private RolService rolServiceMock;

    @Mock
    private ClinicaService clinicaServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private PersonaRolResource personaRolResource;

    private UUID idPersona;
    private UUID idRol;
    private UUID idClinica;
    private UUID idPersonaRol;

    private Persona persona;
    private Rol rol;
    private Clinica clinica;
    private PersonaRol personaRol;

    @BeforeEach
    void setUp() {
        idPersona = UUID.randomUUID();
        idRol = UUID.randomUUID();
        idClinica = UUID.randomUUID();
        idPersonaRol = UUID.randomUUID();

        persona = new Persona();
        persona.setIdPersona(idPersona);

        rol = new Rol();
        rol.setIdRol(idRol);

        clinica = new Clinica();
        clinica.setIdClinica(idClinica);

        personaRol = new PersonaRol();
        personaRol.setIdPersonaRol(idPersonaRol);
        personaRol.setIdPersona(persona);
        personaRol.setIdRol(rol);
        personaRol.setIdClinica(clinica);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        when(clinicaServiceMock.buscar(idClinica)).thenReturn(clinica);
        doNothing().when(personaRolServiceMock).crear(any(PersonaRol.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/persona-rol/" + idPersonaRol));

        Response response = personaRolResource.create(uriInfoMock, idPersona, personaRol);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(personaRolServiceMock, times(1)).crear(personaRol);
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Cuerpo nulo o campos incompletos (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = personaRolResource.create(uriInfoMock, idPersona, null);

        PersonaRol rolSinClinica = new PersonaRol();
        rolSinClinica.setIdRol(rol);
        rolSinClinica.setIdClinica(null);
        Response r2 = personaRolResource.create(uriInfoMock, idPersona, rolSinClinica);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("personaRol", r1.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Persona no encontrada (Retorna 404)")
    void create_PersonaNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = personaRolResource.create(uriInfoMock, idPersona, personaRol);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(rolServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Rol no encontrado (Retorna 404)")
    void create_RolNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(rolServiceMock.buscar(idRol)).thenReturn(null);

        Response response = personaRolResource.create(uriInfoMock, idPersona, personaRol);

        assertEquals(404, response.getStatus());
        assertEquals(idRol.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(clinicaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Clínica no encontrada (Retorna 404)")
    void create_ClinicaNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        when(clinicaServiceMock.buscar(idClinica)).thenReturn(null);

        Response response = personaRolResource.create(uriInfoMock, idPersona, personaRol);

        assertEquals(404, response.getStatus());
        assertEquals(idClinica.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(personaRolServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/persona-rol - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Error en BD"));

        Response response = personaRolResource.create(uriInfoMock, idPersona, personaRol);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);

        Response response = personaRolResource.findById(idPersona, idPersonaRol);

        assertEquals(200, response.getStatus());
        assertEquals(personaRol, response.getEntity());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol/{id} - ID de personaRol nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = personaRolResource.findById(idPersona, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(personaRolServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol/{id} - No encontrado o de otra persona (Retorna 404)")
    void findById_NotFoundOrDifferentPersona_Returns404() throws Exception {
        UUID otraPersonaId = UUID.randomUUID();
        Persona otraPersona = new Persona();
        otraPersona.setIdPersona(otraPersonaId);

        PersonaRol rolAjeno = new PersonaRol();
        rolAjeno.setIdPersonaRol(idPersonaRol);
        rolAjeno.setIdPersona(otraPersona);

        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(null); // Caso nulo
        Response r1 = personaRolResource.findById(idPersona, idPersonaRol);

        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(rolAjeno); // Caso de otra persona
        Response r2 = personaRolResource.findById(idPersona, idPersonaRol);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idPersonaRol.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idPersonaRol.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = personaRolResource.findById(idPersona, idPersonaRol);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<PersonaRol> lista = List.of(personaRol);
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(personaRolServiceMock.findRangeByCriterio(eq("idPersona"), eq(persona), eq(0), eq(10))).thenReturn(lista);
        when(personaRolServiceMock.contarByCriterio(eq("idPersona"), eq(persona))).thenReturn(1); // Conteo Integer

        Response response = personaRolResource.findByRange(idPersona, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = personaRolResource.findByRange(idPersona, -1, 10); // first < 0
        Response r2 = personaRolResource.findByRange(idPersona, 0, 0);   // max <= 0
        Response r3 = personaRolResource.findByRange(idPersona, 0, 51);  // max > 50

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals(422, r3.getStatus());
        assertEquals("primero,max", r1.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol - Persona padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = personaRolResource.findByRange(idPersona, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/persona-rol - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = personaRolResource.findByRange(idPersona, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /persona/{idPersona}/persona-rol/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        doNothing().when(personaRolServiceMock).eliminar(personaRol);

        Response response = personaRolResource.delete(idPersona, idPersonaRol);

        assertEquals(204, response.getStatus());
        verify(personaRolServiceMock, times(1)).eliminar(personaRol);
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/persona-rol/{id} - No encontrado o de otra persona (Retorna 404)")
    void delete_NotFoundOrDifferentPersona_Returns404() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(null);

        Response response = personaRolResource.delete(idPersona, idPersonaRol);

        assertEquals(404, response.getStatus());
        assertEquals(idPersonaRol.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(personaRolServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/persona-rol/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        doThrow(new ReglaNegocioException("El rol de la persona tiene consultas asociadas")).when(personaRolServiceMock).eliminar(personaRol);

        Response response = personaRolResource.delete(idPersona, idPersonaRol);

        assertEquals(409, response.getStatus());
        assertEquals("El rol de la persona tiene consultas asociadas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/persona-rol/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(personaRolServiceMock.buscar(idPersonaRol)).thenReturn(personaRol);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(personaRolServiceMock).eliminar(personaRol);

        Response response = personaRolResource.delete(idPersona, idPersonaRol);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}