package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
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

    @Override
    public TipoDocumento getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(TipoDocumento registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
