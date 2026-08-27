/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
public class TipoDocumentoDAO extends DefaultDAO<TipoDocumento> {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public TipoDocumentoDAO(Class<TipoDocumento> entity) {
        super(entity);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    
}
