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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ConsultaProcedimientoPasoModel;

@ExtendWith(MockitoExtension.class)
class ConsultaProcedimientoPasoModelTest {

    @Mock
    private ConsultaProcedimientoPasoService coppServiceMock;

    @Mock
    private ConsultaProcedimientoService conspServiceMock;

    @Mock
    private PersonaRolService prServiceMock;

    private ConsultaProcedimientoPasoModel consultaProcedimientoPasoModel;

    private UUID sampleId;
    private ConsultaProcedimientoPaso sampleConsultaProcedimientoPaso;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, ConsultaProcedimientoPasoModel.class, "consultaProcedimientoPasoModel");
        sampleId = UUID.randomUUID();
        sampleConsultaProcedimientoPaso = new ConsultaProcedimientoPaso();
        sampleConsultaProcedimientoPaso.setIdConsultaProcedimientoPaso(sampleId);
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(coppServiceMock, consultaProcedimientoPasoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        ConsultaProcedimientoPaso primero = consultaProcedimientoPasoModel.instanciarRegistro();
        ConsultaProcedimientoPaso segundo = consultaProcedimientoPasoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdConsultaProcedimientoPaso());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(coppServiceMock.buscar(sampleId)).thenReturn(sampleConsultaProcedimientoPaso);

        ConsultaProcedimientoPaso resultado = consultaProcedimientoPasoModel.getRegistroById(sampleId.toString());

        assertSame(sampleConsultaProcedimientoPaso, resultado);
        verify(coppServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(coppServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(consultaProcedimientoPasoModel.getRegistroById("no-es-un-uuid"));
        assertNull(consultaProcedimientoPasoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(coppServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, consultaProcedimientoPasoModel.getIdByRegistro(sampleConsultaProcedimientoPaso));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(consultaProcedimientoPasoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(coppServiceMock.contar()).thenReturn(3);

        assertNotNull(consultaProcedimientoPasoModel.getLazyModel());
        assertEquals(3, consultaProcedimientoPasoModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA buscarConsultaProcedimiento
    // ==========================================

    @Test
    @DisplayName("buscarConsultaProcedimiento - Éxito (Busca con el texto sin espacios sobrantes)")
    void buscarConsultaProcedimiento_Success() {
        List<ConsultaProcedimiento> encontrados = List.of(new ConsultaProcedimiento(UUID.randomUUID()));
        when(conspServiceMock.buscarPorNombreProcedimiento("limpieza", Integer.MAX_VALUE)).thenReturn(encontrados);
        consultaProcedimientoPasoModel.setFiltroConsultaProcedimiento("  limpieza  ");

        consultaProcedimientoPasoModel.buscarConsultaProcedimiento();

        assertSame(encontrados, consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento());
        // Ya había resultados: el getter no vuelve a consultar
        verify(conspServiceMock, times(1)).buscarPorNombreProcedimiento(any(), anyInt());
    }

    @Test
    @DisplayName("buscarConsultaProcedimiento - Fallos (Filtro nulo se trata como texto vacío)")
    void buscarConsultaProcedimiento_Failures() {
        when(conspServiceMock.buscarPorNombreProcedimiento("", Integer.MAX_VALUE)).thenReturn(List.of());
        consultaProcedimientoPasoModel.setFiltroConsultaProcedimiento(null);

        consultaProcedimientoPasoModel.buscarConsultaProcedimiento();

        assertTrue(consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento().isEmpty());
    }

    // ==========================================
    // PRUEBAS PARA etiquetaConsultaProcedimiento
    // ==========================================

    @Test
    @DisplayName("etiquetaConsultaProcedimiento - Éxito (Procedimiento y nombre del paciente)")
    void etiquetaConsultaProcedimiento_Success() {
        assertEquals("Limpieza — Ana Pérez", consultaProcedimientoPasoModel.etiquetaConsultaProcedimiento(crearConsp()));
    }

    @Test
    @DisplayName("etiquetaConsultaProcedimiento - Fallos (Datos incompletos)")
    void etiquetaConsultaProcedimiento_Failures() {
        // Sin registro
        assertEquals("", consultaProcedimientoPasoModel.etiquetaConsultaProcedimiento(null));

        // Sin procedimiento
        assertEquals("", consultaProcedimientoPasoModel.etiquetaConsultaProcedimiento(
                new ConsultaProcedimiento(UUID.randomUUID())));

        // Sin consulta: solo muestra el procedimiento
        ConsultaProcedimiento sinConsulta = crearConsp();
        sinConsulta.setIdConsulta(null);
        assertEquals("Limpieza — ", consultaProcedimientoPasoModel.etiquetaConsultaProcedimiento(sinConsulta));

        // Consulta sin paciente
        ConsultaProcedimiento sinPaciente = crearConsp();
        sinPaciente.getIdConsulta().setIdPersonaRol(null);
        assertEquals("Limpieza — ", consultaProcedimientoPasoModel.etiquetaConsultaProcedimiento(sinPaciente));
    }

    // ==========================================
    // PRUEBAS PARA buscarPersonaRol
    // ==========================================

    @Test
    @DisplayName("buscarPersonaRol - Éxito (Busca con el texto sin espacios sobrantes)")
    void buscarPersonaRol_Success() {
        List<PersonaRol> encontrados = List.of(new PersonaRol(UUID.randomUUID()));
        when(prServiceMock.buscarPorNombresApellidos("ana", Integer.MAX_VALUE)).thenReturn(encontrados);
        consultaProcedimientoPasoModel.setFiltroPersonaRol("  ana ");

        consultaProcedimientoPasoModel.buscarPersonaRol();

        assertSame(encontrados, consultaProcedimientoPasoModel.getResultadosPersonaRol());
        verify(prServiceMock, times(1)).buscarPorNombresApellidos(any(), anyInt());
    }

    @Test
    @DisplayName("buscarPersonaRol - Fallos (Filtro nulo se trata como texto vacío)")
    void buscarPersonaRol_Failures() {
        when(prServiceMock.buscarPorNombresApellidos("", Integer.MAX_VALUE)).thenReturn(List.of());
        consultaProcedimientoPasoModel.setFiltroPersonaRol(null);

        consultaProcedimientoPasoModel.buscarPersonaRol();

        assertTrue(consultaProcedimientoPasoModel.getResultadosPersonaRol().isEmpty());
    }

    // ==========================================
    // PRUEBAS PARA etiquetaPersonaRol
    // ==========================================

    @Test
    @DisplayName("etiquetaPersonaRol - Éxito (Nombre de la persona y su rol entre paréntesis)")
    void etiquetaPersonaRol_Success() {
        assertEquals("Ana Pérez (Doctor)", consultaProcedimientoPasoModel.etiquetaPersonaRol(crearPersonaRol()));
    }

    @Test
    @DisplayName("etiquetaPersonaRol - Fallos (Datos incompletos)")
    void etiquetaPersonaRol_Failures() {
        assertEquals("", consultaProcedimientoPasoModel.etiquetaPersonaRol(null));
        assertEquals("", consultaProcedimientoPasoModel.etiquetaPersonaRol(new PersonaRol(UUID.randomUUID())));

        PersonaRol sinRol = crearPersonaRol();
        sinRol.setIdRol(null);
        assertEquals("Ana Pérez ()", consultaProcedimientoPasoModel.etiquetaPersonaRol(sinRol));
    }

    // ==========================================
    // PRUEBAS PARA getResultadosConsultaProcedimiento y getResultadosPersonaRol
    // ==========================================

    @Test
    @DisplayName("getResultados - Éxito (Carga perezosa: consulta solo la primera vez)")
    void getResultados_Success() {
        List<ConsultaProcedimiento> consps = List.of(new ConsultaProcedimiento(UUID.randomUUID()));
        List<PersonaRol> personas = List.of(new PersonaRol(UUID.randomUUID()));
        when(conspServiceMock.buscarPorNombreProcedimiento("", Integer.MAX_VALUE)).thenReturn(consps);
        when(prServiceMock.buscarPorNombresApellidos("", Integer.MAX_VALUE)).thenReturn(personas);

        assertSame(consps, consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento());
        assertSame(consps, consultaProcedimientoPasoModel.getResultadosConsultaProcedimiento());
        assertSame(personas, consultaProcedimientoPasoModel.getResultadosPersonaRol());
        assertSame(personas, consultaProcedimientoPasoModel.getResultadosPersonaRol());

        verify(conspServiceMock, times(1)).buscarPorNombreProcedimiento(any(), anyInt());
        verify(prServiceMock, times(1)).buscarPorNombresApellidos(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA getters y setters
    // ==========================================

    @Test
    @DisplayName("getters y setters de filtros - Éxito")
    void gettersAndSetters_Success() {
        consultaProcedimientoPasoModel.setFiltroConsultaProcedimiento("a");
        consultaProcedimientoPasoModel.setFiltroPersonaRol("b");

        assertEquals("a", consultaProcedimientoPasoModel.getFiltroConsultaProcedimiento());
        assertEquals("b", consultaProcedimientoPasoModel.getFiltroPersonaRol());
    }

    private ConsultaProcedimiento crearConsp() {
        ConsultaProcedimiento consp = new ConsultaProcedimiento(UUID.randomUUID());
        Procedimiento procedimiento = new Procedimiento(UUID.randomUUID());
        procedimiento.setNombre("Limpieza");
        consp.setIdProcedimiento(procedimiento);
        Consulta consulta = new Consulta(UUID.randomUUID());
        consulta.setIdPersonaRol(crearPersonaRol());
        consp.setIdConsulta(consulta);
        return consp;
    }

    private PersonaRol crearPersonaRol() {
        Persona persona = new Persona(UUID.randomUUID());
        persona.setNombres("Ana");
        persona.setApellidos("Pérez");
        Rol rol = new Rol(UUID.randomUUID());
        rol.setNombre("Doctor");
        PersonaRol personaRol = new PersonaRol(UUID.randomUUID());
        personaRol.setIdPersona(persona);
        personaRol.setIdRol(rol);
        return personaRol;
    }
}
