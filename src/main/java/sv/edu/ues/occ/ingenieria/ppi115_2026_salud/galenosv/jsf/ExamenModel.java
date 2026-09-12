package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ExamenModel extends ModelHandler<Examen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenDAO exDAO;

    public ExamenModel() {
        super(Examen.class);
    }

    @Override
    public DAOInterface<Examen> getDAO() {
        return exDAO;
    }

    @Override
    public Examen instanciarRegistro() {
        return new Examen();
    }

    @Override
    public Examen getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(Examen registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
