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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Persona> queryMock;

    @InjectMocks
    private PersonaService service;

    @Test
    @DisplayName("buscarPorNombre - Éxito al buscar personas por nombre completo")
    void buscarPorNombre_Success() {
        List<Persona> lista = List.of(new Persona());
        when(emMock.createNamedQuery("Persona.findByNombreCompleto", Persona.class)).thenReturn(queryMock);
        when(queryMock.setParameter("texto", "%maría%")).thenReturn(queryMock);
        when(queryMock.setMaxResults(10)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.buscarPorNombre(" María ", 10));
    }

    @Test
    @DisplayName("buscarPorNombre - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombre_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombre(null, 10));

    }
}
