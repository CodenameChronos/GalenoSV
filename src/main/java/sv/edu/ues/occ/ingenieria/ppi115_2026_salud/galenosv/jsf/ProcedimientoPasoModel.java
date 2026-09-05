/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

/**
 *
 * @author kardia
 */
public class ProcedimientoPasoModel extends ModelHandler<ProcedimientoPaso> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoDAO ppDAO;

    public ProcedimientoPasoModel() {
        super(ProcedimientoPaso.class);
    }

    @Override
    public DAOInterface<ProcedimientoPaso> getDAO() {
        return ppDAO;
    }

    @Override
    public ProcedimientoPaso instanciarRegistro() {
        return new ProcedimientoPaso();
    }
}
