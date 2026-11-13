package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.validator.ValidatorException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.primefaces.PrimeFaces;
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
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ExamenTipoExamenModel.class, "examenTipoExamenModel");
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
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(examenServiceMock, examenTipoExamenModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Examen primero = examenTipoExamenModel.instanciarRegistro();
        Examen segundo = examenTipoExamenModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdExamen());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(examenServiceMock.buscar(sampleId)).thenReturn(sampleExamen);

        Examen resultado = examenTipoExamenModel.getRegistroById(sampleId.toString());

        assertSame(sampleExamen, resultado);
        verify(examenServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(examenServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(examenTipoExamenModel.getRegistroById("no-es-un-uuid"));
        assertNull(examenTipoExamenModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(examenServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, examenTipoExamenModel.getIdByRegistro(sampleExamen));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(examenTipoExamenModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(examenServiceMock.contar()).thenReturn(3);

        assertNotNull(examenTipoExamenModel.getLazyModel());
        assertEquals(3, examenTipoExamenModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Pasa a CREAR y limpia el árbol de tipos de examen)")
    void nuevo_Success() {
        examenTipoExamenModel.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, examenTipoExamenModel.getEstado());
        verify(arbolModelMock, times(1)).limpiar();
    }

    // ==========================================
    // PRUEBAS PARA seleccionar
    // ==========================================

    @Test
    @DisplayName("seleccionar - Éxito (Pasa a MODIFICAR y carga los tipos del examen elegido)")
    void seleccionar_Success() {
        when(selectEventMock.getObject()).thenReturn(sampleExamen);

        examenTipoExamenModel.seleccionar(selectEventMock);

        assertEquals(ESTADO_CRUD.MODIFICAR, examenTipoExamenModel.getEstado());
        assertSame(sampleExamen, examenTipoExamenModel.getRegistroActual());
        verify(arbolModelMock, times(1)).alEntrarPestana();
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================

    @Test
    @DisplayName("guardarHandler - Éxito (Guarda el examen y luego sus tipos si el examen tiene id)")
    void guardarHandler_Success() {
        // Examen con id: guarda el examen y después los tipos
        examenTipoExamenModel.setRegistroActual(sampleExamen);
        examenTipoExamenModel.setEstado(ESTADO_CRUD.MODIFICAR);

        examenTipoExamenModel.guardarHandler(null);

        verify(examenServiceMock, times(1)).actualizar(sampleExamen);
        verify(arbolModelMock, times(1)).guardarTipos();

        // Examen sin id: guarda el examen pero no asigna tipos
        Examen sinId = new Examen();
        examenTipoExamenModel.setRegistroActual(sinId);
        examenTipoExamenModel.setEstado(ESTADO_CRUD.CREAR);

        examenTipoExamenModel.guardarHandler(null);

        verify(examenServiceMock, times(1)).crear(sinId);
        verify(arbolModelMock, times(1)).guardarTipos();
    }

    @Test
    @DisplayName("guardarHandler - Fallos (Error al guardar los tipos se relanza como IllegalStateException)")
    void guardarHandler_Failures() {
        RuntimeException error = new RuntimeException("No se pudo sincronizar");
        doThrow(error).when(arbolModelMock).guardarTipos();
        examenTipoExamenModel.setRegistroActual(sampleExamen);
        examenTipoExamenModel.setEstado(ESTADO_CRUD.MODIFICAR);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> examenTipoExamenModel.guardarHandler(null));

        assertSame(error, ex.getCause());
    }
}
