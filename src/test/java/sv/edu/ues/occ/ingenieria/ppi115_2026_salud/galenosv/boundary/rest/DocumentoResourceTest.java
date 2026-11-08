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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentoResourceTest {

    @Mock
    private DocumentoService documentoServiceMock;

    @Mock
    private PersonaService personaServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private DocumentoResource documentoResource;

    private UUID idPersona;
    private UUID idDocumento;
    private Persona persona;
    private Documento documento;

    @BeforeEach
    void setUp() {
        idPersona = UUID.randomUUID();
        idDocumento = UUID.randomUUID();

        persona = new Persona();
        persona.setIdPersona(idPersona);

        documento = new Documento();
        documento.setIdDocumento(idDocumento);
        documento.setIdPersona(persona);
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /persona/{idPersona}/documento - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(persona);
        doNothing().when(documentoServiceMock).crear(any(Documento.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/documento/" + idDocumento));

        Response response = documentoResource.create(uriInfoMock, idPersona, documento);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        verify(documentoServiceMock, times(1)).crear(documento);
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/documento - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = documentoResource.create(uriInfoMock, idPersona, null);

        assertEquals(422, response.getStatus());
        assertEquals("doc", response.getHeaderString("Wrong-Param"));
        verify(personaServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/documento - Persona no encontrada (Retorna 404)")
    void create_PersonaNotFound_Returns404() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenReturn(null);

        Response response = documentoResource.create(uriInfoMock, idPersona, documento);

        assertEquals(404, response.getStatus());
        assertEquals(idPersona.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(documentoServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /persona/{idPersona}/documento - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        when(personaServiceMock.buscar(idPersona)).thenThrow(new RuntimeException("Error en BD"));

        Response response = documentoResource.create(uriInfoMock, idPersona, documento);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/documento/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenReturn(documento);

        Response response = documentoResource.findById(idPersona, idDocumento);

        assertEquals(200, response.getStatus());
        assertEquals(documento, response.getEntity());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/documento/{id} - UUID de documento nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = documentoResource.findById(idPersona, null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Entity-Not-Found"));
        verify(documentoServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/documento/{id} - No encontrado o pertenece a otra persona (Retorna 404)")
    void findById_NotFoundOrDifferentPersona_Returns404() throws Exception {
        UUID otraPersonaId = UUID.randomUUID();
        Persona otraPersona = new Persona();
        otraPersona.setIdPersona(otraPersonaId);

        Documento docAjeno = new Documento();
        docAjeno.setIdDocumento(idDocumento);
        docAjeno.setIdPersona(otraPersona);

        when(documentoServiceMock.buscar(idDocumento)).thenReturn(null); // Caso nulo
        Response r1 = documentoResource.findById(idPersona, idDocumento);

        when(documentoServiceMock.buscar(idDocumento)).thenReturn(docAjeno); // Caso de otra persona
        Response r2 = documentoResource.findById(idPersona, idDocumento);

        assertEquals(404, r1.getStatus());
        assertEquals(404, r2.getStatus());
        assertEquals(idDocumento.toString(), r1.getHeaderString("Entity-Not-Found"));
        assertEquals(idDocumento.toString(), r2.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/documento/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = documentoResource.findById(idPersona, idDocumento);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /persona/{idPersona}/documento - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Documento> lista = List.of(documento);
        when(documentoServiceMock.findRange(0, 10)).thenReturn(lista);
        when(documentoServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = documentoResource.findByRange(idPersona, 0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/documento - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = documentoResource.findByRange(idPersona, -1, 10); // first < 0
        Response r2 = documentoResource.findByRange(idPersona, 0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(documentoServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /persona/{idPersona}/documento - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(documentoServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = documentoResource.findByRange(idPersona, 0, 50);

        assertEquals(500, response.getStatus());
        assertEquals("Fallo en listado", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /persona/{idPersona}/documento/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenReturn(documento);
        doNothing().when(documentoServiceMock).eliminar(documento);

        Response response = documentoResource.delete(idPersona, idDocumento);

        assertEquals(204, response.getStatus());
        verify(documentoServiceMock, times(1)).eliminar(documento);
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/documento/{id} - No encontrado o de otra persona (Retorna 404)")
    void delete_NotFoundOrDifferentPersona_Returns404() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenReturn(null);

        Response response = documentoResource.delete(idPersona, idDocumento);

        assertEquals(404, response.getStatus());
        assertEquals(idDocumento.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(documentoServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/documento/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenReturn(documento);
        doThrow(new ReglaNegocioException("El documento posee dependencias activas")).when(documentoServiceMock).eliminar(documento);

        Response response = documentoResource.delete(idPersona, idDocumento);

        assertEquals(409, response.getStatus());
        assertEquals("El documento posee dependencias activas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /persona/{idPersona}/documento/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(documentoServiceMock.buscar(idDocumento)).thenReturn(documento);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(documentoServiceMock).eliminar(documento);

        Response response = documentoResource.delete(idPersona, idDocumento);

        assertEquals(500, response.getStatus());
        assertEquals("Error inesperado al eliminar", response.getHeaderString("Application-Error"));
    }
}