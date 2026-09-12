package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class RolModel extends ModelHandler<Rol> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private RolDAO rDAO;

    public RolModel() {
        super(Rol.class);
    }

    @Override
    public DAOInterface<Rol> getDAO() {
        return rDAO;
    }

    @Override
    public Rol instanciarRegistro() {
        return new Rol();
    }

    @Override
    public Rol getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(Rol registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
