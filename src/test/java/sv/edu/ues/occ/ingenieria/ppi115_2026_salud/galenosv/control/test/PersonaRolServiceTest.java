package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonaRolServiceTest {
    
    @Mock
    private EntityManager emMock;

    @Mock
    private TypedQuery<PersonaRol> personaRolQueryMock;

    @Mock
    private TypedQuery<Documento> documentoQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private PersonaRolService service;

    private UUID idPersona;
    private UUID idRol;
    private UUID idClinica;
    private UUID idPersonaRol;

    private Clinica clinica;
    private Persona persona;
    private PersonaRol personaRol;
    private Documento documento;

    @BeforeEach
    void setUp() {
        idPersona = UUID.randomUUID();
        idRol = UUID.randomUUID();
        idClinica = UUID.randomUUID();
        idPersonaRol = UUID.randomUUID();

        clinica = new Clinica();
        clinica.setIdClinica(idClinica);

        persona = new Persona();
        persona.setIdPersona(idPersona);

        personaRol = new PersonaRol();
        personaRol.setIdPersonaRol(idPersonaRol);
        personaRol.setIdPersona(persona);
        personaRol.setIdClinica(clinica);

        documento = new Documento();
        documento.setIdDocumento(UUID.randomUUID());
        documento.setIdPersona(persona);
    }

    // ==========================================
    // PRUEBAS PARA buscarPorNombresApellidos
    // ==========================================

    @Test
    @DisplayName("buscarPorNombresApellidos - Éxito al buscar por fragmento de nombre")
    void buscarPorNombresApellidos_Success() {
        List<PersonaRol> lista = List.of(personaRol);
        when(emMock.createNamedQuery("PersonaRol.findByNombresApellidos", PersonaRol.class))
                .thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("nombre", "%juan%")).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setMaxResults(10)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.getResultList()).thenReturn(lista);

        List<PersonaRol> resultado = service.buscarPorNombresApellidos("  Juan ", 10);

        assertEquals(lista, resultado);
        verify(personaRolQueryMock, times(1)).setParameter("nombre", "%juan%");
    }

    @Test
    @DisplayName("buscarPorNombresApellidos - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombresApellidos_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombresApellidos(null, 10));
    }

    // ==========================================
    // PRUEBAS PARA existeRolEnClinica
    // ==========================================

    @Test
    @DisplayName("existeRolEnClinica - Éxito cuando ya existe asignación en clínica (Retorna true)")
    void existeRolEnClinica_Exists_ReturnsTrue() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPersona", idPersona)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idRol", idRol)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idClinica", idClinica)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idExcluir", idPersonaRol)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(1L);

        boolean resultado = service.existeRolEnClinica(idPersona, idRol, idClinica, idPersonaRol);

        assertTrue(resultado);
        verify(countQueryMock, times(1)).setParameter("idClinica", idClinica);
        verify(countQueryMock, times(1)).setParameter("idExcluir", idPersonaRol);
    }

    @Test
    @DisplayName("existeRolEnClinica - Sin coincidencia y clínica nula (Retorna false)")
    void existeRolEnClinica_NotExistsWithNullClinica_ReturnsFalse() {
        when(emMock.createQuery(anyString(), eq(Long.class))).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idPersona", idPersona)).thenReturn(countQueryMock);
        when(countQueryMock.setParameter("idRol", idRol)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(0L);

        boolean resultado = service.existeRolEnClinica(idPersona, idRol, null, null);

        assertFalse(resultado);
        verify(countQueryMock, never()).setParameter(eq("idClinica"), any());
    }

    // ==========================================
    // PRUEBAS PARA listarPersonalExcluyendoRol
    // ==========================================

    @Test
    @DisplayName("listarPersonalExcluyendoRol - Éxito al obtener personal activo")
    void listarPersonalExcluyendoRol_Success() {
        List<PersonaRol> lista = List.of(personaRol);
        when(emMock.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("clinica", clinica)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("rol", "paciente")).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.getResultList()).thenReturn(lista);

        List<PersonaRol> resultado = service.listarPersonalExcluyendoRol(clinica, " Paciente ");

        assertEquals(lista, resultado);
        verify(personaRolQueryMock, times(1)).setParameter("rol", "paciente");
    }

    @Test
    @DisplayName("listarPersonalExcluyendoRol - Nombre de rol nulo (Lanza NullPointerException)")
    void listarPersonalExcluyendoRol_NullRoleName_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> service.listarPersonalExcluyendoRol(clinica, null));
    }

    // ==========================================
    // PRUEBAS PARA buscarPacientes
    // ==========================================

    @Test
    @DisplayName("buscarPacientes - Éxito al buscar pacientes de una clínica")
    void buscarPacientes_Success() {
        List<PersonaRol> lista = List.of(personaRol);
        when(emMock.createQuery(anyString(), eq(PersonaRol.class))).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("clinica", clinica)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("rol", "paciente")).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setParameter("texto", "%pérez%")).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.setMaxResults(15)).thenReturn(personaRolQueryMock);
        when(personaRolQueryMock.getResultList()).thenReturn(lista);

        List<PersonaRol> resultado = service.buscarPacientes(clinica, " Pérez ", 15);

        assertEquals(lista, resultado);
        verify(personaRolQueryMock, times(1)).setParameter("texto", "%pérez%");
    }

    @Test
    @DisplayName("buscarPacientes - Texto nulo (Lanza NullPointerException)")
    void buscarPacientes_NullText_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> service.buscarPacientes(clinica, null, 15));
    }

    // ==========================================
    // PRUEBAS PARA listarDocumentosDePersonas
    // ==========================================

    @Test
    @DisplayName("listarDocumentosDePersonas - Éxito al cargar documentos de varias personas")
    void listarDocumentosDePersonas_Success() {
        List<Persona> personas = List.of(persona);
        List<Documento> documentos = List.of(documento);

        when(emMock.createQuery(anyString(), eq(Documento.class))).thenReturn(documentoQueryMock);
        when(documentoQueryMock.setParameter("personas", personas)).thenReturn(documentoQueryMock);
        when(documentoQueryMock.getResultList()).thenReturn(documentos);

        List<Documento> resultado = service.listarDocumentosDePersonas(personas);

        assertEquals(documentos, resultado);
        verify(documentoQueryMock, times(1)).setParameter("personas", personas);
    }

    @Test
    @DisplayName("listarDocumentosDePersonas - Colección vacía o nula (Retorna lista vacía sin consultar BD)")
    void listarDocumentosDePersonas_EmptyOrNullCollection_ReturnsEmptyList() {
        List<Documento> r1 = service.listarDocumentosDePersonas(null);
        List<Documento> r2 = service.listarDocumentosDePersonas(Collections.emptyList());

        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());
        verify(emMock, never()).createQuery(anyString(), eq(Documento.class));
    }
    
}
