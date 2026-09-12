package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
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

    @Override
    public Clinica getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(Clinica registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
