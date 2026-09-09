package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

@Named()
@ViewScoped
public class TipoMedioContactoModel extends ModelHandler<TipoMedioContacto> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoMedioContactoDAO tmcDAO;
    
    public TipoMedioContactoModel() {
        super(TipoMedioContacto.class);
    }

    @Override
    public DAOInterface<TipoMedioContacto> getDAO() {
        return tmcDAO;
    }

    @Override
    public TipoMedioContacto instanciarRegistro() {
        return new TipoMedioContacto();
    }

    
}
