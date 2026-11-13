package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
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

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.SesionService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.SesionModel;

@ExtendWith(MockitoExtension.class)
class SesionModelTest {

    @Mock
    private SesionService sesionServiceMock;

    @Mock
    private Sesion sesionMock;

    private SesionModel sesionModel;

    private FacesTestSupport faces;
    private Clinica clinica;
    private Rol rol;
    private PersonaRol personaRol;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, SesionModel.class, "sesionModel");
        faces = FacesTestSupport.abrir();

        clinica = new Clinica(UUID.randomUUID(), "Clínica Central");
        rol = new Rol(UUID.randomUUID());
        rol.setNombre("Doctor");

        personaRol = new PersonaRol(UUID.randomUUID());
        personaRol.setIdClinica(clinica);
        personaRol.setIdRol(rol);
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA isRolHabilitado
    // ==========================================

    @Test
    @DisplayName("isRolHabilitado - Éxito (Habilitado cuando hay clínica elegida)")
    void isRolHabilitado_Success() {
        sesionModel.setClinica(clinica);

        assertTrue(sesionModel.isRolHabilitado());
    }

    @Test
    @DisplayName("isRolHabilitado - Fallos (Sin clínica no está habilitado)")
    void isRolHabilitado_Failures() {
        assertFalse(sesionModel.isRolHabilitado());
    }

    // ==========================================
    // PRUEBAS PARA isPersonaHabilitada
    // ==========================================

    @Test
    @DisplayName("isPersonaHabilitada - Éxito (Habilitado cuando hay clínica y rol)")
    void isPersonaHabilitada_Success() {
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);

        assertTrue(sesionModel.isPersonaHabilitada());
    }

    @Test
    @DisplayName("isPersonaHabilitada - Fallos (Falta la clínica, el rol o ambos)")
    void isPersonaHabilitada_Failures() {
        assertFalse(sesionModel.isPersonaHabilitada());

        sesionModel.setClinica(clinica);
        assertFalse(sesionModel.isPersonaHabilitada());

        sesionModel.setClinica(null);
        sesionModel.setRol(rol);
        assertFalse(sesionModel.isPersonaHabilitada());
    }

    // ==========================================
    // PRUEBAS PARA alCambiarClinica / alCambiarRol
    // ==========================================

    @Test
    @DisplayName("alCambiarClinica - Éxito (Limpia rol y persona elegida)")
    void alCambiarClinica_Success() {
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        sesionModel.setSesionPersonaRol(personaRol);

        sesionModel.alCambiarClinica();

        assertNull(sesionModel.getRol());
        assertNull(sesionModel.getSesionPersonaRol());
        assertSame(clinica, sesionModel.getClinica());
    }

    @Test
    @DisplayName("alCambiarRol - Éxito (Limpia solo la persona elegida)")
    void alCambiarRol_Success() {
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        sesionModel.setSesionPersonaRol(personaRol);

        sesionModel.alCambiarRol();

        assertNull(sesionModel.getSesionPersonaRol());
        assertSame(clinica, sesionModel.getClinica());
        assertSame(rol, sesionModel.getRol());
    }

    // ==========================================
    // PRUEBAS PARA completarClinica
    // ==========================================

    @Test
    @DisplayName("completarClinica - Éxito (Retorna las clínicas que encuentra el servicio)")
    void completarClinica_Success() {
        List<Clinica> encontradas = List.of(clinica);
        when(sesionServiceMock.buscarClinicas("cen", 10)).thenReturn(encontradas);

        assertSame(encontradas, sesionModel.completarClinica("cen"));
    }

    @Test
    @DisplayName("completarClinica - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarClinica_Failures() {
        assertTrue(sesionModel.completarClinica(null).isEmpty());
        assertTrue(sesionModel.completarClinica("").isEmpty());
        assertTrue(sesionModel.completarClinica("   ").isEmpty());

        verify(sesionServiceMock, never()).buscarClinicas(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarRol
    // ==========================================

    @Test
    @DisplayName("completarRol - Éxito (Con clínica elegida retorna los roles encontrados)")
    void completarRol_Success() {
        List<Rol> encontrados = List.of(rol);
        sesionModel.setClinica(clinica);
        when(sesionServiceMock.buscarRoles("doc", 10)).thenReturn(encontrados);

        assertSame(encontrados, sesionModel.completarRol("doc"));
    }

    @Test
    @DisplayName("completarRol - Fallos (Sin clínica o con texto nulo/en blanco retorna lista vacía)")
    void completarRol_Failures() {
        // Sin clínica
        assertTrue(sesionModel.completarRol("doc").isEmpty());

        // Con clínica pero texto inválido
        sesionModel.setClinica(clinica);
        assertTrue(sesionModel.completarRol(null).isEmpty());
        assertTrue(sesionModel.completarRol("  ").isEmpty());

        verify(sesionServiceMock, never()).buscarRoles(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarPersonaRol
    // ==========================================

    @Test
    @DisplayName("completarPersonaRol - Éxito (Con clínica y rol retorna las personas encontradas)")
    void completarPersonaRol_Success() {
        List<PersonaRol> encontradas = List.of(personaRol);
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        when(sesionServiceMock.buscarPersonaRol(clinica, rol, "ana", 30)).thenReturn(encontradas);

        assertSame(encontradas, sesionModel.completarPersonaRol("ana"));
    }

    @Test
    @DisplayName("completarPersonaRol - Fallos (Falta clínica, rol o texto retorna lista vacía)")
    void completarPersonaRol_Failures() {
        // Sin clínica ni rol
        assertTrue(sesionModel.completarPersonaRol("ana").isEmpty());

        // Solo clínica
        sesionModel.setClinica(clinica);
        assertTrue(sesionModel.completarPersonaRol("ana").isEmpty());

        // Clínica y rol pero texto inválido
        sesionModel.setRol(rol);
        assertTrue(sesionModel.completarPersonaRol(null).isEmpty());
        assertTrue(sesionModel.completarPersonaRol("   ").isEmpty());

        verify(sesionServiceMock, never()).buscarPersonaRol(any(), any(), any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA establecerSesion
    // ==========================================

    @Test
    @DisplayName("establecerSesion - Éxito (Clínica y rol coinciden, establece la sesión y avisa)")
    void establecerSesion_Success() {
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        sesionModel.setSesionPersonaRol(personaRol);

        sesionModel.establecerSesion();

        verify(sesionMock, times(1)).establecerSesion(personaRol);
        assertEquals(FacesMessage.SEVERITY_INFO, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("mensaje.exito"), faces.ultimoMensaje().getSummary());
    }

    @Test
    @DisplayName("establecerSesion - Fallos (Sin persona elegida o datos inconsistentes no establece la sesión)")
    void establecerSesion_Failures() {
        // Sin persona elegida: no hace nada
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        sesionModel.setSesionPersonaRol(null);
        sesionModel.establecerSesion();
        assertTrue(faces.mensajes().isEmpty());

        // La clínica de la asignación no coincide con la elegida
        Clinica otraClinica = new Clinica(UUID.randomUUID(), "Otra Clínica");
        sesionModel.setClinica(otraClinica);
        sesionModel.setSesionPersonaRol(personaRol);
        sesionModel.establecerSesion();
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());
        assertEquals(FacesTestSupport.texto("sesion.error.inconsistente"), faces.ultimoMensaje().getSummary());

        // El rol de la asignación no coincide con el elegido
        Rol otroRol = new Rol(UUID.randomUUID());
        otroRol.setNombre("Enfermero");
        sesionModel.setClinica(clinica);
        sesionModel.setRol(otroRol);
        sesionModel.establecerSesion();
        assertEquals(FacesMessage.SEVERITY_ERROR, faces.ultimoMensaje().getSeverity());

        assertEquals(2, faces.mensajes().size());
        verify(sesionMock, never()).establecerSesion(any());
    }

    // ==========================================
    // PRUEBAS PARA cerrarSesion
    // ==========================================

    @Test
    @DisplayName("cerrarSesion - Éxito (Cierra la sesión y limpia la selección)")
    void cerrarSesion_Success() {
        sesionModel.setClinica(clinica);
        sesionModel.setRol(rol);
        sesionModel.setSesionPersonaRol(personaRol);

        sesionModel.cerrarSesion();

        verify(sesionMock, times(1)).cerrarSesion();
        assertNull(sesionModel.getClinica());
        assertNull(sesionModel.getRol());
        assertNull(sesionModel.getSesionPersonaRol());
    }
}
