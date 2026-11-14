package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Rol> queryMock;

    @InjectMocks
    private RolService service;

    // ==========================================
    // PRUEBAS PARA buscarPorNombre
    // ==========================================

    @Test
    @DisplayName("buscarPorNombre - Éxito al buscar roles activos")
    void buscarPorNombre_Success() {
        List<Rol> lista = List.of(new Rol());
        when(emMock.createNamedQuery("Rol.findActiveByNombre", Rol.class)).thenReturn(queryMock);
        when(queryMock.setParameter("nombre", "%enfermería%")).thenReturn(queryMock);
        when(queryMock.setMaxResults(10)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.buscarPorNombre(" Enfermería ", 10));
    }

    @Test
    @DisplayName("buscarPorNombre - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombre_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombre(null, 10));
    }

    // ==========================================
    // PRUEBAS PARA findAllActive
    // ==========================================

    @Test
    @DisplayName("findAllActive - Éxito al listar todos los roles activos")
    void findAllActive_Success() {
        List<Rol> lista = List.of(new Rol());
        when(emMock.createNamedQuery("Rol.findByActivo", Rol.class)).thenReturn(queryMock);
        when(queryMock.setParameter("activo", Boolean.TRUE)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.findAllActive());
    }

    @Test
    @DisplayName("findAllActive - Fallo en consulta (Propaga excepción)")
    void findAllActive_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("Rol.findByActivo", Rol.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class, () -> service.findAllActive());
    }
}