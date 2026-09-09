package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;

public class ProcedimientoPasoExamenModel extends ModelHandler<ProcedimientoPasoExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoExamenDAO ppeDAO;

    public ProcedimientoPasoExamenModel() {
        super(ProcedimientoPasoExamen.class);
    }

    @Override
    public DAOInterface<ProcedimientoPasoExamen> getDAO() {
        return ppeDAO;
    }

    @Override
    public ProcedimientoPasoExamen instanciarRegistro() {
        return new ProcedimientoPasoExamen();
    }
}
