package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesionServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<Clinica> clinicaQueryMock;

    @Mock
    private TypedQuery<Rol> rolQueryMock;

    @Mock
    private TypedQuery<PersonaRol> personaRolQueryMock;

    @InjectMocks
    private SesionService service;

    private Clinica clinica;
    private Rol rol;
    private PersonaRol personaRol;

    @BeforeEach
    void setUp() {
        clinica = new Clinica();
        rol = new Rol();
        personaRol = new PersonaRol();
    }

    // ==========================================
    // PRUEBAS PARA buscarClinicas
    // ==========================================

    @Test
    @DisplayName("buscarClinicas - Éxito al buscar clínicas por nombre")
    void buscarClinicas_Success() {
        List<Clinica> lista = List.of(clinica);
        when(emMock.createNamedQuery("Clinica.findByNombreLike", Clinica.class)).thenReturn(clinicaQueryMock);
        when(clinicaQueryMock.setParameter("nombre", "%central%")).thenReturn(clinicaQueryMock);
        when(clinicaQueryMock.setMaxResults(10)).thenReturn(clinicaQueryMock);
        when(clinicaQueryMock.getResultList()).thenReturn(lista);

        List<Clinica> resultado = service.buscarClinicas(" Central ", 10);

        assertEquals(lista, resultado);
        verify(clinicaQueryMock, times(1)).setParameter("nombre", "%central%");
    }

    @Test
    @DisplayName("buscarClinicas - Texto nulo (Lanza NullPointerException)")
    void buscarClinicas_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarClinicas(null, 10));
    }

    // ==========================================
    // PRUEBAS PARA buscarRoles
    // ==========================================

    @Test
    @DisplayName("buscarRoles - Éxito al buscar roles por nombre")
    void buscarRoles_Success() {
        List<Rol> lista = List.of(rol);
        when(emMock.createNamedQuery("Rol.findByNombreLike", Rol.class)).thenReturn(rolQueryMock);
        when(rolQueryMock.setParameter("nombre", "%médico%")).thenReturn(rolQueryMock);
        when(rolQueryMock.setMaxResults(10)).thenReturn(rolQueryMock);
        when(rolQueryMock.getResultList()).thenReturn(lista);

        List<Rol> resultado = service.buscarRoles(" Médico ", 10);

        assertEquals(lista, resultado);
        verify(rolQueryMock, times(1)).setParameter("nombre", "%médico%");
    }

    @Test
    @DisplayName("buscarRoles - Texto nulo (Lanza NullPointerException)")
    void buscarRoles_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarRoles(null, 10));
    }

    // ==========================================
    // PRUEBAS PARA buscarPersonaRol
    // ==========================================

    @Test
    @DisplayName("buscarPersonaRol - Éxito al buscar asignaciones por clínica, rol y nombre")
    void buscarPersonaRol_Success() {
        List<PersonaRol> lista = List.of(personaRol);
        when(emMock.createNamedQuery("PersonaRol.findByNombreApellidosClinica", PersonaRol.class))
                .thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("clinica", clinica)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("rol", rol)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("texto", "%lópez%")).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setMaxResults(10)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.getResultList()).thenReturn(lista);

        List<PersonaRol> resultado = service.buscarPersonaRol(clinica, rol, " López ", 10);

        assertEquals(lista, resultado);
        verify(personaRolQueryMock, times(1)).setParameter("texto", "%lópez%");
    }

    @Test
    @DisplayName("buscarPersonaRol - Texto nulo (Lanza NullPointerException)")
    void buscarPersonaRol_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPersonaRol(clinica, rol, null, 10));
    }
}
