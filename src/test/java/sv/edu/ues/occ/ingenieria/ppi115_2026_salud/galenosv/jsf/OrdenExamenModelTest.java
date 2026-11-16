package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.OrdenExamenModel;

@ExtendWith(MockitoExtension.class)
class OrdenExamenModelTest {

    @Mock
    private OrdenExamenService oeServiceMock;

    private OrdenExamenModel ordenExamenModel;

    private UUID sampleId;
    private OrdenExamen sampleOrdenExamen;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, OrdenExamenModel.class, "ordenExamenModel");
        sampleId = UUID.randomUUID();
        sampleOrdenExamen = new OrdenExamen();
        sampleOrdenExamen.setIdOrdenExamen(sampleId);
    }

    @Test
    @DisplayName("getDAO - Éxito")
    void getDAO_Success() {
        assertSame(oeServiceMock, ordenExamenModel.getDAO());
    }

    @Test
    @DisplayName("instanciarRegistro - Éxito")
    void instanciarRegistro_Success() {
        OrdenExamen primero = ordenExamenModel.instanciarRegistro();
        OrdenExamen segundo = ordenExamenModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdOrdenExamen());
        assertNotSame(primero, segundo);
    }

    @Test
    @DisplayName("getRegistroById - No implementado")
    void getRegistroById_NotImplemented() {
        assertAll(
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> ordenExamenModel.getRegistroById(sampleId.toString())
                ),
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> ordenExamenModel.getRegistroById("ID inválido")
                )
        );

        verifyNoInteractions(oeServiceMock);
    }

    @Test
    @DisplayName("getIdByRegistro - No implementado")
    void getIdByRegistro_NotImplemented() {
        assertAll(
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> ordenExamenModel.getIdByRegistro(sampleOrdenExamen)
                ),
                () -> assertThrows(
                        UnsupportedOperationException.class,
                        () -> ordenExamenModel.getIdByRegistro(null)
                )
        );
    }
}
