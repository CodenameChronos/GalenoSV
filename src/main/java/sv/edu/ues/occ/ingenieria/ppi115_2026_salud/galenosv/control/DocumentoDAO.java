/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class DocumentoDAO extends DefaultDAO<Documento> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public DocumentoDAO() {
        super(Documento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
    
}
