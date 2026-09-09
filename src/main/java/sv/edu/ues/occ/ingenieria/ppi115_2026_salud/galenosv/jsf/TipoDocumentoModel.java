package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

public class TipoDocumentoModel extends ModelHandler<TipoDocumento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoDocumentoDAO tdDAO;

    public TipoDocumentoModel() {
        super(TipoDocumento.class);
    }

    @Override
    public DAOInterface<TipoDocumento> getDAO() {
        return tdDAO;
    }

    @Override
    public TipoDocumento instanciarRegistro() {
        return new TipoDocumento();
    }
}
