package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;

public class ExamenModel extends ModelHandler<Examen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenDAO exDAO;

    public ExamenModel() {
        super(Examen.class);
    }

    @Override
    public DAOInterface<Examen> getDAO() {
        return exDAO;
    }

    @Override
    public Examen instanciarRegistro() {
        return new Examen();
    }
    
    
    
}
