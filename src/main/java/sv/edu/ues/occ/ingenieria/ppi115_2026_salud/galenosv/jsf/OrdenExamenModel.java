package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

public class OrdenExamenModel extends ModelHandler<OrdenExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private OrdenExamenDAO oeDAO;

    public OrdenExamenModel() {
        super(OrdenExamen.class);
    }

    @Override
    public DAOInterface<OrdenExamen> getDAO() {
        return oeDAO;
    }

    @Override
    public OrdenExamen instanciarRegistro() {
        return new OrdenExamen();
    }
    
}
