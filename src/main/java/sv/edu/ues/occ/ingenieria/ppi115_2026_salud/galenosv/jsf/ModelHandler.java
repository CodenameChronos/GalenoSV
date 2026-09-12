package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DAOInterface;

public abstract class ModelHandler<T> implements ModelHandlerInterface<T>, Serializable {

    /**
     * Lista que almacena los registros recuperados de la entidad para su
     * visualización o manipulación.
     */
    protected List<T> registros;
    public abstract T getRegistroById(String id);
    public abstract Object getIdByRegistro(T registro);

    /**
     * Clase de la entidad (tipo T) gestionada por este controlador.
     */
    protected final Class<T> entity;

    /**
     * Registro actual seleccionado, en edición o listo para ser creado.
     */
    protected T registroActual;

    /**
     * Estado actual en el que se encuentra el flujo o la interfaz del CRUD.
     */
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;

    /**
     * Obtiene la interfaz de acceso a datos (DAO) correspondiente a la entidad.
     *
     * @return La implementación de {@link DAOInterface} para las operaciones de
     * persistencia.
     */
    public abstract DAOInterface<T> getDAO();

    /**
     * Instancia un nuevo objeto de la entidad para ser utilizado como registro
     * actual.
     *
     * @return Una nueva instancia limpia de la entidad de tipo T.
     */
    public abstract T instanciarRegistro();
    
     
    /**
     * Constructor de la clase ModelHandler que inicializa el manejador
     * asignando la entidad objetivo.
     * @param entity La clase de la entidad (tipo T) que gestionará estecontrolador.
     */
    public ModelHandler(Class<T> entity) {
        this.estado = ESTADO_CRUD.NINGUNO;
        this.entity = entity;
    }
    
    
    /**
     * Método de inicialización del bean ejecutado automáticamente después de
     * que la dependencia ha sido inyectada. Establece el estado inicial del
     * CRUD en NINGUNO, carga los registros existentes e instancia un nuevo
     * registro actual para su uso en el componente.
     */
    @PostConstruct
    public void init() {
        estado = ESTADO_CRUD.NINGUNO;
        this.registroActual = instanciarRegistro();
    }
    
    /**
     * Prepara el controlador para la creación de un nuevo registro,
     * instanciando un objeto vacío y cambiando el estado del CRUD a CREAR.
     */
    @Override
    public void nuevo() {
        this.registroActual = instanciarRegistro();
        this.estado = ESTADO_CRUD.CREAR;
    }
    
    /**
     * Selecciona un registro existente para su visualización o modificación,
     * asignándolo al registro actual y cambiando el estado del CRUD a
     * MODIFICAR.
     *
     * @param registro El objeto de tipo SelectEvent generico que ha sido seleccionado por el usuario.
     */
    public void seleccionar(SelectEvent<T> registro) {
        this.estado = ESTADO_CRUD.MODIFICAR;
        this.registroActual = registro.getObject();
    }

    /**
     * Maneja el evento de guardado (creación o actualización) desde la interfaz
     * de usuario, evaluando el estado actual del CRUD para decidir si se
     * persiste un nuevo registro o se actualiza uno existente, y posteriormente
     * reinicia el estado y actualiza la lista.
     *
     * @param ae El evento de acción (ActionEvent) desencadenado por el
     * componente de la vista.
     * @throws IllegalArgumentException si los datos del registro actual no son
     * válidos.
     * @throws IllegalStateException si ocurre un error durante la operación en
     * la capa de persistencia.
     */
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
    
    
    /**
     * Maneja el evento de eliminación de un registro desde la interfaz de
     * usuario, ejecutando la baja a través del DAO, restableciendo el estado
     * del CRUD a NINGUNO y actualizando la lista de registros disponibles.
     *
     * @param ae El evento de acción (ActionEvent) desencadenado por el componente de la vista.
     * @throws IllegalArgumentException si el registro actual a eliminar no es válido.
     * @throws IllegalStateException si ocurre un error durante el proceso de eliminación en la capa de persistencia.
     */
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
    
    /**
     * Obtiene el número total de registros disponibles llamando a la capa DAO,
     * manejando cualquier excepción imprevista mediante el registro de logs y
     * relanzándola como IllegalStateException.
     *
     * @return La cantidad total de registros existentes.
     * @throws IllegalStateException si ocurre un error durante la operación de conteo.
     */
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
