/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kardia
 */
public abstract class DefaultDAO<T> implements DAOInterface<T> {

    public final Class<T> entity;

    public abstract EntityManager getEntityManager();

    public DefaultDAO(Class<T> entity) {
        this.entity = entity;
    }

    @Override
    public void crear(T registro) throws IllegalArgumentException, IllegalStateException {
        if (registro != null) {
            try {
                getEntityManager().persist(registro);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Pendejo");
        }

    }

    @Override
    public void actualizar(T nuevo) throws IllegalArgumentException, IllegalStateException {
        if (nuevo != null) {
            try {
                getEntityManager().merge(nuevo);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Pendejo");
        }

    }

    @Override
    public void eliminar(T eliminar) {
        if (eliminar != null) {
            try {
                getEntityManager().remove(eliminar);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Pendejo");
        }

    }

    @Override
    public T buscar(UUID uuid) {
        if (uuid != null) {
            try {
                return getEntityManager().find(entity, uuid);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Pendejo");
        }

    }

    @Override
    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException {
        if (first >= 0 && max > 0) {
            try {
                return getEntityManager().createQuery("SELECT e FROM " + entity.getSimpleName() + " e", entity)
                                          .setFirstResult(first)
                                          .setMaxResults(max)
                                          .getResultList();
            } catch (Exception ex) {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Pendejo");
        }
    }
}
