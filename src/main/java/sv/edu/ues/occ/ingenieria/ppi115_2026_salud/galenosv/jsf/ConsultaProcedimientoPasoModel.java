package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelHandler<ConsultaProcedimientoPaso> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaProcedimientoPasoDAO coppDAO;
    
    @Inject
    private ConsultaProcedimientoDAO conspDAO;
    
    @Inject
    private PersonaRolDAO prDAO;
    
    private GenericLazyDataModel<ConsultaProcedimientoPaso> lazyModel;

    public ConsultaProcedimientoPasoModel() {
        super(ConsultaProcedimientoPaso.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<ConsultaProcedimientoPaso> getDAO() {
        return coppDAO;
    }

    public GenericLazyDataModel<ConsultaProcedimientoPaso> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ConsultaProcedimientoPaso instanciarRegistro() {
        return new ConsultaProcedimientoPaso();
    }

    @Override
    public ConsultaProcedimientoPaso getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ConsultaProcedimientoPaso) coppDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para ConsultaProcedimientoPaso: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimientoPaso registro) {
        return registro != null ? registro.getIdConsultaProcedimientoPaso() : null;
    }
    
    /** Método de búsqueda del p:autoComplete de ConsultaProcedimiento.
     * @param consulta
     * @return  */
    public List<ConsultaProcedimiento> completarConsultaProcedimiento(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            return List.of();
        }
        return conspDAO.buscarPorNombreProcedimiento(consulta, 30);
    }

    /**
     * Etiqueta para el autoComplete y la columna de ConsultaProcedimiento:
     * nombre del procedimiento más el nombre de la persona de la consulta a
     * la que pertenece, para distinguir entre instancias del mismo
     * procedimiento en consultas distintas.
     * @param consp
     * @return 
     */
    public String etiquetaConsultaProcedimiento(ConsultaProcedimiento consp) {
        if (consp == null || consp.getIdProcedimiento() == null) {
            return "";
        }
        String nombreProcedimiento = consp.getIdProcedimiento().getNombre();
        String nombrePersona = "";
        if (consp.getIdConsulta() != null
                && consp.getIdConsulta().getIdPersonaRol() != null
                && consp.getIdConsulta().getIdPersonaRol().getIdPersona() != null) {
            nombrePersona = consp.getIdConsulta().getIdPersonaRol().getIdPersona().getNombreCompleto();
        }
        return nombreProcedimiento + " — " + nombrePersona;
    }

    /** Método de búsqueda del p:autoComplete de PersonaRol.
     * @param consulta
     * @return  */
    public List<PersonaRol> completarPersonaRol(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            return List.of();
        }
        return prDAO.buscarPorNombresApellidos(consulta, 30);
    }

    /**
     * Etiqueta para el autoComplete y la columna de PersonaRol: nombre
     * completo de la persona más su rol, para diferenciar cuando la misma
     * persona tiene más de un rol asignado.
     * @param pr
     * @return 
     */
    public String etiquetaPersonaRol(PersonaRol pr) {
        if (pr == null || pr.getIdPersona() == null) {
            return "";
        }
        String rol = pr.getIdRol() != null ? pr.getIdRol().getNombre() : "";
        return pr.getIdPersona().getNombreCompleto() + " (" + rol + ")";
    }
    
}
