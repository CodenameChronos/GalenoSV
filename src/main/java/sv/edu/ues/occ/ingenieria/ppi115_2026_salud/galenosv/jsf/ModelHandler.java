package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;

/**
 *
 * @author kardia
 */
public abstract class ModelHandler<T> implements ModelHandlerInterface<T>, Serializable {

    protected List<T> registros;
    protected final Class<T> entity;
    protected T registroActual;
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;
    public abstract DAOInterface<T> getDAO();
    public abstract T instanciarRegistro();
    public abstract T getRegistroById(String id);
    public abstract Object getIdByRegistro(T registro);

    public ModelHandler(Class<T> entity) {
        this.estado = ESTADO_CRUD.NINGUNO;
        this.entity = entity;
    }
    
    @PostConstruct
    public void init() {
        estado = ESTADO_CRUD.NINGUNO;
        this.registroActual = instanciarRegistro();
    }
    
    @Override
    public void nuevo() {
        this.registroActual = instanciarRegistro();
        this.estado = ESTADO_CRUD.CREAR;
    }

    @Override
    public void guardarHandler(ActionEvent ae) throws IllegalArgumentException, IllegalStateException {
        try {
            if (estado == ESTADO_CRUD.CREAR) {
                getDAO().crear(registroActual);
            } else if (estado == ESTADO_CRUD.MODIFICAR) {
                getDAO().actualizar(registroActual);
            }
            this.estado = ESTADO_CRUD.NINGUNO;
        }  catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
        
    }

    @Override
    public void eliminarHandler(ActionEvent ae) throws IllegalArgumentException, IllegalStateException {
        try {
            getDAO().eliminar(registroActual);
            estado = ESTADO_CRUD.NINGUNO;
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }
    
    @Override
    public void cancelarHandler(ActionEvent ae) {
        this.estado = ESTADO_CRUD.NINGUNO;
    }
    
    @Override
    public int contar() {
        try {
            return getDAO().contar();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }
    
    @Override
    public void obtenerRegistros(int first, int max) {
        this.registros = getDAO().findRange(first, max);
    }

    public void seleccionar(SelectEvent<T> registro) {
        this.estado = ESTADO_CRUD.MODIFICAR;
        this.registroActual = registro.getObject();
    }
    
    public List<T> getRegistros() {
        return registros;
    }

    public Class<T> getEntity() {
        return entity;
    }

    public T getRegistroActual() {
        return registroActual;
    }

    public void setRegistroActual(T registroActual) {
        this.registroActual = registroActual;
    }

    public ESTADO_CRUD getEstado() {
        return estado;
    }

    public void setEstado(ESTADO_CRUD estado) {
        this.estado = estado;
    }

}
