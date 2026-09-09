package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;

public class DocumentoModel extends ModelHandler<Documento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private DocumentoDAO docDAO;

    public DocumentoModel() {
        super(Documento.class);
    }

    @Override
    public DAOInterface<Documento> getDAO() {
        return docDAO;
    }

    @Override
    public Documento instanciarRegistro() {
        return new Documento();
    }
    
    
    
}
