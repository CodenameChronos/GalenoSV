package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;

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
    
    
    
}
