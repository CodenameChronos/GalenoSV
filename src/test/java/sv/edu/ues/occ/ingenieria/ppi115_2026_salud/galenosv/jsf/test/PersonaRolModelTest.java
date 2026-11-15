package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

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
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, PersonaRolModel.class, "personaRolModel");
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

}
