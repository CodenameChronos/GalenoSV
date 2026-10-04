package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

@Named
@SessionScoped
public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Persona persona;
    private Clinica clinica;
    private Rol rol;
    private PersonaRol personaRol;

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public PersonaRol getPersonaRol() {
        return personaRol;
    }

    public void establecerSesion(PersonaRol personaRol) {
        if (personaRol == null) {
            return;
        }

        this.persona = personaRol.getIdPersona();
        this.clinica = personaRol.getIdClinica();
        this.rol = personaRol.getIdRol();
        this.personaRol = personaRol;
    }

    public void cerrarSesion() {
        this.persona = null;
        this.clinica = null;
        this.rol = null;
        this.personaRol = null;
    }
}
