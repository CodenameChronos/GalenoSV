package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

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
}