/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaDAO;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

/**
 *
 * @author kardia
 */
public class ClinicaModel extends ModelHandler<Clinica> {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private ClinicaDAO clDAO;
    
    public ClinicaModel() {
        super(Clinica.class);
    }

    @Override
    public DAOInterface<Clinica> getDAO() {
        return clDAO;
    }

    @Override
    public Clinica instanciarRegistro() {
        return new Clinica();
    }
    
}
