package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.context.FacesContext;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.event.PhaseEvent;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.LocaleBean;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf.LocaleListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocaleListenerTest {

    @Mock
    private PhaseEvent eventoMock;

    @Mock
    private FacesContext facesContextMock;

    @Mock
    private UIViewRoot viewRootMock;

    @Mock
    private Application applicationMock;

    private LocaleListener listener;

    @BeforeEach
    void setUp() {
        listener = new LocaleListener();
        when(eventoMock.getFacesContext()).thenReturn(facesContextMock);
    }

    // ==========================================
    // PRUEBAS PARA beforePhase
    // ==========================================

    @Test
    @DisplayName("beforePhase - Éxito (Aplica al UIViewRoot el idioma guardado en LocaleBean)")
    void beforePhase_Success() {
        LocaleBean localeBean = new LocaleBean();
        localeBean.setIdioma("en");
        when(facesContextMock.getViewRoot()).thenReturn(viewRootMock);
        when(facesContextMock.getApplication()).thenReturn(applicationMock);
        when(applicationMock.evaluateExpressionGet(facesContextMock, "#{localeBean}", LocaleBean.class))
                .thenReturn(localeBean);

        listener.beforePhase(eventoMock);

        verify(viewRootMock, times(1)).setLocale(Locale.ENGLISH);
    }

    @Test
    @DisplayName("beforePhase - Fallos (Sin vista, sin LocaleBean o sin idioma no modifica el Locale)")
    void beforePhase_Failures() {
        // Sin UIViewRoot: ni siquiera consulta el LocaleBean
        when(facesContextMock.getViewRoot()).thenReturn(null);
        listener.beforePhase(eventoMock);
        verify(facesContextMock, never()).getApplication();

        // Con vista, pero sin LocaleBean disponible o con idioma nulo
        LocaleBean sinIdioma = new LocaleBean();
        sinIdioma.setIdioma(null);
        when(facesContextMock.getViewRoot()).thenReturn(viewRootMock);
        when(facesContextMock.getApplication()).thenReturn(applicationMock);
        when(applicationMock.evaluateExpressionGet(facesContextMock, "#{localeBean}", LocaleBean.class))
                .thenReturn(null, sinIdioma);

        listener.beforePhase(eventoMock);
        listener.beforePhase(eventoMock);

        verify(viewRootMock, never()).setLocale(any());
    }
}
