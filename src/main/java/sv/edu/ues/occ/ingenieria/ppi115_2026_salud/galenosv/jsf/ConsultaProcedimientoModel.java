package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;

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
    
    
    
}
