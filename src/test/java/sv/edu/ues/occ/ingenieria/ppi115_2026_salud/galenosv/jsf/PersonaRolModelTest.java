package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.PersonaRolModel;

@ExtendWith(MockitoExtension.class)
class PersonaRolModelTest {

    @Mock
    private PersonaRolService prServiceMock;

    @Mock
    private PersonaService personaServiceMock;

    @Mock
    private RolService rolServiceMock;

    @Mock
    private ClinicaService clinicaServiceMock;

    private PersonaRolModel personaRolModel;

    private UUID sampleId;
    private PersonaRol samplePersonaRol;
    private FacesTestSupport faces;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, PersonaRolModel.class, "personaRolModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        samplePersonaRol = new PersonaRol();
        samplePersonaRol.setIdPersonaRol(sampleId);
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA completarPersona
    // ==========================================

    // ==========================================
    // PRUEBAS PARA completarRol
    // ==========================================

    // ==========================================
    // PRUEBAS PARA completarClinica
    // ==========================================

    // ==========================================
    // PRUEBAS PARA filtrarPorPersona
    // ==========================================

    @Test
    @DisplayName("filtrarPorPersona - Éxito (Aplica el filtro por persona a la búsqueda y al conteo)")
    void filtrarPorPersona_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        List<PersonaRol> filtrados = List.of(samplePersonaRol);
        when(prServiceMock.findRangeByCriterio("idPersona.idPersona", persona.getIdPersona(), 0, 10)).thenReturn(filtrados);
        when(prServiceMock.contarByCriterio("idPersona.idPersona", persona.getIdPersona())).thenReturn(1);

        personaRolModel.filtrarPorPersona(persona);

        assertSame(persona, personaRolModel.getPersonaFiltro());
        assertSame(filtrados, personaRolModel.buscarRegistros(0, 10));
        assertEquals(1, personaRolModel.contar());
    }

    @Test
    @DisplayName("filtrarPorPersona - Fallos (Persona nula limpia el filtro y vuelve a listar todo)")
    void filtrarPorPersona_Failures() {
        List<PersonaRol> todos = List.of(samplePersonaRol);
        when(prServiceMock.findRange(0, 10)).thenReturn(todos);
        personaRolModel.filtrarPorPersona(new Persona(UUID.randomUUID()));

        personaRolModel.filtrarPorPersona(null);

        assertNull(personaRolModel.getPersonaFiltro());
        assertSame(todos, personaRolModel.buscarRegistros(0, 10));
        verify(prServiceMock, never()).findRangeByCriterio(any(), any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Asigna la persona filtrada al nuevo registro solo si hay filtro)")
    void nuevo_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        personaRolModel.filtrarPorPersona(persona);

        personaRolModel.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, personaRolModel.getEstado());
        assertSame(persona, personaRolModel.getRegistroActual().getIdPersona());

        personaRolModel.filtrarPorPersona(null);
        personaRolModel.nuevo();

        assertNull(personaRolModel.getRegistroActual().getIdPersona());
    }

    // ==========================================
    // PRUEBAS PARA guardarHandler
    // ==========================================

    @Test
    @DisplayName("guardarHandler - Éxito (Sin duplicado guarda: CREAR llama crear, MODIFICAR llama actualizar)")
    void guardarHandler_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clínica Central");

        // CREAR: no se excluye ningún registro al validar el duplicado
        PersonaRol nuevo = new PersonaRol();
        nuevo.setIdPersona(persona);
        nuevo.setIdRol(rol);
        nuevo.setIdClinica(clinica);
        when(prServiceMock.existeRolEnClinica(persona.getIdPersona(), rol.getIdRol(), clinica.getIdClinica(), null))
                .thenReturn(false);
        personaRolModel.setRegistroActual(nuevo);
        personaRolModel.setEstado(ESTADO_CRUD.CREAR);

        personaRolModel.guardarHandler(null);

        verify(prServiceMock, times(1)).crear(nuevo);
        assertEquals(ESTADO_CRUD.NINGUNO, personaRolModel.getEstado());

        // MODIFICAR: se excluye el propio registro al validar el duplicado
        PersonaRol existente = new PersonaRol(UUID.randomUUID());
        existente.setIdPersona(persona);
        existente.setIdRol(rol);
        existente.setIdClinica(clinica);
        when(prServiceMock.existeRolEnClinica(persona.getIdPersona(), rol.getIdRol(), clinica.getIdClinica(),
                existente.getIdPersonaRol())).thenReturn(false);
        personaRolModel.setRegistroActual(existente);
        personaRolModel.setEstado(ESTADO_CRUD.MODIFICAR);

        personaRolModel.guardarHandler(null);

        verify(prServiceMock, times(1)).actualizar(existente);
        assertEquals(ESTADO_CRUD.NINGUNO, personaRolModel.getEstado());

        // Sin persona o sin rol no hay duplicado que validar, pero igual guarda
        PersonaRol incompleto = new PersonaRol();
        personaRolModel.setRegistroActual(incompleto);
        personaRolModel.setEstado(ESTADO_CRUD.CREAR);

        personaRolModel.guardarHandler(null);

        verify(prServiceMock, times(1)).crear(incompleto);
        verify(prServiceMock, times(2)).existeRolEnClinica(any(), any(), any(), any());
    }

    @Test
    @DisplayName("guardarHandler - Fallos (Duplicado muestra error sin guardar; error al validar se propaga)")
    void guardarHandler_Failures() {
        Persona persona = new Persona(UUID.randomUUID());
        Rol rol = new Rol(UUID.randomUUID());
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clínica Central");
        PersonaRol nuevo = new PersonaRol();
        nuevo.setIdPersona(persona);
        nuevo.setIdRol(rol);
        nuevo.setIdClinica(clinica);
        personaRolModel.setRegistroActual(nuevo);
        personaRolModel.setEstado(ESTADO_CRUD.CREAR);

        // La persona ya tiene ese rol en la clínica
        when(prServiceMock.existeRolEnClinica(persona.getIdPersona(), rol.getIdRol(), clinica.getIdClinica(), null))
                .thenReturn(true);

        personaRolModel.guardarHandler(null);

        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals("Esta persona ya tiene asignado ese rol en la clínica seleccionada.",
                faces.ultimoMensaje().getSummary());
        assertEquals(ESTADO_CRUD.CREAR, personaRolModel.getEstado());
        verify(prServiceMock, never()).crear(any());

        // Falla la consulta de duplicados: la excepción no se oculta
        RuntimeException errorConsulta = new IllegalStateException("Query failed");
        when(prServiceMock.existeRolEnClinica(persona.getIdPersona(), rol.getIdRol(), clinica.getIdClinica(), null))
                .thenThrow(errorConsulta);

        RuntimeException lanzada = assertThrows(RuntimeException.class,
                () -> personaRolModel.guardarHandler(null));

        assertSame(errorConsulta, lanzada);
        verify(prServiceMock, never()).crear(any());
        verify(prServiceMock, never()).actualizar(any());
    }
}
