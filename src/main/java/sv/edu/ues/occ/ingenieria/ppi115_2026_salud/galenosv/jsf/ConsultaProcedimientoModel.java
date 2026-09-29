package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ConsultaProcedimientoModel extends ModelHandler<ConsultaProcedimiento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaProcedimientoDAO copDAO;
    
    @Inject
    private ProcedimientoDAO pDAO;
    
    @Inject
    private ConsultaDAO conDAO;
    
    private GenericLazyDataModel<ConsultaProcedimiento> lazyModel;

    // Estado de las pestañas de selección de entidades foráneas
    /** Sin criterio de búsqueda se cargan todos los registros. */
    private static final int MAX_REGISTROS = Integer.MAX_VALUE;

    private String filtroProcedimiento;
    private List<Procedimiento> resultadosProcedimiento;

    private String filtroConsulta;
    private List<Consulta> resultadosConsulta;

    public ConsultaProcedimientoModel() {
        super(ConsultaProcedimiento.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<ConsultaProcedimiento> getDAO() {
        return copDAO;
    }

    public GenericLazyDataModel<ConsultaProcedimiento> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ConsultaProcedimiento instanciarRegistro() {
        return new ConsultaProcedimiento();
    }

    @Override
    public ConsultaProcedimiento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ConsultaProcedimiento) copDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para ConsultaProcedimiento: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimiento registro) {
        return registro != null ? registro.getIdConsultaProcedimiento() : null;
    }
    
    /**
     * Busca según el filtro de la pestaña; si el filtro está vacío devuelve
     * todos los registros.
     */
    public void buscarProcedimiento() {
        String texto = filtroProcedimiento == null ? "" : filtroProcedimiento.trim();
        resultadosProcedimiento = pDAO.buscarPorNombre(texto, MAX_REGISTROS);
    }


    /**
     * Busca según el filtro de la pestaña; si el filtro está vacío devuelve
     * todos los registros.
     */
    public void buscarConsulta() {
        String texto = filtroConsulta == null ? "" : filtroConsulta.trim();
        resultadosConsulta = conDAO.buscarPorNombrePersona(texto, MAX_REGISTROS);
    }


    /**
    * Construye la etiqueta mostrada para la Consulta seleccionada: nombre
    * completo de la persona más la fecha de inicio, para distinguir entre
    * varias consultas de la misma persona.
    */
   public String etiquetaConsulta(Consulta c) {
       if (c == null || c.getIdPersonaRol() == null || c.getIdPersonaRol().getIdPersona() == null) {
           return "";
       }
       Persona p = c.getIdPersonaRol().getIdPersona();
       String fecha = "";
       if (c.getFechaInicio() != null) {
           fecha = new SimpleDateFormat("dd/MM/yyyy").format(c.getFechaInicio());
       }
       return p.getNombres() + " " + p.getApellidos() + " — " + fecha;
   }

    public String getFiltroProcedimiento() {
        return filtroProcedimiento;
    }

    public void setFiltroProcedimiento(String filtroProcedimiento) {
        this.filtroProcedimiento = filtroProcedimiento;
    }

    public List<Procedimiento> getResultadosProcedimiento() {
        if (resultadosProcedimiento == null) {
            buscarProcedimiento();
        }
        return resultadosProcedimiento;
    }

    public String getFiltroConsulta() {
        return filtroConsulta;
    }

    public void setFiltroConsulta(String filtroConsulta) {
        this.filtroConsulta = filtroConsulta;
    }

    public List<Consulta> getResultadosConsulta() {
        if (resultadosConsulta == null) {
            buscarConsulta();
        }
        return resultadosConsulta;
    }

}