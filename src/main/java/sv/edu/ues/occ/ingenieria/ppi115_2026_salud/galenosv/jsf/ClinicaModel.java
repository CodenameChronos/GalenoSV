package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ClinicaModel extends ModelHandler<Clinica> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ClinicaDAO clDAO;
    
    private GenericLazyDataModel<Clinica> lazyModel;
    
    public ClinicaModel() {
        super(Clinica.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<Clinica> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<Clinica> getDAO() {
        return clDAO;
    }

    @Override
    public Clinica instanciarRegistro() {
        return new Clinica();
    }

    @Override
    public Clinica getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Clinica) clDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,
                "ID inválido recibido para Clinica: " + id, ex);
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Clinica registro) {
        return registro != null ? registro.getIdClinica() : null;
    }
    
}
