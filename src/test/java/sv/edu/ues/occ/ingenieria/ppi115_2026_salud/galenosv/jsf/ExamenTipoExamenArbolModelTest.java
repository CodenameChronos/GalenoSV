package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenTipoExamenArbolModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ExamenTipoExamenModel;

@ExtendWith(MockitoExtension.class)
class ExamenTipoExamenArbolModelTest {

    @Mock
    private ExamenTipoExamenModel examenModel;

    @Mock
    private TipoExamenService tipoExamenService;

    // El Model inyecta DOS campos del mismo tipo (examenTipoExamenService y service).
    // Mockito los distingue por nombre, por eso los mocks se llaman igual que los campos.
    @Mock
    private ExamenTipoExamenService examenTipoExamenService;

    @Mock
    private ExamenTipoExamenService service;

    private ExamenTipoExamenArbolModel arbolModel;

    private FacesTestSupport faces;
    private Examen sampleExamen;
    private TipoExamen tipoA;
    private TipoExamen tipoB;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ExamenTipoExamenArbolModel.class, "arbolModel");
        faces = FacesTestSupport.abrir();
        sampleExamen = new Examen(UUID.randomUUID());
        tipoA = new TipoExamen(UUID.randomUUID());
        tipoA.setNombre("Laboratorio");
        tipoB = new TipoExamen(UUID.randomUUID());
        tipoB.setNombre("Imagen");
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    private ExamenTipoExamen relacion(TipoExamen tipo) {
        ExamenTipoExamen relacion = new ExamenTipoExamen(UUID.randomUUID());
        relacion.setIdTipoExamen(tipo);
        return relacion;
    }

    // ==========================================
    // PRUEBAS PARA getTiposExamenForm
    // ==========================================

    @Test
    @DisplayName("getTiposExamenForm - Éxito (Carga los tipos del examen actual una sola vez)")
    void getTiposExamenForm_Success() {
        when(examenModel.getRegistroActual()).thenReturn(sampleExamen);
        when(examenTipoExamenService.findByIdExamen(sampleExamen.getIdExamen(), 0, Integer.MAX_VALUE))
                .thenReturn(List.of(relacion(tipoA), relacion(null)));

        // La relación sin tipo se ignora
        assertEquals(List.of(tipoA), arbolModel.getTiposExamenForm());

        // Segunda llamada: el examen no cambió, no vuelve a consultar
        arbolModel.getTiposExamenForm();
        verify(examenTipoExamenService, times(1)).findByIdExamen(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("getTiposExamenForm - Fallos (Sin examen o sin id retorna lista vacía sin consultar)")
    void getTiposExamenForm_Failures() {
        when(examenModel.getRegistroActual()).thenReturn(null);
        assertTrue(arbolModel.getTiposExamenForm().isEmpty());

        when(examenModel.getRegistroActual()).thenReturn(new Examen());
        assertTrue(arbolModel.getTiposExamenForm().isEmpty());

        verify(examenTipoExamenService, never()).findByIdExamen(any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA alEntrarPestana
    // ==========================================

    @Test
    @DisplayName("alEntrarPestana - Éxito (Carga los tipos del examen actual)")
    void alEntrarPestana_Success() {
        when(examenModel.getRegistroActual()).thenReturn(sampleExamen);
        when(examenTipoExamenService.findByIdExamen(sampleExamen.getIdExamen(), 0, Integer.MAX_VALUE))
                .thenReturn(List.of(relacion(tipoA)));

        arbolModel.alEntrarPestana();

        assertEquals(List.of(tipoA), arbolModel.getTiposExamenForm());
        verify(examenTipoExamenService, times(1)).findByIdExamen(any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarTipoExamen
    // ==========================================

    @Test
    @DisplayName("completarTipoExamen - Éxito (Excluye los tipos que ya están en el formulario)")
    void completarTipoExamen_Success() {
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();
        when(tipoExamenService.buscarPorNombre("lab", 10)).thenReturn(List.of(tipoA, tipoB));

        assertEquals(List.of(tipoB), arbolModel.completarTipoExamen("lab"));
    }

    // ==========================================
    // PRUEBAS PARA agregarTipoExamen
    // ==========================================

    @Test
    @DisplayName("agregarTipoExamen - Éxito (Agrega el tipo y limpia la selección)")
    void agregarTipoExamen_Success() {
        arbolModel.setTipoExamenAgregar(tipoA);

        arbolModel.agregarTipoExamen();

        assertEquals(List.of(tipoA), arbolModel.getTiposExamenForm());
        assertNull(arbolModel.getTipoExamenAgregar());
        assertTrue(faces.mensajes().isEmpty());
    }

    @Test
    @DisplayName("agregarTipoExamen - Fallos (Sin selección o tipo repetido muestra advertencia)")
    void agregarTipoExamen_Failures() {
        // Sin selección
        arbolModel.setTipoExamenAgregar(null);
        arbolModel.agregarTipoExamen();
        assertEquals(FacesMessage.SEVERITY_WARN, faces.ultimoMensaje().getSeverity());
        assertEquals("Debe seleccionar un tipo de examen.", faces.ultimoMensaje().getSummary());

        // Tipo ya agregado
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();
        assertEquals(FacesMessage.SEVERITY_WARN, faces.ultimoMensaje().getSeverity());
        assertEquals("El tipo de examen ya fue agregado.", faces.ultimoMensaje().getSummary());

        assertEquals(2, faces.mensajes().size());
        assertEquals(List.of(tipoA), arbolModel.getTiposExamenForm());
    }

    // ==========================================
    // PRUEBAS PARA quitarTipoExamen
    // ==========================================

    @Test
    @DisplayName("quitarTipoExamen - Éxito (Quita el tipo seleccionado y limpia la selección)")
    void quitarTipoExamen_Success() {
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();
        arbolModel.setTipoExamenSeleccionado(tipoA);

        arbolModel.quitarTipoExamen();

        assertTrue(arbolModel.getTiposExamenForm().isEmpty());
        assertNull(arbolModel.getTipoExamenSeleccionado());
    }

    @Test
    @DisplayName("quitarTipoExamen - Fallos (Sin selección muestra advertencia)")
    void quitarTipoExamen_Failures() {
        arbolModel.setTipoExamenSeleccionado(null);

        arbolModel.quitarTipoExamen();

        assertEquals(FacesMessage.SEVERITY_WARN, faces.ultimoMensaje().getSeverity());
        assertEquals("Debe seleccionar un tipo de examen para quitarlo.", faces.ultimoMensaje().getSummary());
    }

    // ==========================================
    // PRUEBAS PARA guardarTipos
    // ==========================================

    @Test
    @DisplayName("guardarTipos - Éxito (Sincroniza los tipos, avisa y recarga desde la base)")
    void guardarTipos_Success() {
        List<TipoExamen> enviados = new ArrayList<>();
        // La lista interna se limpia al recargar, por eso se copia al momento de la llamada
        doAnswer(inv -> {
            enviados.addAll(inv.<List<TipoExamen>>getArgument(1));
            return null;
        }).when(service).sincronizarTipos(any(), any());
        when(examenModel.getRegistroActual()).thenReturn(sampleExamen);
        when(examenTipoExamenService.findByIdExamen(sampleExamen.getIdExamen(), 0, Integer.MAX_VALUE))
                .thenReturn(List.of(relacion(tipoA)));
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();

        arbolModel.guardarTipos();

        verify(service, times(1)).sincronizarTipos(eq(sampleExamen), any());
        assertEquals(List.of(tipoA), enviados);
        assertEquals(FacesMessage.SEVERITY_INFO, faces.ultimoMensaje().getSeverity());
        assertEquals("Tipos de examen guardados correctamente.", faces.ultimoMensaje().getSummary());
        assertEquals(List.of(tipoA), arbolModel.getTiposExamenForm());
    }

    @Test
    @DisplayName("guardarTipos - Fallos (Sin examen guardado advierte; error del servicio muestra error)")
    void guardarTipos_Failures() {
        when(examenModel.getRegistroActual()).thenReturn(null, new Examen(), sampleExamen);
        doThrow(new IllegalStateException("Database error")).when(service).sincronizarTipos(any(), any());

        // Sin examen
        arbolModel.guardarTipos();
        assertEquals(FacesMessage.SEVERITY_WARN, faces.ultimoMensaje().getSeverity());
        assertEquals("El examen debe guardarse antes de asignar tipos de examen.",
                faces.ultimoMensaje().getSummary());

        // Examen sin id
        arbolModel.guardarTipos();
        assertEquals(FacesMessage.SEVERITY_WARN, faces.ultimoMensaje().getSeverity());
        verify(service, never()).sincronizarTipos(any(), any());

        // El servicio falla
        arbolModel.guardarTipos();
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals("No se pudieron guardar los tipos de examen.", faces.ultimoMensaje().getSummary());
        verify(service, times(1)).sincronizarTipos(any(), any());
        assertEquals(3, faces.mensajes().size());
    }

    // ==========================================
    // PRUEBAS PARA limpiar
    // ==========================================

    @Test
    @DisplayName("limpiar - Éxito (Vacía la lista y las selecciones)")
    void limpiar_Success() {
        arbolModel.setTipoExamenAgregar(tipoA);
        arbolModel.agregarTipoExamen();
        arbolModel.setTipoExamenAgregar(tipoB);
        arbolModel.setTipoExamenSeleccionado(tipoA);

        arbolModel.limpiar();

        assertTrue(arbolModel.getTiposExamenForm().isEmpty());
        assertNull(arbolModel.getTipoExamenAgregar());
        assertNull(arbolModel.getTipoExamenSeleccionado());
    }
}
