package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;

public class ConsultaModel extends ModelHandler<Consulta> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ConsultaDAO coDAO;

    public ConsultaModel() {
        super(Consulta.class);
    }

    @Override
    public DAOInterface<Consulta> getDAO() {
        return coDAO;
    }

    @Override
    public Consulta instanciarRegistro() {
        return new Consulta();
    }    
    
}
