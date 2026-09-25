package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ExamenTipoExamenModel extends ModelHandler<ExamenTipoExamen> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenTipoExamenDAO eteDAO;
    
    @Inject
    private ExamenDAO exDAO;
    
    @Inject
    private TipoExamenDAO teDAO;
    
    private GenericLazyDataModel<ExamenTipoExamen> lazyModel;

    public ExamenTipoExamenModel() {
        super(ExamenTipoExamen.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<ExamenTipoExamen> getDAO() {
        return eteDAO;
    }

    public GenericLazyDataModel<ExamenTipoExamen> getLazyModel() {
        return lazyModel;
    }
    
    @Override
    public ExamenTipoExamen instanciarRegistro() {
        return new ExamenTipoExamen();
    }

    @Override
    public ExamenTipoExamen getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (ExamenTipoExamen) eteDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para ExamenTipoExamen: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(ExamenTipoExamen registro) {
        return registro != null ? registro.getIdExamenTipoExamen() : null;
    }
    
    /**
     * Método de búsqueda del p:autoComplete de examenes.
     *
     * @param texto texto escrito por el usuario; si está vacío no se
     * consulta la base de datos.
     * @return hasta 30 examenes cuyo nombre contenga el texto.
     */
    public List<Examen> completarExamen(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return exDAO.buscarPorNombre(texto, 30);
    }
    
    /**
     * Método de búsqueda del p:autoComplete de los tipos de examenes.
     *
     * @param texto texto escrito por el usuario; si está vacío no se
     * consulta la base de datos.
     * @return hasta 30 tipos de examenes cuyo nombre contenga el texto.
     */
    public List<TipoExamen> completarTipoExamen(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return teDAO.buscarPorNombre(texto, 30);
    }
}
