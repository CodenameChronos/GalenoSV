/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;

/**
 *
 * @author kardia
 */

@Stateless
@LocalBean
public class ExamenTipoExamenDAO extends DefaultDAO<ExamenTipoExamen> implements Serializable {
    
    @PersistenceContext(unitName = "Galeno-PU")
    EntityManager em;

    public ExamenTipoExamenDAO() {
        super(ExamenTipoExamen.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public List<ExamenTipoExamen> findByIdExamen(final UUID uuid, int first, int max){
        try {
            TypedQuery<ExamenTipoExamen> tq = em.createNamedQuery("ExamenTipoExamen.findByIdTipoExamen", ExamenTipoExamen.class);
            return tq.getResultList();
        } catch(Exception ex){
            return null;
        }
    }
    
    /*
    public int countByIdTipoExamen (final UUID idTipoExamen) {
        try {
            TypedQuery<ExamenTipoExamen> tq = em.createNamedQuery("ExamenTipoExamen.countByIdTipoExamen", Long.class);
            return tq.getFirstResult();
        } catch (Exception ex) {
            return -1;
        }
    }*/
    
}
