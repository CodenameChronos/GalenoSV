package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

public class ClinicaModel extends ModelHandler<Clinica> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ClinicaDAO clDAO;
    
    public ClinicaModel() {
        super(Clinica.class);
    }

    @Override
    public DAOInterface<Clinica> getDAO() {
        return clDAO;
    }

    @Override
    public Clinica instanciarRegistro() {
        return new Clinica();
    }
    
}
