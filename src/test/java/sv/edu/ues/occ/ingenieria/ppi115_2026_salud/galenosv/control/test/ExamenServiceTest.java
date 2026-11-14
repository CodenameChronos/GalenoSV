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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Examen> queryMock;

    @InjectMocks
    private ExamenService service;

    @Test
    @DisplayName("buscarPorNombre - Éxito al buscar exámenes activos")
    void buscarPorNombre_Success() {
        List<Examen> lista = List.of(new Examen());
        when(emMock.createNamedQuery("Examen.findActiveByNombre", Examen.class)).thenReturn(queryMock);
        when(queryMock.setParameter("nombre", "%hemograma%")).thenReturn(queryMock);
        when(queryMock.setMaxResults(10)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.buscarPorNombre(" Hemograma ", 10));
    }

    @Test
    @DisplayName("buscarPorNombre - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombre_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombre(null, 10));
    }
}