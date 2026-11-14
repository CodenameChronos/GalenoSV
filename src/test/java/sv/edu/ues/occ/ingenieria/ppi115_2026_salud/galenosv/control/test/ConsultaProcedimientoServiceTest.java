package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsultaProcedimientoServiceTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private ProcedimientoPasoService procedimientoPasoServiceMock;

    @Mock
    private PersonaRolService personaRolServiceMock;

    @Mock
    private TypedQuery<ConsultaProcedimiento> cpQueryMock;

    @Mock
    private TypedQuery<Procedimiento> procQueryMock;

    @Mock
    private TypedQuery<Long> countQueryMock;

    @InjectMocks
    private ConsultaProcedimientoService service;

    private UUID idConsulta;
    private UUID idProcedimiento;
    private Clinica clinica;
    private Consulta consulta;
    private Procedimiento procedimiento;
    private ConsultaProcedimiento consultaProcedimiento;
    private ProcedimientoPaso pasoInicial;
    private PersonaRol responsable;

    @BeforeEach
    void setUp() {
        idConsulta = UUID.randomUUID();
        idProcedimiento = UUID.randomUUID();

        clinica = new Clinica();
        clinica.setIdClinica(UUID.randomUUID());

        consulta = new Consulta();
        consulta.setIdConsulta(idConsulta);

        procedimiento = new Procedimiento();
        procedimiento.setIdProcedimiento(idProcedimiento);

        consultaProcedimiento = new ConsultaProcedimiento();
        consultaProcedimiento.setIdConsulta(consulta);
        consultaProcedimiento.setIdProcedimiento(procedimiento);

        pasoInicial = new ProcedimientoPaso();
        pasoInicial.setIdProcedimientoPaso(UUID.randomUUID());

        responsable = new PersonaRol();
        responsable.setIdPersonaRol(UUID.randomUUID());
    }

    // ==========================================
    // PRUEBAS PARA buscarPorNombreProcedimiento
    // ==========================================
    @Test
    @DisplayName("buscarPorNombreProcedimiento - Éxito al buscar por nombre")
    void buscarPorNombreProcedimiento_Success() {
        List<ConsultaProcedimiento> lista = List.of(consultaProcedimiento);
        when(emMock.createNamedQuery("ConsultaProcedimiento.findByNombreProcedimiento", ConsultaProcedimiento.class))
                .thenReturn(cpQueryMock);
        when(cpQueryMock.setParameter("texto", "%sutura%")).thenReturn(cpQueryMock);
        when(cpQueryMock.setMaxResults(10)).thenReturn(cpQueryMock);
        when(cpQueryMock.getResultList()).thenReturn(lista);

        List<ConsultaProcedimiento> resultado = service.buscarPorNombreProcedimiento("  Sutura ", 10);

        assertEquals(lista, resultado);
        verify(cpQueryMock, times(1)).setParameter("texto", "%sutura%");
        verify(cpQueryMock, times(1)).setMaxResults(10);
    }

    @Test
    @DisplayName("buscarPorNombreProcedimiento - Texto nulo (Lanza NullPointerException)")
    void buscarPorNombreProcedimiento_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarPorNombreProcedimiento(null, 10));
        
    }

    // ==========================================
    // PRUEBAS PARA listarPorConsulta
    // ==========================================
    @Test
    @DisplayName("listarPorConsulta - Éxito al listar procedimientos de una consulta")
    void listarPorConsulta_Success() {
        List<ConsultaProcedimiento> lista = List.of(consultaProcedimiento);
        when(emMock.createNamedQuery("ConsultaProcedimiento.listarPorConsulta", ConsultaProcedimiento.class))
                .thenReturn(cpQueryMock);
        when(cpQueryMock.setParameter("consulta", idConsulta)).thenReturn(cpQueryMock);
        when(cpQueryMock.setFirstResult(0)).thenReturn(cpQueryMock);
        when(cpQueryMock.setMaxResults(10)).thenReturn(cpQueryMock);
        when(cpQueryMock.getResultList()).thenReturn(lista);

        List<ConsultaProcedimiento> resultado = service.listarPorConsulta(idConsulta, 0, 10);

        assertEquals(lista, resultado);
        verify(cpQueryMock, times(1)).setParameter("consulta", idConsulta);
    }

    @Test
    @DisplayName("listarPorConsulta - Fallo en consulta (Propaga excepción)")
    void listarPorConsulta_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ConsultaProcedimiento.listarPorConsulta", ConsultaProcedimiento.class))
                .thenThrow(new RuntimeException("Error JPQL"));

        assertThrows(RuntimeException.class, () -> service.listarPorConsulta(idConsulta, 0, 10));
    }

    // ==========================================
    // PRUEBAS PARA contarPorConsulta
    // ==========================================
    @Test
    @DisplayName("contarPorConsulta - Éxito al obtener conteo por consulta")
    void contarPorConsulta_Success() {
        when(emMock.createNamedQuery("ConsultaProcedimiento.contarPorConsulta", Long.class))
                .thenReturn(countQueryMock);
        when(countQueryMock.setParameter("consulta", idConsulta)).thenReturn(countQueryMock);
        when(countQueryMock.getSingleResult()).thenReturn(3L);

        long resultado = service.contarPorConsulta(idConsulta);

        assertEquals(3L, resultado);
        verify(countQueryMock, times(1)).setParameter("consulta", idConsulta);
    }

    @Test
    @DisplayName("contarPorConsulta - Fallo en consulta (Propaga excepción)")
    void contarPorConsulta_QueryError_ThrowsException() {
        when(emMock.createNamedQuery("ConsultaProcedimiento.contarPorConsulta", Long.class))
                .thenThrow(new RuntimeException("Error en conteo"));

        assertThrows(RuntimeException.class, () -> service.contarPorConsulta(idConsulta));
    }

    // ==========================================
    // PRUEBAS PARA buscarProcedimientosActivos
    // ==========================================
    @Test
    @DisplayName("buscarProcedimientosActivos - Éxito al buscar procedimientos activos")
    void buscarProcedimientosActivos_Success() {
        List<Procedimiento> lista = List.of(procedimiento);
        when(emMock.createNamedQuery("Procedimiento.findActiveByNombre", Procedimiento.class))
                .thenReturn(procQueryMock);
        when(procQueryMock.setParameter("nombre", "%curación%")).thenReturn(procQueryMock);
        when(procQueryMock.setMaxResults(5)).thenReturn(procQueryMock);
        when(procQueryMock.getResultList()).thenReturn(lista);

        List<Procedimiento> resultado = service.buscarProcedimientosActivos(" Curación ", 5);

        assertEquals(lista, resultado);
    }

    @Test
    @DisplayName("buscarProcedimientosActivos - Texto nulo (Lanza NullPointerException)")
    void buscarProcedimientosActivos_NullText_ThrowsException() {
        assertThrows(NullPointerException.class, () -> service.buscarProcedimientosActivos(null, 5));
    }

    // ==========================================
    // PRUEBAS PARA crearConPasoInicial
    // ==========================================
    @Test
    @DisplayName("crearConPasoInicial - Éxito al crear procedimiento y su primer paso")
    void crearConPasoInicial_Success() {
        when(procedimientoPasoServiceMock.obtenerPasoInicial(idProcedimiento)).thenReturn(pasoInicial);
        when(personaRolServiceMock.listarPersonalExcluyendoRol(clinica, PersonaRolService.ROL_PACIENTE))
                .thenReturn(List.of(responsable));

        service.crearConPasoInicial(consultaProcedimiento, clinica);

        verify(emMock, times(1)).persist(consultaProcedimiento);
        verify(emMock, times(1)).persist(any(ConsultaProcedimientoPaso.class));
    }

    @Test
    @DisplayName("crearConPasoInicial - Clínica nula o sin personal disponible (Lanza ReglaNegocioException)")
    void crearConPasoInicial_ValidationOrNoStaff_ThrowsReglaNegocioException() {
        // Caso 1: Clínica nula
        assertThrows(ReglaNegocioException.class,
                () -> service.crearConPasoInicial(consultaProcedimiento, null));

        // Caso 2: Clínica válida pero sin personal disponible
        when(procedimientoPasoServiceMock.obtenerPasoInicial(idProcedimiento)).thenReturn(pasoInicial);
        when(personaRolServiceMock.listarPersonalExcluyendoRol(clinica, PersonaRolService.ROL_PACIENTE))
                .thenReturn(Collections.emptyList());

        assertThrows(ReglaNegocioException.class,
                () -> service.crearConPasoInicial(consultaProcedimiento, clinica));

        verify(emMock, never()).persist(any());
    }

}
