package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

@Named
@ViewScoped
public class SesionModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PersonaRolDAO prDAO;

    @Inject
    private Sesion sesion;

    private PersonaRol personaRolSeleccionado;

    public PersonaRol getPersonaRolSeleccionado() {
        return personaRolSeleccionado;
    }

    public void setPersonaRolSeleccionado(PersonaRol personaRolSeleccionado) {
        this.personaRolSeleccionado = personaRolSeleccionado;
    }

    public List<PersonaRol> completarPersonaRol(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }

        return prDAO.buscarPorNombresApellidos(texto, 30);
    }

    public void establecerSesion() {
        if (personaRolSeleccionado != null) {
            sesion.establecerSesion(personaRolSeleccionado);
        }
    }

    public void cerrarSesion() {
        sesion.cerrarSesion();
        personaRolSeleccionado = null;
    }
}