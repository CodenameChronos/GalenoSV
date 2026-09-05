/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;

/**
 *
 * @author kardia
 */
public class ExamenTipoExamenModel extends ModelHandler<ExamenTipoExamen> {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenTipoExamenDAO eteDAO;

    public ExamenTipoExamenModel() {
        super(ExamenTipoExamen.class);
    }

    @Override
    public DAOInterface<ExamenTipoExamen> getDAO() {
        return eteDAO;
    }

    @Override
    public ExamenTipoExamen instanciarRegistro() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
