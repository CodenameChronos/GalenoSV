/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

/**
 *
 * @author kardia
 */
public class PersonaModel extends ModelHandler<Persona> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PersonaDAO pDAO;

    public PersonaModel() {
        super(Persona.class);
    }

    @Override
    public DAOInterface<Persona> getDAO() {
        return pDAO;
    }

    @Override
    public Persona instanciarRegistro() {
        return new Persona();
    }
}
