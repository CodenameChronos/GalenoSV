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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.MedioContactoModel;

@ExtendWith(MockitoExtension.class)
class MedioContactoModelTest {

    @Mock
    private MedioContactoService mcServiceMock;

    @Mock
    private PersonaService persServiceMock;

    @Mock
    private TipoMedioContactoService tmcServiceMock;

    private MedioContactoModel medioContactoModel;

    private UUID sampleId;
    private MedioContacto sampleMedioContacto;
    private FacesTestSupport faces;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, MedioContactoModel.class, "medioContactoModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        sampleMedioContacto = new MedioContacto();
        sampleMedioContacto.setIdMedioContacto(sampleId);
    }

    @AfterEach
    void tearDown() {
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(mcServiceMock, medioContactoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        MedioContacto primero = medioContactoModel.instanciarRegistro();
        MedioContacto segundo = medioContactoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdMedioContacto());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(mcServiceMock.buscar(sampleId)).thenReturn(sampleMedioContacto);

        MedioContacto resultado = medioContactoModel.getRegistroById(sampleId.toString());

        assertSame(sampleMedioContacto, resultado);
        verify(mcServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(mcServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(medioContactoModel.getRegistroById("no-es-un-uuid"));
        assertNull(medioContactoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(mcServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, medioContactoModel.getIdByRegistro(sampleMedioContacto));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(medioContactoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(mcServiceMock.contar()).thenReturn(3);

        assertNotNull(medioContactoModel.getLazyModel());
        assertEquals(3, medioContactoModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA completarPersona
    // ==========================================

    @Test
    @DisplayName("completarPersona - Éxito (Retorna los registros que encuentra el servicio)")
    void completarPersona_Success() {
        List<Persona> encontrados = List.of(new Persona(UUID.randomUUID()));
        when(persServiceMock.buscarPorNombre("ana", 30)).thenReturn(encontrados);

        assertSame(encontrados, medioContactoModel.completarPersona("ana"));
    }

    @Test
    @DisplayName("completarPersona - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarPersona_Failures() {
        assertTrue(medioContactoModel.completarPersona(null).isEmpty());
        assertTrue(medioContactoModel.completarPersona("").isEmpty());
        assertTrue(medioContactoModel.completarPersona("   ").isEmpty());

        verify(persServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarTipoMedioContacto
    // ==========================================

    @Test
    @DisplayName("completarTipoMedioContacto - Éxito (Retorna los registros que encuentra el servicio)")
    void completarTipoMedioContacto_Success() {
        List<TipoMedioContacto> encontrados = List.of(new TipoMedioContacto(UUID.randomUUID()));
        when(tmcServiceMock.buscarPorNombre("correo", 30)).thenReturn(encontrados);

        assertSame(encontrados, medioContactoModel.completarTipoMedioContacto("correo"));
    }

    @Test
    @DisplayName("completarTipoMedioContacto - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarTipoMedioContacto_Failures() {
        assertTrue(medioContactoModel.completarTipoMedioContacto(null).isEmpty());
        assertTrue(medioContactoModel.completarTipoMedioContacto("").isEmpty());
        assertTrue(medioContactoModel.completarTipoMedioContacto("   ").isEmpty());

        verify(tmcServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA filtrarPorPersona
    // ==========================================

    @Test
    @DisplayName("filtrarPorPersona - Éxito (Aplica el filtro por persona a la búsqueda y al conteo)")
    void filtrarPorPersona_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        List<MedioContacto> filtrados = List.of(sampleMedioContacto);
        when(mcServiceMock.findRangeByCriterio("idPersona.idPersona", persona.getIdPersona(), 0, 10)).thenReturn(filtrados);
        when(mcServiceMock.contarByCriterio("idPersona.idPersona", persona.getIdPersona())).thenReturn(1);

        medioContactoModel.filtrarPorPersona(persona);

        assertSame(persona, medioContactoModel.getPersonaFiltro());
        assertSame(filtrados, medioContactoModel.buscarRegistros(0, 10));
        assertEquals(1, medioContactoModel.contar());
    }

    @Test
    @DisplayName("filtrarPorPersona - Fallos (Persona nula limpia el filtro y vuelve a listar todo)")
    void filtrarPorPersona_Failures() {
        List<MedioContacto> todos = List.of(sampleMedioContacto);
        when(mcServiceMock.findRange(0, 10)).thenReturn(todos);
        medioContactoModel.filtrarPorPersona(new Persona(UUID.randomUUID()));

        medioContactoModel.filtrarPorPersona(null);

        assertNull(medioContactoModel.getPersonaFiltro());
        assertSame(todos, medioContactoModel.buscarRegistros(0, 10));
        verify(mcServiceMock, never()).findRangeByCriterio(any(), any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Asigna la persona filtrada al nuevo registro solo si hay filtro)")
    void nuevo_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        medioContactoModel.filtrarPorPersona(persona);

        medioContactoModel.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, medioContactoModel.getEstado());
        assertSame(persona, medioContactoModel.getRegistroActual().getIdPersona());

        medioContactoModel.filtrarPorPersona(null);
        medioContactoModel.nuevo();

        assertNull(medioContactoModel.getRegistroActual().getIdPersona());
    }

    // ==========================================
    // PRUEBAS PARA validarValor
    // ==========================================

    @Test
    @DisplayName("validarValor - Éxito (Valores válidos o sin regla que validar no lanzan excepción)")
    void validarValor_Success() {
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setExpresionRegular("\\d{8}-\\d");
        sampleMedioContacto.setIdTipoMedioContacto(tipo);
        medioContactoModel.setRegistroActual(sampleMedioContacto);

        // Valor que cumple el patrón (se ignoran los espacios alrededor)
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, "01234567-8"));
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, " 01234567-8 "));

        // Valor vacío: de eso se encarga @NotBlank
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, null));
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, "   "));

        // Tipo sin patrón: no hay nada que validar
        tipo.setExpresionRegular(null);
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, "cualquier cosa"));
        tipo.setExpresionRegular("  ");
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, "cualquier cosa"));

        // Sin tipo seleccionado
        sampleMedioContacto.setIdTipoMedioContacto(null);
        assertDoesNotThrow(() -> medioContactoModel.validarValor(null, null, "cualquier cosa"));
    }

    @Test
    @DisplayName("validarValor - Fallos (Valor que no cumple el patrón o patrón inválido lanza ValidatorException)")
    void validarValor_Failures() {
        TipoMedioContacto tipo = new TipoMedioContacto(UUID.randomUUID());
        tipo.setExpresionRegular("\\d{8}-\\d");
        sampleMedioContacto.setIdTipoMedioContacto(tipo);
        medioContactoModel.setRegistroActual(sampleMedioContacto);

        // El valor no cumple el formato
        ValidatorException noCumple = assertThrows(ValidatorException.class,
                () -> medioContactoModel.validarValor(null, null, "abc"));
        assertEquals(FacesMessage.SEVERITY_ERROR, noCumple.getFacesMessage().getSeverity());

        // La expresión regular del tipo está mal escrita
        tipo.setExpresionRegular("[abc");
        ValidatorException patronInvalido = assertThrows(ValidatorException.class,
                () -> medioContactoModel.validarValor(null, null, "abc"));
        assertEquals(FacesMessage.SEVERITY_ERROR, patronInvalido.getFacesMessage().getSeverity());
        assertNotEquals(noCumple.getFacesMessage().getSummary(), patronInvalido.getFacesMessage().getSummary());
    }

    // ==========================================
    // PRUEBAS PARA alCambiarTipoMedioContacto
    // ==========================================

    @Test
    @DisplayName("alCambiarTipoMedioContacto - Éxito (Limpia el valor al cambiar el tipo)")
    void alCambiarTipoMedioContacto_Success() {
        sampleMedioContacto.setValor("01234567-8");
        medioContactoModel.setRegistroActual(sampleMedioContacto);

        medioContactoModel.alCambiarTipoMedioContacto();

        assertNull(sampleMedioContacto.getValor());
    }
}
