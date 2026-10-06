package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;
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
    private ClinicaService clService;
    
    private GenericLazyDataModel<Clinica> lazyModel;
    
    public ClinicaModel() {
        super(Clinica.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }
    
    public GenericLazyDataModel<Clinica> getLazyModel() {
        return lazyModel;
    }

    @Override
    public ParentServiceInterface<Clinica> getDAO() {
        return clService;
    }

    @Override
    public Clinica instanciarRegistro() {
        return new Clinica();
    }

    @Override
    public Clinica getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Clinica) clService.buscar(uuid);
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
