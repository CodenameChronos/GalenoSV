/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.MedioContactoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.MedioContacto;

/**
 *
 * @author kardia
 */
public class MedioContactoModel extends ModelHandler<MedioContacto> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private MedioContactoDAO mcDAO;

    public MedioContactoModel() {
        super(MedioContacto.class);
    }

    @Override
    public DAOInterface<MedioContacto> getDAO() {
    return mcDAO;
    }

    @Override
    public MedioContacto instanciarRegistro() {
        return new MedioContacto();
    }
}
