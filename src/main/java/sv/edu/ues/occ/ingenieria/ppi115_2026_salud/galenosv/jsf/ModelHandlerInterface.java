package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.event.ActionEvent;

public interface ModelHandlerInterface <T> {
    
    public void nuevo();
    
    public void seleccionar(T registro) throws IllegalArgumentException;
    
    public void guardarHandler(ActionEvent ae) throws IllegalStateException;
    
    public void eliminarHandler(ActionEvent ae) throws IllegalStateException;
    
    public void obtenerRegistros(int first, int max) throws IllegalStateException;
    
    public int contar() throws IllegalStateException;
    
}
