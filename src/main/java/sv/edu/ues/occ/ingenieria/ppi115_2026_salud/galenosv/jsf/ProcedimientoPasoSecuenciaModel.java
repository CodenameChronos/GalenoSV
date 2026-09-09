package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

public class ProcedimientoPasoSecuenciaModel extends ModelHandler<ProcedimientoPasoSecuencia> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoSecuenciaDAO ppsDAO;

    public ProcedimientoPasoSecuenciaModel() {
        super(ProcedimientoPasoSecuencia.class);
    }

    @Override
    public DAOInterface<ProcedimientoPasoSecuencia> getDAO() {
        return ppsDAO;
    }

    @Override
    public ProcedimientoPasoSecuencia instanciarRegistro() {
        return new ProcedimientoPasoSecuencia();
    }
}
