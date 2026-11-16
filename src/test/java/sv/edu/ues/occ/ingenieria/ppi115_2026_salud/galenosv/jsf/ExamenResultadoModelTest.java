package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenResultadoModel;

@ExtendWith(MockitoExtension.class)
class ExamenResultadoModelTest {

    @Mock
    private ExamenResultadoService erServiceMock;

    private ExamenResultadoModel examenResultadoModel;

    private UUID sampleId;
    private ExamenResultado sampleExamenResultado;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ExamenResultadoModel.class, "examenResultadoModel");
        sampleId = UUID.randomUUID();
        sampleExamenResultado = new ExamenResultado();
        sampleExamenResultado.setIdExamenResultado(sampleId);
    }

    @Test
    @DisplayName("getDAO - Éxito")
    void getDAO_Success() {
        assertSame(erServiceMock, examenResultadoModel.getDAO());
    }

    @Test
    @DisplayName("instanciarRegistro - Éxito")
    void instanciarRegistro_Success() {
        ExamenResultado primero = examenResultadoModel.instanciarRegistro();
        ExamenResultado segundo = examenResultadoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdExamenResultado());
        assertNotSame(primero, segundo);
    }

    @Test
    @DisplayName("getRegistroById - No implementado")
    void getRegistroById_NotImplemented() {
        assertAll(
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> examenResultadoModel.getRegistroById(sampleId.toString())
                ),
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> examenResultadoModel.getRegistroById("ID inválido")
                )
        );

        verifyNoInteractions(erServiceMock);
    }

    @Test
    @DisplayName("getIdByRegistro - No implementado")
    void getIdByRegistro_NotImplemented() {
        assertAll(
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> examenResultadoModel.getIdByRegistro(sampleExamenResultado)
                ),
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> examenResultadoModel.getIdByRegistro(null)
                )
        );
    }
}
