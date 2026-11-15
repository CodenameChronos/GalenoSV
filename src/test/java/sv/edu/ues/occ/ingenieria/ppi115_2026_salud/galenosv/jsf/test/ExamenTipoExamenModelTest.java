package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.event.SelectEvent;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenTipoExamenArbolModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenTipoExamenModel;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenModelTest {

    @Mock
    private ExamenService examenServiceMock;

    @Mock
    private ExamenTipoExamenArbolModel arbolModelMock;

    @Mock
    private SelectEvent<Examen> selectEventMock;

    private ExamenTipoExamenModel examenTipoExamenModel;

    private UUID sampleId;
    private Examen sampleExamen;
    private FacesTestSupport faces;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, ExamenTipoExamenModel.class, "examenTipoExamenModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        sampleExamen = new Examen();
        sampleExamen.setIdExamen(sampleId);
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }


    // ==========================================
    // PRUEBAS PARA nuevo / seleccionar (comportamiento sobrescrito)
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Prepara un examen nuevo y limpia el árbol de tipos)")
    void nuevo_Success() {
        examenTipoExamenModel.nuevo();

        assertNotNull(examenTipoExamenModel.getRegistroActual());
        assertNull(examenTipoExamenModel.getRegistroActual().getIdExamen());
        assertEquals(ESTADO_CRUD.CREAR, examenTipoExamenModel.getEstado());
        verify(arbolModelMock).limpiar();
    }

    @Test
    @DisplayName("seleccionar - Éxito (Selecciona el examen y carga los tipos asociados)")
    void seleccionar_Success() {
        when(selectEventMock.getObject()).thenReturn(sampleExamen);

        examenTipoExamenModel.seleccionar(selectEventMock);

        assertSame(sampleExamen, examenTipoExamenModel.getRegistroActual());
        assertEquals(ESTADO_CRUD.MODIFICAR, examenTipoExamenModel.getEstado());
        verify(arbolModelMock).alEntrarPestana();
    }

}
