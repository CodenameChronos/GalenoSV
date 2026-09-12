package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;

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

    public ConsultaProcedimientoModel() {
        super(ConsultaProcedimiento.class);
    }

    @Override
    public DAOInterface<ConsultaProcedimiento> getDAO() {
        return copDAO;
    }

    @Override
    public ConsultaProcedimiento instanciarRegistro() {
        return new ConsultaProcedimiento();
    }

    @Override
    public ConsultaProcedimiento getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimiento registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
