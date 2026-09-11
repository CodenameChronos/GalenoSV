package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.event.ActionEvent;

/**
 *
 * @author kardia
 */
public interface ModelHandlerInterface <T> {
    
    public void nuevo();
    
    public void guardarHandler(ActionEvent ae) throws IllegalStateException;
    
    public void eliminarHandler(ActionEvent ae) throws IllegalStateException;
    
    public void cancelarHandler(ActionEvent ae);
    
    public void obtenerRegistros(int first, int max) throws IllegalStateException;
    
    public int contar() throws IllegalStateException;
    
}
