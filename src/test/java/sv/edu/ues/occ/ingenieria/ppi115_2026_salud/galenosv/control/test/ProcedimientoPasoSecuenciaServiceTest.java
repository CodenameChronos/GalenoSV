package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoSecuenciaServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<ProcedimientoPasoSecuencia> queryMock;

    @InjectMocks
    private ProcedimientoPasoSecuenciaService service;

    @Test
    @DisplayName("listarPorProcedimiento - Éxito al listar secuencias del procedimiento")
    void listarPorProcedimiento_Success() {
        UUID idProcedimiento = UUID.randomUUID();
        List<ProcedimientoPasoSecuencia> lista = List.of(new ProcedimientoPasoSecuencia());
        when(emMock.createNamedQuery("ProcedimientoPasoSecuencia.findByProcedimiento",
                ProcedimientoPasoSecuencia.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idProcedimiento", idProcedimiento)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.listarPorProcedimiento(idProcedimiento));
    }

    @Test
    @DisplayName("listarPorProcedimiento - Fallo en consulta (Propaga excepción)")
    void listarPorProcedimiento_QueryError_ThrowsException() {
        UUID idProcedimiento = UUID.randomUUID();
        when(emMock.createNamedQuery("ProcedimientoPasoSecuencia.findByProcedimiento",
                ProcedimientoPasoSecuencia.class)).thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class, () -> service.listarPorProcedimiento(idProcedimiento));
    }
}