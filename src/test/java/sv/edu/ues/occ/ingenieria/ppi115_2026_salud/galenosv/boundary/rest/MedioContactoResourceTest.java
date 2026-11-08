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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedioContactoResourceTest {

    @Mock
    private MedioContactoService medioContactoServiceMock;

    @Mock
    private PersonaService personaServiceMock;

    @Mock
    private TipoMedioContactoService tipoMedioContactoServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private MedioContactoResource medioContactoResource;

    private UUID idPersona;
    private UUID idTipoMedio;
    private UUID idMedioContacto;

    private Persona persona;
    private TipoMedioContacto tipoMedioContacto;
    private MedioContacto medioContacto;

    @BeforeEach
    void setUp() {
        idPersona = UUID.randomUUID();
        idTipoMedio = UUID.randomUUID();
        idMedioContacto = UUID.randomUUID();

        persona = new Persona();
        persona.setIdPersona(idPersona);

        tipoMedioContacto = new TipoMedioContacto();
        tipoMedioContacto.setIdTipoMedioContacto(idTipoMedio);

        medioContacto = new MedioContacto();
        medioContacto.setIdMedioContacto(idMedioContacto);
        medioContacto.setIdPersona(persona);
        medioContacto.setIdTipoMedioContacto(tipoMedioContacto);
        medioContacto.setValor("correo@galenosv.com");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /persona/{idPersona}/medio-contacto - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(tipoMedioContactoServiceMock.buscar(idTipoMedio)).thenReturn(tipoMedioContacto);
        doNothing().when(medioContactoServiceMock).crear(any(MedioContacto.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/medio-contacto/" + idMedioContacto));

        Response response = medioContactoResource.create(uriInfoMock, idPersona, medioContacto);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(medioContactoServiceMock, times(1)).crear(medioContacto);
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/medio-contacto - Cuerpo nulo o sin tipo de medio (Retorna 422)")
    void create_InvalidBody_Returns422() {
        Response r1 = medioContactoResource.create(uriInfoMock, idPersona, null);

        MedioContacto medioSinTipo = new MedioContacto();
        medioSinTipo.setIdTipoMedioContacto(null);
        Response r2 = medioContactoResource.create(uriInfoMock, idPersona, medioSinTipo);

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("medio", r1.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/medio-contacto - Persona no encontrada (Retorna 404)")
    void create_PersonaNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = medioContactoResource.create(uriInfoMock, idPersona, medioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(tipoMedioContactoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/medio-contacto - TipoMedioContacto no encontrado (Retorna 404)")
    void create_TipoMedioNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(tipoMedioContactoServiceMock.buscar(idTipoMedio)).thenReturn(null);

        Response response = medioContactoResource.create(uriInfoMock, idPersona, medioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idTipoMedio.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(medioContactoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/medio-contacto - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Error en BD"));

        Response response = medioContactoResource.create(uriInfoMock, idPersona, medioContacto);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(medioContacto);

        Response response = medioContactoResource.findById(idPersona, idMedioContacto);

        assertEquals(200, response.getStatus());
        assertEquals(medioContacto, response.getEntity());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto/{id} - ID de medio nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = medioContactoResource.findById(idPersona, null);

        assertEquals(422, response.getStatus());
        assertEquals("id", response.getHeaderString("Wrong-Param"));
        verify(medioContactoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto/{id} - No encontrado o de otra persona (Retorna 404)")
    void findById_NotFoundOrDifferentPersona_Returns404() throws Exception {
        UUID otraPersonaId = UUID.randomUUID();
        Persona otraPersona = new Persona();
        otraPersona.setIdPersona(otraPersonaId);

        MedioContacto medioAjeno = new MedioContacto();
        medioAjeno.setIdMedioContacto(idMedioContacto);
        medioAjeno.setIdPersona(otraPersona);

        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(null); // Caso nulo
        Response r1 = medioContactoResource.findById(idPersona, idMedioContacto);

        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(medioAjeno); // Caso de otra persona
        Response r2 = medioContactoResource.findById(idPersona, idMedioContacto);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idMedioContacto.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idMedioContacto.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = medioContactoResource.findById(idPersona, idMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<MedioContacto> lista = List.of(medioContacto);
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        when(medioContactoServiceMock.findRangeByCriterio(eq("idPersona"), eq(persona), eq(0), eq(10))).thenReturn(lista);
        when(medioContactoServiceMock.contarByCriterio(eq("idPersona"), eq(persona))).thenReturn(1); // Conteo Integer

        Response response = medioContactoResource.findByRange(idPersona, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = medioContactoResource.findByRange(idPersona, -1, 10); // first < 0
        Response r2 = medioContactoResource.findByRange(idPersona, 0, 0);   // max <= 0
        Response r3 = medioContactoResource.findByRange(idPersona, 0, 51);  // max > 50

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals(422, r3.getStatus());
        assertEquals("primero,max", r1.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto - Persona padre no encontrada (Retorna 404)")
    void findByRange_ParentNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = medioContactoResource.findByRange(idPersona, 0, 50);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/medio-contacto - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = medioContactoResource.findByRange(idPersona, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /persona/{idPersona}/medio-contacto/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(medioContacto);
        doNothing().when(medioContactoServiceMock).eliminar(medioContacto);

        Response response = medioContactoResource.delete(idPersona, idMedioContacto);

        assertEquals(204, response.getStatus());
        verify(medioContactoServiceMock, times(1)).eliminar(medioContacto);
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/medio-contacto/{id} - No encontrado o de otra persona (Retorna 404)")
    void delete_NotFoundOrDifferentPersona_Returns404() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(null);

        Response response = medioContactoResource.delete(idPersona, idMedioContacto);

        assertEquals(404, response.getStatus());
        assertEquals(idMedioContacto.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(medioContactoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/medio-contacto/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(medioContacto);
        doThrow(new ReglaNegocioException("El medio de contacto no se puede eliminar")).when(medioContactoServiceMock).eliminar(medioContacto);

        Response response = medioContactoResource.delete(idPersona, idMedioContacto);

        assertEquals(409, response.getStatus());
        assertEquals("El medio de contacto no se puede eliminar", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/medio-contacto/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(medioContactoServiceMock.buscar(idMedioContacto)).thenReturn(medioContacto);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(medioContactoServiceMock).eliminar(medioContacto);

        Response response = medioContactoResource.delete(idPersona, idMedioContacto);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}