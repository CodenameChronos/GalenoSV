package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoService;
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
    private ExamenResultadoService erService;

    public ExamenResultadoModel() {
        super(ExamenResultado.class);
    }

    @Override
    public ParentServiceInterface<ExamenResultado> getDAO() {
        return erService;
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
