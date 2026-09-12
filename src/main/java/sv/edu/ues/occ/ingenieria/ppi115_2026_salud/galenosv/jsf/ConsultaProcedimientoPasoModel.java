package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ConsultaProcedimientoPasoModel extends ModelHandler<ConsultaProcedimientoPaso> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaProcedimientoPasoDAO coppDAO;

    public ConsultaProcedimientoPasoModel() {
        super(ConsultaProcedimientoPaso.class);
    }

    @Override
    public DAOInterface<ConsultaProcedimientoPaso> getDAO() {
        return coppDAO;
    }

    @Override
    public ConsultaProcedimientoPaso instanciarRegistro() {
        return new ConsultaProcedimientoPaso();
    }

    @Override
    public ConsultaProcedimientoPaso getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ConsultaProcedimientoPaso registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
}
