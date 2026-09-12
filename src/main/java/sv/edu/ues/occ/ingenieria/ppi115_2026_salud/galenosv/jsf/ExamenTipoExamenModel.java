package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;

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

    public ExamenTipoExamenModel() {
        super(ExamenTipoExamen.class);
    }

    @Override
    public DAOInterface<ExamenTipoExamen> getDAO() {
        return eteDAO;
    }

    @Override
    public ExamenTipoExamen instanciarRegistro() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ExamenTipoExamen getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ExamenTipoExamen registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
