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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.DocumentoModel;

@ExtendWith(MockitoExtension.class)
class DocumentoModelTest {

    @Mock
    private DocumentoService docServiceMock;

    @Mock
    private PersonaService persServiceMock;

    @Mock
    private TipoDocumentoService tdServiceMock;

    private DocumentoModel documentoModel;

    private UUID sampleId;
    private Documento sampleDocumento;
    private FacesTestSupport faces;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, DocumentoModel.class, "documentoModel");
        faces = FacesTestSupport.abrir();
        sampleId = UUID.randomUUID();
        sampleDocumento = new Documento();
        sampleDocumento.setIdDocumento(sampleId);
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
        assertSame(docServiceMock, documentoModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Documento primero = documentoModel.instanciarRegistro();
        Documento segundo = documentoModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdDocumento());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(docServiceMock.buscar(sampleId)).thenReturn(sampleDocumento);

        Documento resultado = documentoModel.getRegistroById(sampleId.toString());

        assertSame(sampleDocumento, resultado);
        verify(docServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(docServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(documentoModel.getRegistroById("no-es-un-uuid"));
        assertNull(documentoModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(docServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, documentoModel.getIdByRegistro(sampleDocumento));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(documentoModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(docServiceMock.contar()).thenReturn(3);

        assertNotNull(documentoModel.getLazyModel());
        assertEquals(3, documentoModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA completarPersona
    // ==========================================

    @Test
    @DisplayName("completarPersona - Éxito (Retorna los registros que encuentra el servicio)")
    void completarPersona_Success() {
        List<Persona> encontrados = List.of(new Persona(UUID.randomUUID()));
        when(persServiceMock.buscarPorNombre("ana", 30)).thenReturn(encontrados);

        assertSame(encontrados, documentoModel.completarPersona("ana"));
    }

    @Test
    @DisplayName("completarPersona - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarPersona_Failures() {
        assertTrue(documentoModel.completarPersona(null).isEmpty());
        assertTrue(documentoModel.completarPersona("").isEmpty());
        assertTrue(documentoModel.completarPersona("   ").isEmpty());

        verify(persServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA completarTipoDocumento
    // ==========================================

    @Test
    @DisplayName("completarTipoDocumento - Éxito (Retorna los registros que encuentra el servicio)")
    void completarTipoDocumento_Success() {
        List<TipoDocumento> encontrados = List.of(new TipoDocumento(UUID.randomUUID()));
        when(tdServiceMock.buscarPorNombre("dui", 30)).thenReturn(encontrados);

        assertSame(encontrados, documentoModel.completarTipoDocumento("dui"));
    }

    @Test
    @DisplayName("completarTipoDocumento - Fallos (Texto nulo o en blanco retorna lista vacía sin consultar)")
    void completarTipoDocumento_Failures() {
        assertTrue(documentoModel.completarTipoDocumento(null).isEmpty());
        assertTrue(documentoModel.completarTipoDocumento("").isEmpty());
        assertTrue(documentoModel.completarTipoDocumento("   ").isEmpty());

        verify(tdServiceMock, never()).buscarPorNombre(any(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA filtrarPorPersona
    // ==========================================

    @Test
    @DisplayName("filtrarPorPersona - Éxito (Aplica el filtro por persona a la búsqueda y al conteo)")
    void filtrarPorPersona_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        List<Documento> filtrados = List.of(sampleDocumento);
        when(docServiceMock.findRangeByCriterio("idPersona.idPersona", persona.getIdPersona(), 0, 10)).thenReturn(filtrados);
        when(docServiceMock.contarByCriterio("idPersona.idPersona", persona.getIdPersona())).thenReturn(1);

        documentoModel.filtrarPorPersona(persona);

        assertSame(persona, documentoModel.getPersonaFiltro());
        assertSame(filtrados, documentoModel.buscarRegistros(0, 10));
        assertEquals(1, documentoModel.contar());
    }

    @Test
    @DisplayName("filtrarPorPersona - Fallos (Persona nula limpia el filtro y vuelve a listar todo)")
    void filtrarPorPersona_Failures() {
        List<Documento> todos = List.of(sampleDocumento);
        when(docServiceMock.findRange(0, 10)).thenReturn(todos);
        documentoModel.filtrarPorPersona(new Persona(UUID.randomUUID()));

        documentoModel.filtrarPorPersona(null);

        assertNull(documentoModel.getPersonaFiltro());
        assertSame(todos, documentoModel.buscarRegistros(0, 10));
        verify(docServiceMock, never()).findRangeByCriterio(any(), any(), anyInt(), anyInt());
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Asigna la persona filtrada al nuevo registro solo si hay filtro)")
    void nuevo_Success() {
        Persona persona = new Persona(UUID.randomUUID());
        documentoModel.filtrarPorPersona(persona);

        documentoModel.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, documentoModel.getEstado());
        assertSame(persona, documentoModel.getRegistroActual().getIdPersona());

        documentoModel.filtrarPorPersona(null);
        documentoModel.nuevo();

        assertNull(documentoModel.getRegistroActual().getIdPersona());
    }

    // ==========================================
    // PRUEBAS PARA validarValor
    // ==========================================

    @Test
    @DisplayName("validarValor - Éxito (Valores válidos o sin regla que validar no lanzan excepción)")
    void validarValor_Success() {
        TipoDocumento tipo = new TipoDocumento(UUID.randomUUID());
        tipo.setExpresionRegular("\\d{8}-\\d");
        sampleDocumento.setIdTipoDocumento(tipo);
        documentoModel.setRegistroActual(sampleDocumento);

        // Valor que cumple el patrón (se ignoran los espacios alrededor)
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, "01234567-8"));
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, " 01234567-8 "));

        // Valor vacío: de eso se encarga @NotBlank
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, null));
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, "   "));

        // Tipo sin patrón: no hay nada que validar
        tipo.setExpresionRegular(null);
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, "cualquier cosa"));
        tipo.setExpresionRegular("  ");
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, "cualquier cosa"));

        // Sin tipo seleccionado
        sampleDocumento.setIdTipoDocumento(null);
        assertDoesNotThrow(() -> documentoModel.validarValor(null, null, "cualquier cosa"));
    }

    @Test
    @DisplayName("validarValor - Fallos (Valor que no cumple el patrón o patrón inválido lanza ValidatorException)")
    void validarValor_Failures() {
        TipoDocumento tipo = new TipoDocumento(UUID.randomUUID());
        tipo.setExpresionRegular("\\d{8}-\\d");
        sampleDocumento.setIdTipoDocumento(tipo);
        documentoModel.setRegistroActual(sampleDocumento);

        // El valor no cumple el formato
        ValidatorException noCumple = assertThrows(ValidatorException.class,
                () -> documentoModel.validarValor(null, null, "abc"));
        assertEquals(FacesMessage.SEVERITY_ERROR, noCumple.getFacesMessage().getSeverity());

        // La expresión regular del tipo está mal escrita
        tipo.setExpresionRegular("[abc");
        ValidatorException patronInvalido = assertThrows(ValidatorException.class,
                () -> documentoModel.validarValor(null, null, "abc"));
        assertEquals(FacesMessage.SEVERITY_ERROR, patronInvalido.getFacesMessage().getSeverity());
        assertNotEquals(noCumple.getFacesMessage().getSummary(), patronInvalido.getFacesMessage().getSummary());
    }

    // ==========================================
    // PRUEBAS PARA alCambiarTipoDocumento
    // ==========================================

    @Test
    @DisplayName("alCambiarTipoDocumento - Éxito (Limpia el valor al cambiar el tipo)")
    void alCambiarTipoDocumento_Success() {
        sampleDocumento.setValor("01234567-8");
        documentoModel.setRegistroActual(sampleDocumento);

        documentoModel.alCambiarTipoDocumento();

        assertNull(sampleDocumento.getValor());
    }
}
