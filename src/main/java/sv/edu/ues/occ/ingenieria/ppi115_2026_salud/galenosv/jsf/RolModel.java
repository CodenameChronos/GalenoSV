package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Named
@ViewScoped
public class RolModel extends ModelHandler<Rol> {

    private static final long serialVersionUID = 1L;

    @Inject
    private RolDAO rolDAO;

    private GenericLazyDataModel<Rol> lazyModel;

    public RolModel() {
        super(Rol.class);
        this.lazyModel = new GenericLazyDataModel<>(this);
    }

    public GenericLazyDataModel<Rol> getLazyModel() {
        return lazyModel;
    }

    @Override
    public DAOInterface<Rol> getDAO() {
        return rolDAO;
    }

    @Override
    public Rol instanciarRegistro() {
        return new Rol();
    }

    @Override
    public Rol getRegistroById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return (Rol) rolDAO.buscar(uuid);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(getClass().getName()).log(
                Level.WARNING,
                "ID inválido recibido para Rol: " + id,
                ex
            );
            return null;
        }
    }

    @Override
    public Object getIdByRegistro(Rol registro) {
        return registro != null ? registro.getIdRol() : null;
    }
}