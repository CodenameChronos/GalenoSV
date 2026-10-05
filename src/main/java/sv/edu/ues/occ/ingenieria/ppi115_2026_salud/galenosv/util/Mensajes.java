package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.util.ResourceBundle;

public final class Mensajes {

    private Mensajes() {
    }

    public static void exito(String clave) {
        agregar(FacesMessage.SEVERITY_INFO, clave);
    }

    public static void info(String clave) {
        agregar(FacesMessage.SEVERITY_INFO, clave);
    }

    public static void advertencia(String clave) {
        agregar(FacesMessage.SEVERITY_WARN, clave);
    }

    public static void error(String clave) {
        agregar(FacesMessage.SEVERITY_ERROR, clave);
    }

    private static void agregar(
            FacesMessage.Severity severidad,
            String clave) {

        FacesContext context = FacesContext.getCurrentInstance();

        ResourceBundle mensajes =
                context.getApplication().getResourceBundle(context, "msg");

        String texto = mensajes.getString(clave);

        context.addMessage(
                null,
                new FacesMessage(severidad, texto, null)
        );
    }
}