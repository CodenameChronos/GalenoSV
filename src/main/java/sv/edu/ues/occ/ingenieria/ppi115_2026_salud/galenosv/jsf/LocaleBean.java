package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Guarda el idioma elegido por el usuario para la sesión completa.
 *
 * Es @SessionScoped para que el idioma se mantenga mientras el usuario
 * navega entre pantallas. La propiedad idioma se enlaza directamente al
 * selector en g:layout; aplicar ese valor al Locale de cada vista es
 * responsabilidad de LocaleAplicadoPhaseListener, no de este bean, para que
 * funcione igual con una navegación normal (enlaces de p:menu) que con un
 * postback AJAX.
 */
@Named
@SessionScoped
public class LocaleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Código de idioma por defecto */
    private String idioma = "es";

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

}