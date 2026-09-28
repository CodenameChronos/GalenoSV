package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoMedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoMedioContacto;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class MedioContactoModel extends ModelHandler<MedioContacto> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MedioContactoDAO mcDAO;
    
    @Inject
    private PersonaDAO pDAO;
    
    @Inject
    private TipoMedioContactoDAO tmcDAO;
    
    private GenericLazyDataModel<MedioContacto> lazyModel;

    public MedioContactoModel() {
        super(MedioContacto.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<MedioContacto> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<MedioContacto> getDAO() {
    return mcDAO;
    }

    @Override
    public MedioContacto instanciarRegistro() {
        return new MedioContacto();
    }

    @Override
    public MedioContacto getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (MedioContacto) mcDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para MedioContacto: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(MedioContacto registro) {
        return registro != null ? registro.getIdMedioContacto() : null;
    }
    
    public List<Persona> completarPersona(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return pDAO.buscarPorNombre(texto, 30);
    }
    
    public List<TipoMedioContacto> completarTipoMedioContacto(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        return tmcDAO.buscarPorNombre(texto, 30);
    }
}
