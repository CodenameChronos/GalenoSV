package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

public class PersonaRolModel extends ModelHandler<PersonaRol> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private PersonaRolDAO prDAO;

    public PersonaRolModel() {
        super(PersonaRol.class);
    }

    @Override
    public DAOInterface<PersonaRol> getDAO() {
        return prDAO;
    }

    @Override
    public PersonaRol instanciarRegistro() {
        return new PersonaRol();
    }
    
}
