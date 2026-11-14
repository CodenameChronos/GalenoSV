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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMedioContactoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<TipoMedioContacto> queryMock;

    @InjectMocks
    private TipoMedioContactoService service;

    @Test
    @DisplayName("buscarPorNombre - Éxito al buscar tipos de medio de contacto activos")
    void buscarPorNombre_Success() {
        List<TipoMedioContacto> lista = List.of(new TipoMedioContacto());
        when(emMock.createNamedQuery("TipoMedioContacto.findActiveByNombre", TipoMedioContacto.class))
                .thenReturn(queryMock);
        when(queryMock.setParameter("nombre", "%teléfono%")).thenReturn(queryMock);
        when(queryMock.setMaxResults(10)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.buscarPorNombre(" Teléfono ", 10));
    }

    @Test
    @DisplayName("buscarPorNombre - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombre_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombre(null, 10));
    }
}