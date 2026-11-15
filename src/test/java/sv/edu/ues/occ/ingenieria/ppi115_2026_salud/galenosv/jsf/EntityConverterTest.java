package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.ConverterException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.EntityConverter;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityConverterTest {

    @Mock
    private EntityManager emMock;

    @Mock
    private EntityManagerFactory emfMock;

    @Mock
    private PersistenceUnitUtil unitUtilMock;

    @Mock
    private FacesContext facesContextMock;

    @Mock
    private UIComponent componenteMock;

    @Mock
    private ValueExpression valueExpressionMock;

    @Mock
    private ELContext elContextMock;

    @InjectMocks
    private EntityConverter converter;

    private UUID sampleId;
    private Clinica sampleClinica;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleClinica = new Clinica();
        sampleClinica.setIdClinica(sampleId);
        sampleClinica.setNombre("Clínica Central");
    }

    // ==========================================
    // PRUEBAS PARA getAsObject
    // ==========================================

    @Test
    @DisplayName("getAsObject - Éxito (Busca la entidad del tipo enlazado por su UUID)")
    void getAsObject_Success() {
        when(componenteMock.getValueExpression("value")).thenReturn(valueExpressionMock);
        when(facesContextMock.getELContext()).thenReturn(elContextMock);
        doReturn(Clinica.class).when(valueExpressionMock).getType(elContextMock);
        when(emMock.find(Clinica.class, sampleId)).thenReturn(sampleClinica);

        Object resultado = converter.getAsObject(facesContextMock, componenteMock, sampleId.toString());

        assertSame(sampleClinica, resultado);
        verify(emMock, times(1)).find(Clinica.class, sampleId);
    }

    @Test
    @DisplayName("getAsObject - Fallos (Sin selección retorna null; sin value enlazado o UUID inválido lanzan ConverterException)")
    void getAsObject_Failures() {
        // Sin selección: no consulta nada
        assertNull(converter.getAsObject(facesContextMock, componenteMock, null));
        assertNull(converter.getAsObject(facesContextMock, componenteMock, "   "));
        verifyNoInteractions(emMock);

        // Componente sin value enlazado
        assertThrows(ConverterException.class,
                () -> converter.getAsObject(facesContextMock, componenteMock, sampleId.toString()));

        // Texto que no es un UUID
        when(componenteMock.getValueExpression("value")).thenReturn(valueExpressionMock);
        assertThrows(ConverterException.class,
                () -> converter.getAsObject(facesContextMock, componenteMock, "no-es-un-uuid"));

        verifyNoInteractions(emMock);
    }

    // ==========================================
    // PRUEBAS PARA getAsString
    // ==========================================

    @Test
    @DisplayName("getAsString - Éxito (Retorna el id de la entidad en texto)")
    void getAsString_Success() {
        when(emMock.getEntityManagerFactory()).thenReturn(emfMock);
        when(emfMock.getPersistenceUnitUtil()).thenReturn(unitUtilMock);
        when(unitUtilMock.getIdentifier(sampleClinica)).thenReturn(sampleId);

        assertEquals(sampleId.toString(), converter.getAsString(facesContextMock, componenteMock, sampleClinica));
    }

    @Test
    @DisplayName("getAsString - Fallos (Entidad nula o sin id retorna cadena vacía)")
    void getAsString_Failures() {
        assertEquals("", converter.getAsString(facesContextMock, componenteMock, null));
        verifyNoInteractions(emMock);

        Clinica sinId = new Clinica();
        when(emMock.getEntityManagerFactory()).thenReturn(emfMock);
        when(emfMock.getPersistenceUnitUtil()).thenReturn(unitUtilMock);
        when(unitUtilMock.getIdentifier(sinId)).thenReturn(null);

        assertEquals("", converter.getAsString(facesContextMock, componenteMock, sinId));
    }
}
