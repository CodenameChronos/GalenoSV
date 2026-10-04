package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import java.util.Locale;

/**
 * Aplica el idioma guardado en sesión (LocaleBean) al UIViewRoot de cada
 * vista, antes de que se renderice.
 *
 * Es necesario porque un cambio de página mediante un enlace normal
 * (p:menu con atributo url, como en g:layout) genera una petición GET nueva
 * y, por lo tanto, un UIViewRoot nuevo, construido con el idioma por
 * defecto. El listener del p:ajax del selector de idioma solo actualiza la
 * vista donde se disparó el cambio, no las vistas que se cargan después
 * mediante navegación normal. Este PhaseListener corre en cada ciclo de
 * vida (postback o no) y corrige el Locale antes de que se evalúen las
 * expresiones #{msg[...]} de la página, sin importar cómo se llegó a ella.
 */
public class LocaleListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext ctx = event.getFacesContext();
        if (ctx.getViewRoot() == null) {
            return;
        }
        LocaleBean localeBean = ctx.getApplication()
                .evaluateExpressionGet(ctx, "#{localeBean}", LocaleBean.class);
        if (localeBean != null && localeBean.getIdioma() != null) {
            ctx.getViewRoot().setLocale(new Locale(localeBean.getIdioma()));
        }
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        // No se requiere acción posterior a la fase.
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

}