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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.DocumentoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.ESTADO_CRUD;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.MedioContactoModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.PersonaModel;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.PersonaRolModel;

@ExtendWith(MockitoExtension.class)
class PersonaModelTest {

    @Mock
    private PersonaService persServiceMock;

    @Mock
    private DocumentoModel documentoModelMock;

    @Mock
    private MedioContactoModel medioContactoModelMock;

    @Mock
    private PersonaRolModel personaRolModelMock;

    @Mock
    private SelectEvent<Persona> selectEventMock;

    private PersonaModel personaModel;

    private UUID sampleId;
    private Persona samplePersona;
    private FacesTestSupport faces;
    private MockedStatic<PrimeFaces> primeFacesEstatico;
    private PrimeFaces.Ajax ajaxMock;

    @BeforeEach
    void setUp() {
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.ModelTestSupport.crearModelo(this, PersonaModel.class, "personaModel");
        faces = FacesTestSupport.abrir();
        PrimeFaces primeFacesMock = mock(PrimeFaces.class);
        ajaxMock = mock(PrimeFaces.Ajax.class);
        lenient().when(primeFacesMock.ajax()).thenReturn(ajaxMock);
        primeFacesEstatico = mockStatic(PrimeFaces.class, withSettings().lenient());
        primeFacesEstatico.when(PrimeFaces::current).thenReturn(primeFacesMock);
        sampleId = UUID.randomUUID();
        samplePersona = new Persona();
        samplePersona.setIdPersona(sampleId);
    }

    @AfterEach
    void tearDown() {
        primeFacesEstatico.close();
        faces.close();
    }

    // ==========================================
    // PRUEBAS PARA getDAO
    // ==========================================

    @Test
    @DisplayName("getDAO - Éxito (Retorna el servicio inyectado)")
    void getDAO_Success() {
        assertSame(persServiceMock, personaModel.getDAO());
    }

    // ==========================================
    // PRUEBAS PARA instanciarRegistro
    // ==========================================

    @Test
    @DisplayName("instanciarRegistro - Éxito (Retorna una instancia nueva y vacía cada vez)")
    void instanciarRegistro_Success() {
        Persona primero = personaModel.instanciarRegistro();
        Persona segundo = personaModel.instanciarRegistro();

        assertNotNull(primero);
        assertNull(primero.getIdPersona());
        assertNotSame(primero, segundo);
    }

    // ==========================================
    // PRUEBAS PARA getRegistroById
    // ==========================================

    @Test
    @DisplayName("getRegistroById - Éxito (Retorna el registro que encuentra el servicio)")
    void getRegistroById_Success() {
        when(persServiceMock.buscar(sampleId)).thenReturn(samplePersona);

        Persona resultado = personaModel.getRegistroById(sampleId.toString());

        assertSame(samplePersona, resultado);
        verify(persServiceMock, times(1)).buscar(sampleId);
    }

    @Test
    @DisplayName("getRegistroById - Fallos (ID con formato inválido o registro inexistente retornan null)")
    void getRegistroById_Failures() {
        UUID idInexistente = UUID.randomUUID();
        when(persServiceMock.buscar(idInexistente)).thenReturn(null);

        assertNull(personaModel.getRegistroById("no-es-un-uuid"));
        assertNull(personaModel.getRegistroById(idInexistente.toString()));

        // Solo el ID válido llegó al servicio
        verify(persServiceMock, times(1)).buscar(any());
    }

    // ==========================================
    // PRUEBAS PARA getIdByRegistro
    // ==========================================

    @Test
    @DisplayName("getIdByRegistro - Éxito (Retorna el id del registro)")
    void getIdByRegistro_Success() {
        assertEquals(sampleId, personaModel.getIdByRegistro(samplePersona));
    }

    @Test
    @DisplayName("getIdByRegistro - Fallos (Registro nulo retorna null)")
    void getIdByRegistro_Failures() {
        assertNull(personaModel.getIdByRegistro(null));
    }

    // ==========================================
    // PRUEBAS PARA getLazyModel
    // ==========================================

    @Test
    @DisplayName("getLazyModel - Éxito (Modelo perezoso conectado a este Model)")
    void getLazyModel_Success() {
        when(persServiceMock.contar()).thenReturn(3);

        assertNotNull(personaModel.getLazyModel());
        assertEquals(3, personaModel.getLazyModel().count(Collections.emptyMap()));
    }

    // ==========================================
    // PRUEBAS PARA seleccionar
    // ==========================================

    @Test
    @DisplayName("seleccionar - Éxito (Filtra documentos, medios de contacto y roles por la persona elegida)")
    void seleccionar_Success() {
        when(selectEventMock.getObject()).thenReturn(samplePersona);

        personaModel.seleccionar(selectEventMock);

        assertEquals(ESTADO_CRUD.MODIFICAR, personaModel.getEstado());
        assertSame(samplePersona, personaModel.getRegistroActual());
        verify(documentoModelMock, times(1)).filtrarPorPersona(samplePersona);
        verify(medioContactoModelMock, times(1)).filtrarPorPersona(samplePersona);
        verify(personaRolModelMock, times(1)).filtrarPorPersona(samplePersona);
        verify(ajaxMock, times(1)).update(":layout:tabsPersona");
    }

    // ==========================================
    // PRUEBAS PARA nuevo
    // ==========================================

    @Test
    @DisplayName("nuevo - Éxito (Quita el filtro por persona de las pestañas y pasa a CREAR)")
    void nuevo_Success() {
        personaModel.nuevo();

        assertEquals(ESTADO_CRUD.CREAR, personaModel.getEstado());
        verify(documentoModelMock, times(1)).filtrarPorPersona(null);
        verify(medioContactoModelMock, times(1)).filtrarPorPersona(null);
        verify(personaRolModelMock, times(1)).filtrarPorPersona(null);
        verify(ajaxMock, times(1)).update(":layout:tabsPersona");
    }
}
