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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcedimientoPasoExamenServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Examen> queryMock;

    @InjectMocks
    private ProcedimientoPasoExamenService service;

    @Test
    @DisplayName("listarExamenesDePaso - Éxito al listar exámenes asignados al paso")
    void listarExamenesDePaso_Success() {
        UUID idPaso = UUID.randomUUID();
        List<Examen> lista = List.of(new Examen());
        when(emMock.createNamedQuery("ProcedimientoPasoExamen.findExamenByProcedimientoPaso", Examen.class))
                .thenReturn(queryMock);
        when(queryMock.setParameter("idPaso", idPaso)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(lista);

        assertEquals(lista, service.listarExamenesDePaso(idPaso));
    }

    @Test
    @DisplayName("listarExamenesDePaso - Fallo en consulta (Propaga excepción)")
    void listarExamenesDePaso_QueryError_ThrowsException() {
        UUID idPaso = UUID.randomUUID();
        when(emMock.createNamedQuery("ProcedimientoPasoExamen.findExamenByProcedimientoPaso", Examen.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class, () -> service.listarExamenesDePaso(idPaso));
    }
}
