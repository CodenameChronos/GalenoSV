/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

/**
 *
 * @author kardia
 */
@Named
@ViewScoped
public class TipoExamenModel extends ModelHandler<TipoExamen> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private TipoExamenDAO teDAO;

    public TipoExamenModel() {
        super(TipoExamen.class);
    }
    
    @Override
    public DAOInterface<TipoExamen> getDAO(){
        return teDAO;
    }
    
    @Override
    public TipoExamen instanciarRegistro(){
        return new TipoExamen();
    }
    
}
