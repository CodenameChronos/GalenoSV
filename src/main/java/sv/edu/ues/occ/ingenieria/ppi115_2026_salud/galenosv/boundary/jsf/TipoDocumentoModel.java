package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Named
@ViewScoped
public class TipoDocumentoModel extends ModelHandler<TipoDocumento> {

    private static final long serialVersionUID = 1L;

    @Inject
    private TipoDocumentoService tdService;

    private GenericLazyDataModel<TipoDocumento> lazyModel;

    public TipoDocumentoModel() {
        super(TipoDocumento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<TipoDocumento> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ParentServiceInterface<TipoDocumento> getDAO() {
        return tdService;
    }

    @Override
    public TipoDocumento instanciarRegistro() {
        return new TipoDocumento();
    }

    @Override
    public TipoDocumento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (TipoDocumento) tdService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "ID inválido recibido para TipoDocumento: " + id,
                    ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(TipoDocumento registro) {
        return registro != null ? registro.getIdTipoDocumento() : null;
    }

    public void validarExpresionRegular(
            FacesContext context,
            UIComponent component,
            Object value) {

        if (value == null || value.toString().isBlank()) {
            return;
        }

        try {
            Pattern.compile(value.toString().trim());

        } catch (PatternSyntaxException e) {
            throw new ValidatorException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "La expresión regular proporcionada no es un patrón sintácticamente válido",
                            null
                    )
            );
        }
    }
}
