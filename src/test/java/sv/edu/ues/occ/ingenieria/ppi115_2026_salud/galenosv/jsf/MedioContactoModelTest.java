package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.validator.ValidatorException;
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
    // PRUEBAS PARA completarPersona
    // ==========================================

    // ==========================================
    // PRUEBAS PARA completarTipoMedioContacto
    // ==========================================

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

}
