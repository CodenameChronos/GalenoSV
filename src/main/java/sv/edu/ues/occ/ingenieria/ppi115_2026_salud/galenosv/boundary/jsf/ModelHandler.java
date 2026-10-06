package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ParentServiceInterface;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.util.Mensajes;

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

    protected String filtroPropiedad;
    protected Object filtroValor;

    public void setFiltro(String propiedad, Object valor) {
        this.filtroPropiedad = propiedad;
        this.filtroValor = valor;
    }

    public void limpiarFiltro() {
        this.filtroPropiedad = null;
        this.filtroValor = null;
    }

    public List<T> buscarRegistros(int first, int max) {
        if (filtroPropiedad != null) {
            return getDAO().findRangeByCriterio(filtroPropiedad, filtroValor, first, max);
        }
        return getDAO().findRange(first, max);
    }

    /**
     * Estado actual en el que se encuentra el flujo o la interfaz del CRUD.
     */
    protected ESTADO_CRUD estado = ESTADO_CRUD.NINGUNO;

    /**
     * Obtiene la interfaz de acceso a datos (DAO) correspondiente a la entidad.
     *
     * @return La implementación de {@link ParentServiceInterface} para las operaciones de
     * persistencia.
     */
    public abstract ParentServiceInterface<T> getDAO();

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
     *
     * @param entity La clase de la entidad (tipo T) que gestionará
     * estecontrolador.
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
        resetInputs("pnlCrear");
    }

    /**
     * Selecciona un registro existente para su visualización o modificación,
     * asignándolo al registro actual y cambiando el estado del CRUD a
     * MODIFICAR.
     *
     * @param registro El objeto de tipo SelectEvent generico que ha sido
     * seleccionado por el usuario.
     */
    public void seleccionar(SelectEvent<T> registro) {
        this.estado = ESTADO_CRUD.MODIFICAR;
        this.registroActual = registro.getObject();
        resetInputs("pnlCrear");
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
            Mensajes.exito("mensaje.exito");
            
        } catch (Exception ex) {
            resetInputs("pnlCrear");
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Mensajes.error("mensaje.error");
        }

    }

    /**
     * Maneja el evento de eliminación de un registro desde la interfaz de
     * usuario, ejecutando la baja a través del DAO, restableciendo el estado
     * del CRUD a NINGUNO y actualizando la lista de registros disponibles.
     *
     * @param ae El evento de acción (ActionEvent) desencadenado por el
     * componente de la vista.
     * @throws IllegalArgumentException si el registro actual a eliminar no es
     * válido.
     * @throws IllegalStateException si ocurre un error durante el proceso de
     * eliminación en la capa de persistencia.
     */
    @Override
    public void eliminarHandler(ActionEvent ae) throws IllegalArgumentException, IllegalStateException {
        try {
            getDAO().eliminar(registroActual);
            estado = ESTADO_CRUD.NINGUNO;
            Mensajes.exito("mensaje.exito");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void cancelarHandler(ActionEvent ae) {
        this.estado = ESTADO_CRUD.NINGUNO;
        this.registroActual = instanciarRegistro();
        resetInputs("pnlCrear");
    }

    /**
     * Obtiene el número total de registros disponibles llamando a la capa DAO,
     * manejando cualquier excepción imprevista mediante el registro de logs y
     * relanzándola como IllegalStateException.
     *
     * @return La cantidad total de registros existentes.
     * @throws IllegalStateException si ocurre un error durante la operación de
     * conteo.
     */
    /* @Override
    public int contar() throws IllegalStateException {
        try {
            return getDAO().contar();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }*/
    @Override
    public int contar() throws IllegalStateException {
        try {
            if (filtroPropiedad != null) {
                return getDAO().contarByCriterio(filtroPropiedad, filtroValor);
            }
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

    /**
     * Restablece el estado local (valor enviado y validez) de todos los
     * componentes de entrada (inputs) asociados al contenedor identificado por
     * el id indicado. Esto es necesario porque JSF retiene el valor y estado de
     * validación de un input tras un fallo de validación, incluso si el modelo
     * subyacente cambia posteriormente (por ejemplo, al seleccionar un nuevo
     * registro o al iniciar la creación de uno).
     *
     * Si el contenedor pertenece a un composite component (por ejemplo,
     * g:crud), el reinicio se realiza sobre todo el composite y no solo sobre
     * el subárbol del contenedor. Los campos del formulario se declaran como
     * facet del composite, por lo que no son descendientes del contenedor en el
     * árbol de componentes, aunque se rendericen dentro de él.
     *
     * @param simpleId El id simple (no el clientId completo) del contenedor del
     * formulario de creación/edición, típicamente pnlCrear.
     */
    private void resetInputs(String simpleId) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        UIComponent encontrado = buscarPorId(ctx.getViewRoot(), simpleId);
        if (encontrado == null) {
            return;
        }
        UIComponent composite = UIComponent.getCompositeComponentParent(encontrado);
        resetInputsRecursivo(composite != null ? composite : encontrado);
    }

    /**
     * Busca recursivamente, en el árbol de componentes de Faces, aquel
     * componente cuyo id simple coincida con el indicado. La búsqueda recorre
     * tanto los hijos normales como los facets de cada componente (mediante
     * {@link UIComponent#getFacetsAndChildren()}), ya que un componente
     * objetivo puede encontrarse dentro de un facet (por ejemplo, cuando la
     * vista se compone a través de un composite component con
     * {@code <f:facet>}) y no sería localizado si solo se recorrieran los hijos
     * directos.
     *
     * @param raiz El componente raíz desde el cual iniciar la búsqueda.
     * @param id El id simple del componente que se desea encontrar.
     * @return El componente encontrado, o {@code null} si ningún componente en
     * el subárbol coincide con el id indicado.
     */
    private UIComponent buscarPorId(UIComponent raiz, String id) {
        if (id.equals(raiz.getId())) {
            return raiz;
        }
        Iterator<UIComponent> it = raiz.getFacetsAndChildren();
        while (it.hasNext()) {
            UIComponent resultado = buscarPorId(it.next(), id);
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    /**
     * Recorre recursivamente el subárbol de componentes a partir del componente
     * indicado, reiniciando el valor enviado y el estado de validación de cada
     * componente de entrada ({@link UIInput}) encontrado mediante
     * {@link UIInput#resetValue()}. Al igual que la búsqueda por id, el
     * recorrido incluye tanto los hijos normales como los facets de cada
     * componente.
     *
     * @param componente El componente raíz del subárbol sobre el cual se
     * reiniciarán los inputs encontrados.
     */
    private void resetInputsRecursivo(UIComponent componente) {
        if (componente instanceof UIInput) {
            ((UIInput) componente).resetValue();
        }
        Iterator<UIComponent> it = componente.getFacetsAndChildren();
        while (it.hasNext()) {
            resetInputsRecursivo(it.next());
        }
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
