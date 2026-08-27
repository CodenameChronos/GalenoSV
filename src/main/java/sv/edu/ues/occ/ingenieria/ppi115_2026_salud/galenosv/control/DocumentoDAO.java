/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;

/**
 *
 * @author kardia
 */
public class DocumentoDAO extends DefaultDAO<Documento> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public DocumentoDAO(Class<Documento> entity) {
        super(entity);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
    
}
