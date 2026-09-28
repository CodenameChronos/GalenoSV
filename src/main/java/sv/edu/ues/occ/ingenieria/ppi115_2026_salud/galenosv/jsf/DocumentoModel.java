package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class DocumentoModel extends ModelHandler<Documento> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private DocumentoDAO docDAO;
    
    @Inject
    private PersonaDAO pDAO;
    
    @Inject
    private TipoDocumentoDAO tdDAO;
    
       private GenericLazyDataModel<Documento> lazyModel;

    public DocumentoModel() {
        super(Documento.class);
        
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    @Override
    public DAOInterface<Documento> getDAO() {
        return docDAO;
    }

     public GenericLazyDataModel<Documento> getLazyModel() {
        return lazyModel;
    }
     
    @Override
    public Documento instanciarRegistro() {
        return new Documento();
    }

    @Override
    public Documento getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Documento) docDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Documento: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Documento registro) {
        return registro != null ? registro.getIdDocumento() : null;
    }
    
    public List<Persona> completarPersona(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return pDAO.buscarPorNombre(texto, 30);
    }
    
    public List<TipoDocumento> completarTipoDocumento(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return tdDAO.buscarPorNombre(texto, 30);
    }
    
    
}
