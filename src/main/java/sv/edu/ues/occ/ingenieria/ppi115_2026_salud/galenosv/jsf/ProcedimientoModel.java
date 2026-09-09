package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

public class ProcedimientoModel extends ModelHandler<Procedimiento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoDAO prDAO;

    public ProcedimientoModel() {
        super(Procedimiento.class);
    }

    @Override
    public DAOInterface<Procedimiento> getDAO() {
        return prDAO;
    }

    @Override
    public Procedimiento instanciarRegistro() {
        return new Procedimiento();
    }
}
