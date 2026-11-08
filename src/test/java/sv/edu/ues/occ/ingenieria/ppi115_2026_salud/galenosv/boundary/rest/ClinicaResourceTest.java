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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaResourceTest {

    @Mock
    private ClinicaService serviceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private ClinicaResource clinicaResource;

    private UUID sampleId;
    private Clinica sampleClinica;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleClinica = new Clinica();
        sampleClinica.setIdClinica(sampleId);
        sampleClinica.setNombre("Clínica Central");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /clinica - Éxito (Retorna 201 Created con URI)")
    void create_Success() throws Exception {
        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/clinica/" + sampleId));
        doNothing().when(serviceMock).crear(any(Clinica.class));

        Response response = clinicaResource.create(uriInfoMock, sampleClinica);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(sampleClinica.getActivo()); // Verifica que asigne activo = true por defecto
        verify(serviceMock, times(1)).crear(sampleClinica);
    }

    @Test
    @DisplayName("POST /clinica - Fallo por objeto nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = clinicaResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("clinica", response.getHeaderString("Wrong-Param"));
        verify(serviceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /clinica - Fallo interno del servicio (Retorna 500)")
    void create_ServiceException_Returns500() throws Exception {
        doThrow(new RuntimeException("Database error")).when(serviceMock).crear(any(Clinica.class));

        Response response = clinicaResource.create(uriInfoMock, sampleClinica);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /clinica/{id} - Éxito (Retorna 200 y la clínica)")
    void findById_Success() throws Exception {
        when(serviceMock.buscar(sampleId)).thenReturn(sampleClinica);

        Response response = clinicaResource.findById(sampleId);

        assertEquals(200, response.getStatus());
        assertEquals(sampleClinica, response.getEntity());
        verify(serviceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("GET /clinica/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        when(serviceMock.buscar(randomId)).thenReturn(null);

        Response response = clinicaResource.findById(randomId);

        assertEquals(404, response.getStatus());
        assertEquals(randomId.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /clinica/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = clinicaResource.findById(null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Wrong-Param"));
        verify(serviceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /clinica/{id} - Error de aplicación (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(serviceMock.buscar(sampleId)).thenThrow(new RuntimeException("Connection error"));

        Response response = clinicaResource.findById(sampleId);

        assertEquals(500, response.getStatus());
        assertEquals("Connection error", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /clinica - Éxito con paginación (Retorna 200 y lista)")
    void findByRange_Success() throws Exception {
        List<Clinica> listaMock = List.of(sampleClinica);
        when(serviceMock.findRange(0, 10)).thenReturn(listaMock);
        when(serviceMock.contar()).thenReturn(1);

        Response response = clinicaResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(listaMock, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /clinica - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response response1 = clinicaResource.findByRange(-1, 10); // first < 0
        Response response2 = clinicaResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, response1.getStatus());
        assertEquals("first,max", response1.getHeaderString("Wrong-Param"));

        assertEquals(422, response2.getStatus());
        assertEquals("first,max", response2.getHeaderString("Wrong-Param"));
        verify(serviceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /clinica - Excepción en capa de control (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(serviceMock.findRange(0, 50)).thenThrow(new RuntimeException("Query failed"));

        Response response = clinicaResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /clinica/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(serviceMock.buscar(sampleId)).thenReturn(sampleClinica);
        doNothing().when(serviceMock).eliminar(sampleClinica);

        Response response = clinicaResource.delete(sampleId);

        assertEquals(204, response.getStatus());
        verify(serviceMock, times(1)).eliminar(sampleClinica);
    }

    @Test
    @DisplayName("DELETE /clinica/{id} - Recurso no encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(serviceMock.buscar(sampleId)).thenReturn(null);

        Response response = clinicaResource.delete(sampleId);

        assertEquals(404, response.getStatus());
        assertEquals(sampleId.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(serviceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /clinica/{id} - Excepción de regla de negocio (Retorna 409 Conflict)")
    void delete_BusinessRuleException_Returns409() throws Exception {
        when(serviceMock.buscar(sampleId)).thenReturn(sampleClinica);
        doThrow(new ReglaNegocioException("La clínica tiene registros asociados")).when(serviceMock).eliminar(sampleClinica);

        Response response = clinicaResource.delete(sampleId);

        assertEquals(409, response.getStatus());
        assertEquals("La clínica tiene registros asociados", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /clinica/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(serviceMock.buscar(sampleId)).thenReturn(sampleClinica);
        doThrow(new RuntimeException("Unexpected error")).when(serviceMock).eliminar(sampleClinica);

        Response response = clinicaResource.delete(sampleId);

        assertEquals(500, response.getStatus());
    }
}