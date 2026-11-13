package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


public final class FacesTestSupport implements AutoCloseable {

    private final MockedStatic<FacesContext> facesContextEstatico;
    private final FacesContext contexto;
    private final UIViewRoot raiz;
    private final List<FacesMessage> mensajes = new ArrayList<>();
    private final List<UIComponent> hijosDeLaRaiz = new ArrayList<>();

    private FacesTestSupport() {
        contexto = Mockito.mock(FacesContext.class, Mockito.withSettings().lenient());
        raiz = Mockito.mock(UIViewRoot.class, Mockito.withSettings().lenient());
        Application aplicacion = Mockito.mock(Application.class, Mockito.withSettings().lenient());

        ResourceBundle bundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return texto(key);
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }
        };

        Mockito.when(raiz.getFacetsAndChildren()).thenAnswer(inv -> hijosDeLaRaiz.iterator());
        Mockito.when(contexto.getViewRoot()).thenReturn(raiz);
        Mockito.when(contexto.getApplication()).thenReturn(aplicacion);
        Mockito.when(aplicacion.getResourceBundle(contexto, "msg")).thenReturn(bundle);
        Mockito.doAnswer(inv -> {
            mensajes.add(inv.getArgument(1));
            return null;
        }).when(contexto).addMessage(Mockito.any(), Mockito.any(FacesMessage.class));

        facesContextEstatico = Mockito.mockStatic(FacesContext.class, Mockito.withSettings().lenient());
        facesContextEstatico.when(FacesContext::getCurrentInstance).thenReturn(contexto);
    }

    /**
     * Activa el FacesContext simulado para el hilo actual. Debe cerrarse al
     * terminar la prueba (close()).
     */
    public static FacesTestSupport abrir() {
        return new FacesTestSupport();
    }

    /**
     * Texto que devuelve el bundle simulado para una clave.
     */
    public static String texto(String clave) {
        return "txt:" + clave;
    }

    public FacesContext contexto() {
        return contexto;
    }

    /**
     * Agrega un componente como hijo directo de la raíz de la vista simulada
     * (para probar el reinicio de inputs).
     */
    public void agregarHijoALaRaiz(UIComponent componente) {
        hijosDeLaRaiz.add(componente);
    }

    public List<FacesMessage> mensajes() {
        return mensajes;
    }

    public FacesMessage ultimoMensaje() {
        return mensajes.isEmpty() ? null : mensajes.get(mensajes.size() - 1);
    }

    @Override
    public void close() {
        facesContextEstatico.close();
    }
}
