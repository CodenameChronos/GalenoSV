package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;

class SesionTest {

    private Sesion sesion;
    private Persona persona;
    private Clinica clinica;
    private Rol rol;
    private PersonaRol personaRol;

    @BeforeEach
    void setUp() {
        sesion = new Sesion();

        persona = new Persona(UUID.randomUUID());
        persona.setNombres("Ana");
        persona.setApellidos("Pérez");

        clinica = new Clinica(UUID.randomUUID(), "Clínica Central");

        rol = new Rol(UUID.randomUUID());
        rol.setNombre("Doctor");

        personaRol = new PersonaRol(UUID.randomUUID());
        personaRol.setIdPersona(persona);
        personaRol.setIdClinica(clinica);
        personaRol.setIdRol(rol);
    }

    // ==========================================
    // PRUEBAS PARA getNombrePersona
    // ==========================================

    @Test
    @DisplayName("getNombrePersona - Éxito (Nombres y apellidos concatenados)")
    void getNombrePersona_Success() {
        sesion.setPersona(persona);

        assertEquals("Ana Pérez", sesion.getNombrePersona());
    }

    @Test
    @DisplayName("getNombrePersona - Fallos (Sin persona retorna cadena vacía)")
    void getNombrePersona_Failures() {
        assertEquals("", sesion.getNombrePersona());
    }

    // ==========================================
    // PRUEBAS PARA getNombreRol
    // ==========================================

    @Test
    @DisplayName("getNombreRol - Éxito (Retorna el nombre del rol)")
    void getNombreRol_Success() {
        sesion.setRol(rol);

        assertEquals("Doctor", sesion.getNombreRol());
    }

    @Test
    @DisplayName("getNombreRol - Fallos (Sin rol retorna cadena vacía)")
    void getNombreRol_Failures() {
        assertEquals("", sesion.getNombreRol());
    }

    // ==========================================
    // PRUEBAS PARA getNombreClinica
    // ==========================================

    @Test
    @DisplayName("getNombreClinica - Éxito (Retorna el nombre de la clínica)")
    void getNombreClinica_Success() {
        sesion.setClinica(clinica);

        assertEquals("Clínica Central", sesion.getNombreClinica());
    }

    @Test
    @DisplayName("getNombreClinica - Fallos (Sin clínica retorna cadena vacía)")
    void getNombreClinica_Failures() {
        assertEquals("", sesion.getNombreClinica());
    }

    // ==========================================
    // PRUEBAS PARA establecerSesion
    // ==========================================

    @Test
    @DisplayName("establecerSesion - Éxito (Toma persona, clínica y rol de la asignación)")
    void establecerSesion_Success() {
        sesion.establecerSesion(personaRol);

        assertSame(persona, sesion.getPersona());
        assertSame(clinica, sesion.getClinica());
        assertSame(rol, sesion.getRol());
    }

    @Test
    @DisplayName("establecerSesion - Fallos (Asignación nula no modifica la sesión actual)")
    void establecerSesion_Failures() {
        sesion.establecerSesion(personaRol);

        sesion.establecerSesion(null);

        assertSame(persona, sesion.getPersona());
        assertSame(clinica, sesion.getClinica());
        assertSame(rol, sesion.getRol());
    }

    // ==========================================
    // PRUEBAS PARA cerrarSesion
    // ==========================================

    @Test
    @DisplayName("cerrarSesion - Éxito (Limpia persona, clínica y rol)")
    void cerrarSesion_Success() {
        sesion.establecerSesion(personaRol);

        sesion.cerrarSesion();

        assertNull(sesion.getPersona());
        assertNull(sesion.getClinica());
        assertNull(sesion.getRol());
    }
}
