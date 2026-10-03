package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class PersonaModel extends ModelHandler<Persona> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PersonaDAO pDAO;
    
    @Inject
    private DocumentoModel documentoModel;

    @Inject
    private MedioContactoModel medioContactoModel;

    @Inject
    private PersonaRolModel personaRolModel;
    

    private GenericLazyDataModel<Persona> lazyModel;
    
    public PersonaModel() {
        super(Persona.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<Persona> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<Persona> getDAO() {
        return pDAO;
    }

    @Override
    public Persona instanciarRegistro() {;
        //Persona nuevoRegistro = new Persona();
        //nuevoRegistro.setFechaCreaacion(Date.from(LocalDateTime.now(atZone(ZoneId.systemDefault()).toIntstant)));
        return new Persona();
    }

    @Override
    public Persona getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Persona) pDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Persona: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Persona registro) {
        return registro != null ? registro.getIdPersona() : null;
    }
    
     @Override
    public void seleccionar(SelectEvent<Persona> registro) {
        super.seleccionar(registro);
        documentoModel.filtrarPorPersona(registroActual);
        medioContactoModel.filtrarPorPersona(registroActual);
        personaRolModel.filtrarPorPersona(registroActual);
        PrimeFaces.current().ajax().update(":layout:tabsPersona");
    }
    
    @Override
    public void nuevo() {
        super.nuevo();
        documentoModel.filtrarPorPersona(null);
        medioContactoModel.filtrarPorPersona(null);
        personaRolModel.filtrarPorPersona(null);
        PrimeFaces.current().ajax().update(":layout:tabsPersona");
    }
    
}
