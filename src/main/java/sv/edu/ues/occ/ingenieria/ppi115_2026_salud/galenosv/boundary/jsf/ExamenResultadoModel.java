package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ExamenResultadoModel extends ModelHandler<ExamenResultado> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenResultadoDAO erDAO;

    public ExamenResultadoModel() {
        super(ExamenResultado.class);
    }

    @Override
    public DAOInterface<ExamenResultado> getDAO() {
        return erDAO;
    }

    @Override
    public ExamenResultado instanciarRegistro() {
        return new ExamenResultado();
    }

    @Override
    public ExamenResultado getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ExamenResultado registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
