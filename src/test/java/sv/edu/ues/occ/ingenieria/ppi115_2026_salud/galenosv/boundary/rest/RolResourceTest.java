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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolResourceTest {

    @Mock
    private RolService rolServiceMock;

    @Mock
    private UriInfo uriInfoMock;

    @Mock
    private UriBuilder uriBuilderMock;

    @InjectMocks
    private RolResource rolResource;

    private UUID idRol;
    private Rol rol;

    @BeforeEach
    void setUp() {
        idRol = UUID.randomUUID();
        rol = new Rol();
        rol.setIdRol(idRol);
        rol.setNombre("Administrador");
    }

    // ==========================================
    // PRUEBAS PARA POST (create)
    // ==========================================

    @Test
    @DisplayName("POST /rol - Éxito (Retorna 201 Created)")
    void create_Success() throws Exception {
        doNothing().when(rolServiceMock).crear(any(Rol.class));

        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(any(String.class))).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(new URI("http://localhost/rol/" + idRol));

        Response response = rolResource.create(uriInfoMock, rol);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(rol.getActivo()); // Verifica que asigne activo = true por defecto
        verify(rolServiceMock, times(1)).crear(rol);
    }

    @Test
    @DisplayName("POST /rol - Fallo por cuerpo nulo (Retorna 422)")
    void create_NullBody_Returns422() {
        Response response = rolResource.create(uriInfoMock, null);

        assertEquals(422, response.getStatus());
        assertEquals("rol", response.getHeaderString("Wrong-Param"));
        verify(rolServiceMock, never()).crear(any());
    }

    @Test
    @DisplayName("POST /rol - Excepción general (Retorna 500)")
    void create_Exception_Returns500() throws Exception {
        doThrow(new RuntimeException("Error en BD")).when(rolServiceMock).crear(any());

        Response response = rolResource.create(uriInfoMock, rol);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA GET /id (findById)
    // ==========================================

    @Test
    @DisplayName("GET /rol/{id} - Éxito (Retorna 200)")
    void findById_Success() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);

        Response response = rolResource.findById(idRol);

        assertEquals(200, response.getStatus());
        assertEquals(rol, response.getEntity());
    }

    @Test
    @DisplayName("GET /rol/{id} - UUID nulo (Retorna 422)")
    void findById_NullUuid_Returns422() {
        Response response = rolResource.findById(null);

        assertEquals(422, response.getStatus());
        assertNull(response.getHeaderString("Wrong-Param"));
        verify(rolServiceMock, never()).buscar(any());
    }

    @Test
    @DisplayName("GET /rol/{id} - No encontrado (Retorna 404)")
    void findById_NotFound() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(null);

        Response response = rolResource.findById(idRol);

        assertEquals(404, response.getStatus());
        assertEquals(idRol.toString(), response.getHeaderString("Entity-Not-Found"));
    }

    @Test
    @DisplayName("GET /rol/{id} - Excepción general (Retorna 500)")
    void findById_Exception_Returns500() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenThrow(new RuntimeException("Error de conexión"));

        Response response = rolResource.findById(idRol);

        assertEquals(500, response.getStatus());
        assertEquals("Error de conexión", response.getHeaderString("Application-Error"));
    }

    // ==========================================
    // PRUEBAS PARA GET (findByRange)
    // ==========================================

    @Test
    @DisplayName("GET /rol - Éxito con rango y conteo Integer (Retorna 200)")
    void findByRange_Success() throws Exception {
        List<Rol> lista = List.of(rol);
        when(rolServiceMock.findRange(0, 10)).thenReturn(lista);
        when(rolServiceMock.contar()).thenReturn(1); // Conteo tipo Integer

        Response response = rolResource.findByRange(0, 10);

        assertEquals(200, response.getStatus());
        assertEquals(lista, response.getEntity());
        assertEquals("1", response.getHeaderString("Total-registros"));
    }

    @Test
    @DisplayName("GET /rol - Parámetros de rango inválidos (Retorna 422)")
    void findByRange_InvalidParams_Returns422() {
        Response r1 = rolResource.findByRange(-1, 10); // first < 0
        Response r2 = rolResource.findByRange(0, 0);   // max <= 0

        assertEquals(422, r1.getStatus());
        assertEquals(422, r2.getStatus());
        assertEquals("first,max", r1.getHeaderString("Wrong-Param"));
        verify(rolServiceMock, never()).findRange(anyInt(), anyInt());
    }

    @Test
    @DisplayName("GET /rol - Excepción general (Retorna 500)")
    void findByRange_Exception_Returns500() throws Exception {
        when(rolServiceMock.findRange(0, 50)).thenThrow(new RuntimeException("Fallo en listado"));

        Response response = rolResource.findByRange(0, 50);

        assertEquals(500, response.getStatus());
    }

    // ==========================================
    // PRUEBAS PARA DELETE (delete)
    // ==========================================

    @Test
    @DisplayName("DELETE /rol/{id} - Éxito (Retorna 204 No Content)")
    void delete_Success() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        doNothing().when(rolServiceMock).eliminar(rol);

        Response response = rolResource.delete(idRol);

        assertEquals(204, response.getStatus());
        verify(rolServiceMock, times(1)).eliminar(rol);
    }

    @Test
    @DisplayName("DELETE /rol/{id} - No encontrado (Retorna 404)")
    void delete_NotFound() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(null);

        Response response = rolResource.delete(idRol);

        assertEquals(404, response.getStatus());
        assertEquals(idRol.toString(), response.getHeaderString("Entity-Not-Found"));
        verify(rolServiceMock, never()).eliminar(any());
    }

    @Test
    @DisplayName("DELETE /rol/{id} - Excepción de Regla de Negocio (Retorna 409 Conflict)")
    void delete_ReglaNegocioException_Returns409() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        doThrow(new ReglaNegocioException("El rol tiene personas asociadas")).when(rolServiceMock).eliminar(rol);

        Response response = rolResource.delete(idRol);

        assertEquals(409, response.getStatus());
        assertEquals("El rol tiene personas asociadas", response.getEntity());
    }

    @Test
    @DisplayName("DELETE /rol/{id} - Excepción general (Retorna 500)")
    void delete_Exception_Returns500() throws Exception {
        when(rolServiceMock.buscar(idRol)).thenReturn(rol);
        doThrow(new RuntimeException("Error inesperado al eliminar")).when(rolServiceMock).eliminar(rol);

        Response response = rolResource.delete(idRol);

        assertEquals(500, response.getStatus());
    }
}