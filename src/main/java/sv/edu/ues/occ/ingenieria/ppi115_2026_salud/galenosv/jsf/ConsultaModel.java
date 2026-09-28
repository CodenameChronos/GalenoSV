package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

@Named
@ViewScoped
public class ConsultaModel extends ModelHandler<Consulta> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaDAO coDAO;
    
    @Inject
    private PersonaRolDAO prDAO;
    
    private GenericLazyDataModel<Consulta> lazyModel;

    public ConsultaModel() {
        super(Consulta.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<Consulta> getDAO() {
        return coDAO;
    }

    public GenericLazyDataModel<Consulta> getLazyModel() {
        return lazyModel;
    }
    
    @Override
    public Consulta instanciarRegistro() {
        return new Consulta();
    }    

    @Override
    public Consulta getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Consulta) coDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Consulta: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Consulta registro) {
        return registro != null ? registro.getIdConsulta() : null;
    }
    
    /**
     * Método de búsqueda del p:autoComplete de examenes.
     *
     * @param texto texto escrito por el usuario; si está vacío no se
     * consulta la base de datos.
     * @return hasta 30 examenes cuyo nombre contenga el texto.
     */
    
    public List<PersonaRol> completarPersonaRol(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return prDAO.buscarPorNombresApellidos(texto, 30);
    }
    
}
