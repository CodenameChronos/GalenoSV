package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class OrdenExamenModel extends ModelHandler<OrdenExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private OrdenExamenService oeService;

    public OrdenExamenModel() {
        super(OrdenExamen.class);
    }

    @Override
    public ParentServiceInterface<OrdenExamen> getDAO() {
        return oeService;
    }

    @Override
    public OrdenExamen instanciarRegistro() {
        return new OrdenExamen();
    }

    @Override
    public OrdenExamen getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(OrdenExamen registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
