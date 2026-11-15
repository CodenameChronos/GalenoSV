package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf.test.ModelTestSupport.crearModelo(this, PersonaModel.class, "personaModel");
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
