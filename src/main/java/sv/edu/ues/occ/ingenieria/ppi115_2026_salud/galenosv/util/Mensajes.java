package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.util;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
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

    public static void errorConDetalle(
            String clave,
            String claveDetalle,
            Object... parametros) {

        agregar(
                FacesMessage.SEVERITY_ERROR,
                clave,
                claveDetalle,
                parametros
        );
    }

    private static void agregar(
            FacesMessage.Severity severidad,
            String clave) {

        agregar(severidad, clave, null);
    }

    private static void agregar(
            FacesMessage.Severity severidad,
            String clave,
            String claveDetalle,
            Object... parametros) {

        FacesContext context = FacesContext.getCurrentInstance();

        ResourceBundle mensajes
                = context.getApplication().getResourceBundle(context, "msg");

        Locale locale = context.getViewRoot() != null
                ? context.getViewRoot().getLocale()
                : Locale.getDefault();

        String resumen = obtenerTexto(
                mensajes, clave, locale, parametros
        );

        String detalle = claveDetalle == null
                ? null
                : obtenerTexto(mensajes, claveDetalle, locale, parametros);

        context.addMessage(
                null,
                new FacesMessage(severidad, resumen, detalle)
        );
    }

    private static String obtenerTexto(
            ResourceBundle mensajes,
            String clave,
            Locale locale,
            Object... parametros) {

        String plantilla = mensajes.getString(clave);

        if (parametros == null || parametros.length == 0) {
            return plantilla;
        }

        return new MessageFormat(plantilla, locale).format(parametros);
    }

    public static void errorTexto(String texto) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, texto, null)
        );
    }
}
