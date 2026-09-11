/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ProcedimientoPasoExamenModel extends ModelHandler<ProcedimientoPasoExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoExamenDAO ppeDAO;

    public ProcedimientoPasoExamenModel() {
        super(ProcedimientoPasoExamen.class);
    }

    @Override
    public DAOInterface<ProcedimientoPasoExamen> getDAO() {
        return ppeDAO;
    }

    @Override
    public ProcedimientoPasoExamen instanciarRegistro() {
        return new ProcedimientoPasoExamen();
    }

    @Override
    public ProcedimientoPasoExamen getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ProcedimientoPasoExamen registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
