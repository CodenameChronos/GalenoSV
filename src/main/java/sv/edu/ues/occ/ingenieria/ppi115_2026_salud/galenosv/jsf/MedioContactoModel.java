package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;

public class MedioContactoModel extends ModelHandler<MedioContacto> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MedioContactoDAO mcDAO;

    public MedioContactoModel() {
        super(MedioContacto.class);
    }

    @Override
    public DAOInterface<MedioContacto> getDAO() {
    return mcDAO;
    }

    @Override
    public MedioContacto instanciarRegistro() {
        return new MedioContacto();
    }
}
