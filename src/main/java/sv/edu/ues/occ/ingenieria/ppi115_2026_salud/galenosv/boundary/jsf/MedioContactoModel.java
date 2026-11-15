package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class MedioContactoModel extends ModelHandler<MedioContacto> {

    private static final long serialVersionUID = 1L;

    @Inject
    private MedioContactoService mcService;

    @Inject
    private PersonaService persService;

    @Inject
    private TipoMedioContactoService tmcService;

    private GenericLazyDataModel<MedioContacto> lazyModel;

    public MedioContactoModel() {
        super(MedioContacto.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<MedioContacto> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ParentServiceInterface<MedioContacto> getDAO() {
        return mcService;
    }

    @Override
    public MedioContacto instanciarRegistro() {
        return new MedioContacto();
    }

    @Override
    public MedioContacto getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (MedioContacto) mcService.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    "ID inválido recibido para MedioContacto: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(MedioContacto registro) {
        return registro != null ? registro.getIdMedioContacto() : null;
    }

    public List<Persona> completarPersona(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return persService.buscarPorNombre(texto, 30);
    }

    public List<TipoMedioContacto> completarTipoMedioContacto(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return tmcService.buscarPorNombre(texto, 30);
    }

    private Persona personaFiltro;

    public void filtrarPorPersona(Persona persona) {
        this.personaFiltro = persona;
        if (persona != null) {
            setFiltro("idPersona.idPersona", persona.getIdPersona());
        } else {
            limpiarFiltro();
        }
    }

    public Persona getPersonaFiltro() {
        return personaFiltro;
    }

    @Override
    public void nuevo() {
        super.nuevo();
        if (personaFiltro != null) {
            registroActual.setIdPersona(personaFiltro);
        }
    }

    public void alCambiarTipoMedioContacto() {
        this.registroActual.setValor(null);
    }

    public void validarValor(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.toString().isBlank()) {
            return; // @NotBlank se encarga de los valores vacíos
        }

        TipoMedioContacto tipo = registroActual.getIdTipoMedioContacto();

        if (tipo == null) {
            return;
        }

        String patron = tipo.getExpresionRegular();

        if (patron == null || patron.isBlank()) {
            return;
        }

        try {
            boolean valido = Pattern.matches(patron, value.toString().trim());

            if (!valido) {
                throw new ValidatorException(
                        new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "El valor no cumple con el formato esperado para el tipo de medio de contacto",
                                null
                        )
                );
            }

        } catch (PatternSyntaxException e) {
            throw new ValidatorException(
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "El patrón de validación del tipo de medio de contacto no es válido",
                            null
                    )
            );
        }
    }
}
