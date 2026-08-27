/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

/**
 *
 * @author kardia
 */
public class ClinicaDAO extends DefaultDAO<Clinica> {

    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ClinicaDAO(Class<Clinica> entity) {
        super(entity);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
