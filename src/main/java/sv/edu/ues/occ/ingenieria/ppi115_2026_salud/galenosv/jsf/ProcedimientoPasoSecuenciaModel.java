/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class ProcedimientoPasoSecuenciaModel extends ModelHandler<ProcedimientoPasoSecuencia> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ProcedimientoPasoSecuenciaDAO ppsDAO;

    public ProcedimientoPasoSecuenciaModel() {
        super(ProcedimientoPasoSecuencia.class);
    }

    @Override
    public DAOInterface<ProcedimientoPasoSecuencia> getDAO() {
        return ppsDAO;
    }

    @Override
    public ProcedimientoPasoSecuencia instanciarRegistro() {
        return new ProcedimientoPasoSecuencia();
    }

    @Override
    public ProcedimientoPasoSecuencia getRegistroById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object getIdByRegistro(ProcedimientoPasoSecuencia registro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
