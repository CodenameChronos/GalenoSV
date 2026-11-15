package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.Sesion;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

import static org.junit.jupiter.api.Assertions.*;

class SesionTest {

    @Test
    @DisplayName("Nombres - Retorna cadenas vacías cuando no hay datos de sesión")
    void nombres_SinDatos_RetornaVacio() {
        Sesion sesion = new Sesion();

        assertEquals("", sesion.getNombrePersona());
        assertEquals("", sesion.getNombreRol());
        assertEquals("", sesion.getNombreClinica());
    }

    @Test
    @DisplayName("Nombres - Construye los nombres de persona, rol y clínica")
    void nombres_ConDatos_RetornaValores() {
        Sesion sesion = new Sesion();
        Persona persona = new Persona(UUID.randomUUID());
        persona.setNombres(" Ana ");
        persona.setApellidos(" Pérez ");
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Doctora");
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clínica Central");

        sesion.setPersona(persona);
        sesion.setRol(rol);
        sesion.setClinica(clinica);

        assertEquals("Ana Pérez", sesion.getNombrePersona());
        assertEquals("Doctora", sesion.getNombreRol());
        assertEquals("Clínica Central", sesion.getNombreClinica());
    }

    @Test
    @DisplayName("establecerSesion - Copia persona, clínica y rol desde la asignación")
    void establecerSesion_ConAsignacion_EstableceDatos() {
        Sesion sesion = new Sesion();
        Persona persona = new Persona(UUID.randomUUID());
        Clinica clinica = new Clinica(UUID.randomUUID(), "Clínica Central");
        Rol rol = new Rol(UUID.randomUUID());
        PersonaRol asignacion = new PersonaRol(UUID.randomUUID());
        asignacion.setIdPersona(persona);
        asignacion.setIdClinica(clinica);
        asignacion.setIdRol(rol);

        sesion.establecerSesion(asignacion);

        assertSame(persona, sesion.getPersona());
        assertSame(clinica, sesion.getClinica());
        assertSame(rol, sesion.getRol());
    }

    @Test
    @DisplayName("establecerSesion - No modifica la sesión cuando la asignación es nula")
    void establecerSesion_Nula_NoModificaDatos() {
        Sesion sesion = new Sesion();
        Persona persona = new Persona(UUID.randomUUID());
        sesion.setPersona(persona);

        sesion.establecerSesion(null);

        assertSame(persona, sesion.getPersona());
        assertNull(sesion.getClinica());
        assertNull(sesion.getRol());
    }

    @Test
    @DisplayName("cerrarSesion - Limpia persona, clínica y rol")
    void cerrarSesion_LimpiaDatos() {
        Sesion sesion = new Sesion();
        sesion.setPersona(new Persona(UUID.randomUUID()));
        sesion.setClinica(new Clinica(UUID.randomUUID(), "Clínica Central"));
        sesion.setRol(new Rol(UUID.randomUUID()));

        sesion.cerrarSesion();

        assertNull(sesion.getPersona());
        assertNull(sesion.getClinica());
        assertNull(sesion.getRol());
        assertEquals("", sesion.getNombrePersona());
        assertEquals("", sesion.getNombreRol());
        assertEquals("", sesion.getNombreClinica());
    }
}
