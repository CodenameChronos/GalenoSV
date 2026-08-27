/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author kardia
 */
public interface DAOInterface<T> {
    
    public void crear(T registro) throws IllegalArgumentException, IllegalStateException;
    
    public void actualizar(T nuevo)throws IllegalArgumentException, IllegalStateException;
    
    public void eliminar(T eliminar) throws IllegalArgumentException, IllegalStateException;
    
    public T buscar(UUID uuid) throws IllegalArgumentException, IllegalStateException;
    
    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException;
    
}
