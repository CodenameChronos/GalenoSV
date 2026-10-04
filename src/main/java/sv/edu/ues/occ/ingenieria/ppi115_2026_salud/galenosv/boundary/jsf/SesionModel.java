package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.SesionDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Named
@ViewScoped
public class SesionModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private SesionDAO sesionDAO;

    @Inject
    private Sesion sesion;

    private Clinica clinica;
    private Rol rol;

    private PersonaRol sesionPersonaRol;

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

    public PersonaRol getSesionPersonaRol() {
        return sesionPersonaRol;
    }

    public void setSesionPersonaRol(PersonaRol sesionPersonaRol) {
        this.sesionPersonaRol = sesionPersonaRol;
    }

    public boolean isRolHabilitado() {
        return clinica != null;
    }

    public boolean isPersonaHabilitada() {
        return clinica != null && rol != null;
    }

    public void alCambiarClinica() {
        rol = null;
        sesionPersonaRol = null;
    }

    public void alCambiarRol() {
        sesionPersonaRol = null;
    }

    public List<Clinica> completarClinica(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return sesionDAO.buscarClinicas(texto, 10);
    }

    /**
     * Sugerencias de roles para el segundo campo. Son todos los roles
     * existentes; si nadie lo tiene en la clínica elegida, el siguiente campo
     * simplemente no tendrá sugerencias.
     *
     * @param texto lo que el usuario lleva escrito.
     * @return hasta 10 roles cuyo nombre coincide; vacía si no hay texto o si
     * todavía no hay clínica.
     */
    public List<Rol> completarRol(String texto) {
        if (clinica == null || texto == null || texto.isBlank()) {
            return List.of();
        }
        return sesionDAO.buscarRoles(texto, 10);
    }

    /**
     * Sugerencias de personas para el tercer campo: solo quienes tienen el rol
     * elegido en la clínica elegida.
     *
     * @param texto lo que el usuario lleva escrito.
     * @return hasta 30 asignaciones; vacía si no hay texto, si falta clínica o
     * rol, o si nadie cumple (entonces el campo muestra "sin resultados").
     */
    public List<PersonaRol> completarPersonaRol(String texto) {
        if (clinica == null || rol == null || texto == null || texto.isBlank()) {
            return List.of();
        }
        return sesionDAO.buscarPersonaRol(clinica, rol, texto, 30);
    }

    public void establecerSesion() {
        if (sesionPersonaRol == null) {
            return;
        }
        boolean coincide = Objects.equals(sesionPersonaRol.getIdClinica(), clinica)
                && Objects.equals(sesionPersonaRol.getIdRol(), rol);
        if (!coincide) {
            agregarMensaje(FacesMessage.SEVERITY_ERROR, "sesion.error.inconsistente");
            return;
        }
        sesion.establecerSesion(sesionPersonaRol);
    }

    public void cerrarSesion() {
        sesion.cerrarSesion();
        clinica = null;
        rol = null;
        sesionPersonaRol = null;
    }

    /**
     * Agrega un mensaje global con el texto del bundle para que
     * respete el idioma elegido.
     *
     * @param severidad el tipo de mensaje (info, advertencia, error).
     * @param clave la clave del texto en el bundle.
     */
    private void agregarMensaje(FacesMessage.Severity severidad, String clave) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String texto = ctx.getApplication().getResourceBundle(ctx, "msg").getString(clave);
        ctx.addMessage(null, new FacesMessage(severidad, texto, null));
    }
}