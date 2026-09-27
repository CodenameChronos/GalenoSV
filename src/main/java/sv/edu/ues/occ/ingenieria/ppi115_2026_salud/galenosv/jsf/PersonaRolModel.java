package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Named
@ViewScoped
public class PersonaRolModel extends ModelHandler<PersonaRol> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PersonaRolDAO prDAO;

    @Inject
    private PersonaDAO personaDAO;

    @Inject
    private RolDAO rolDAO;

    @Inject
    private ClinicaDAO clinicaDAO;

    private GenericLazyDataModel<PersonaRol> lazyModel;

    private UUID idClinicaSeleccionada;

    public PersonaRolModel() {
        super(PersonaRol.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<PersonaRol> getLazyModel() {
        return lazyModel;
    }

    public List<Persona> getPersonas() {
        return personaDAO.findRange(0, 1000);
    }

    public List<Rol> getRoles() {
        return rolDAO.findRange(0, 1000);
    }

    public List<Clinica> getClinicas() {
        return clinicaDAO.findRange(0, 1000);
    }

    public UUID getIdClinicaSeleccionada() {
        return idClinicaSeleccionada;
    }

    public void setIdClinicaSeleccionada(UUID idClinicaSeleccionada) {
        this.idClinicaSeleccionada = idClinicaSeleccionada;
    }

    @Override
    public void nuevo() {
        super.nuevo();
        idClinicaSeleccionada = null;
    }

    @Override
    public void seleccionar(SelectEvent<PersonaRol> registro) {
        super.seleccionar(registro);

        PersonaRol personaRol = registro.getObject();

        if (personaRol.getIdClinica() != null) {
            idClinicaSeleccionada =
                    personaRol.getIdClinica().getIdClinica();
        } else {
            idClinicaSeleccionada = null;
        }
    }

    @Override
    public void guardarHandler(ActionEvent ae)
            throws IllegalArgumentException, IllegalStateException {

        if (idClinicaSeleccionada == null) {
            getRegistroActual().setIdClinica(null);
        } else {
            Clinica clinica = (Clinica) clinicaDAO.buscar(idClinicaSeleccionada);
            getRegistroActual().setIdClinica(clinica);
        }

        super.guardarHandler(ae);
    }

    @Override
    public void cancelarHandler(ActionEvent ae) {
        super.cancelarHandler(ae);
        idClinicaSeleccionada = null;
    }

    @Override
    public DAOInterface<PersonaRol> getDAO() {
        return prDAO;
    }

    @Override
    public PersonaRol instanciarRegistro() {
        return new PersonaRol();
    }

    @Override
    public PersonaRol getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (PersonaRol) prDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "ID inválido recibido para PersonaRol: " + id,
                    ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(PersonaRol registro) {
        return registro != null
                ? registro.getIdPersonaRol()
                : null;
    }
}