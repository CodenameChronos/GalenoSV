/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;

/**
 *
 * @author kardia
 */
public class ExamenResultadoModel extends ModelHandler<ExamenResultado> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ExamenResultadoDAO erDAO;

    public ExamenResultadoModel() {
        super(ExamenResultado.class);
    }

    @Override
    public DAOInterface<ExamenResultado> getDAO() {
        return erDAO;
    }

    @Override
    public ExamenResultado instanciarRegistro() {
        return new ExamenResultado();
    }
    
    
    
}
