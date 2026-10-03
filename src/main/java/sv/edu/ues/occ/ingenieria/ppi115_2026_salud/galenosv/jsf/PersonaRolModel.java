package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
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

    public PersonaRolModel() {
        super(PersonaRol.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<PersonaRol> getLazyModel() {
        return lazyModel;
    }

    public List<Persona> completarPersona(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return personaDAO.buscarPorNombre(texto, 30);
    }

    public List<Rol> completarRol(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return rolDAO.buscarPorNombre(texto, 30);
    }

    public List<Clinica> completarClinica(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return clinicaDAO.buscarPorNombre(texto, 30);
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

    @Override
    public void guardarHandler(ActionEvent ae) throws IllegalArgumentException, IllegalStateException {
        UUID idPersona = registroActual.getIdPersona() != null ? registroActual.getIdPersona().getIdPersona() : null;
        UUID idRol = registroActual.getIdRol() != null ? registroActual.getIdRol().getIdRol() : null;
        UUID idClinica = registroActual.getIdClinica() != null ? registroActual.getIdClinica().getIdClinica() : null;
        UUID idExcluir = estado == ESTADO_CRUD.MODIFICAR ? registroActual.getIdPersonaRol() : null;

        try {
            boolean duplicado = idPersona != null && idRol != null
                    && prDAO.existeRolEnClinica(idPersona, idRol, idClinica, idExcluir);

            Logger.getLogger(getClass().getName()).log(Level.INFO,
                    "Validacion duplicado -> idPersona={0}, idRol={1}, idClinica={2}, idExcluir={3}, resultado={4}",
                    new Object[]{idPersona, idRol, idClinica, idExcluir, duplicado});

            if (duplicado) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Esta persona ya tiene asignado ese rol en la clínica seleccionada.", null));
                return;
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al validar duplicado de PersonaRol", ex);
            throw ex;
        }

        super.guardarHandler(ae);
    }
}
