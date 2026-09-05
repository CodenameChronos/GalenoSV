/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

/**
 *
 * @author kardia
 */
public class RolModel extends ModelHandler<Rol> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private RolDAO rDAO;

    public RolModel() {
        super(Rol.class);
    }

    @Override
    public DAOInterface<Rol> getDAO() {
        return rDAO;
    }

    @Override
    public Rol instanciarRegistro() {
        return new Rol();
    }
    
}
