package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author kardia
 */
public interface DAOInterface<T> {

    public void crear(Object registro) throws IllegalArgumentException, IllegalStateException;

    public void actualizar(Object nuevo) throws IllegalArgumentException, IllegalStateException;

    public void eliminar(Object eliminar) throws IllegalArgumentException, IllegalStateException;

    public Object buscar(Object uuid) throws IllegalArgumentException, IllegalStateException;

    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException;
    
    public List<T> findRangeByCriterio(String propiedad, Object valor, int first, int max)
            throws IllegalArgumentException, IllegalStateException;

    public int contarByCriterio(String propiedad, Object valor);
    
    public int contar();

}
