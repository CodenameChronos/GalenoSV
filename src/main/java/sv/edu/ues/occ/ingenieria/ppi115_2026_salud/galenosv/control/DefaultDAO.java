package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kardia
 */
  

    public abstract class DefaultDAO<T> implements DAOInterface<T> {

        public final Class entity;

        public abstract EntityManager getEntityManager();

        public DefaultDAO(Class<T> entity) {
            this.entity = entity;
        }

        /**
         * Persiste un nuevo registro en la base de datos utilizando el
         * EntityManager.
         *
         * @param registro El objeto que se desea almacenar. No debe ser nulo.
         * @throws IllegalArgumentException si el objeto proporcionado es nulo.
         * @throws IllegalStateException si ocurre un error durante la operación
         * de persistencia.
         */
        @Override
        public void crear(Object registro) throws IllegalArgumentException, IllegalStateException {
            if (registro != null) {
                try {
                    getEntityManager().persist(registro);
                } catch (ConstraintViolationException cve) {
                    throw new IllegalStateException(registrarViolaciones(cve), cve);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                    throw new IllegalStateException();
                }
            } else {
                throw new IllegalArgumentException("El registro no puede ser nulo");
            }

        }

        /**
         * Actualiza un registro existente en la base de datos utilizando el
         * EntityManager.
         *
         * @param nuevo El objeto con los nuevos datos a fusionar o actualizar.
         * No debe ser nulo.
         * @throws IllegalArgumentException si el objeto proporcionado es nulo.
         * @throws IllegalStateException si ocurre un error durante la operación
         * de merge.
         */
        @Override
        public void actualizar(Object nuevo) throws IllegalArgumentException, IllegalStateException {
            if (nuevo != null) {
                try {
                    getEntityManager().merge(nuevo);
                } catch (ConstraintViolationException cve) {
                    throw new IllegalStateException(registrarViolaciones(cve), cve);
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                    throw new IllegalStateException();
            }
        }

        
            else {
            throw new IllegalArgumentException("El objeto a actualizar no puede ser nulo");
        }

    }

    /**
     * Elimina un registro existente de la base de datos utilizando el
     * EntityManager.
     *
     * Si el objeto recibido no está gestionado (por ejemplo, porque proviene de
     * una selección en la vista y su contexto de persistencia ya se cerró), se
     * incorpora primero al contexto con merge, ya que remove solo acepta
     * entidades gestionadas.
     *
     * @param eliminar El objeto que se desea eliminar. No debe ser nulo.
     * @throws IllegalArgumentException si el objeto proporcionado es nulo.
     * @throws IllegalStateException si ocurre un error durante la operación de
     * eliminación.
     */
    @Override
    public void eliminar(Object eliminar) throws IllegalArgumentException, IllegalStateException {
        if (eliminar == null) {
            throw new IllegalArgumentException("El objeto a eliminar no puede ser nulo");
        }
        try {
            EntityManager em = getEntityManager();
            Object managed = em.contains(eliminar) ? eliminar : em.merge(eliminar);
            em.remove(managed);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException("No se pudo eliminar el registro", ex);
        }
    }

    /**
     * Busca y recupera una entidad de la base de datos utilizando su
     * identificador único (UUID).
     *
     * @param uuid El identificador único del registro que se desea buscar. No
     * debe ser nulo.
     * @return El objeto encontrado que corresponde a la entidad, o null si no
     * existe.
     * @throws IllegalArgumentException si el identificador proporcionado es
     * nulo.
     * @throws IllegalStateException si ocurre un error durante la operación de
     * consulta.
     */
    @Override
    public Object buscar(Object uuid) throws IllegalArgumentException, IllegalStateException {
        if (uuid != null) {
            try {
                return getEntityManager().find(entity, uuid);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalArgumentException("Se requiere un UUID válido para realizar la búsqueda");
        }

    }

    /**
     * Obtiene un rango paginado de registros de la entidad utilizando el
     * EntityManager.
     *
     * @param first El índice del primer resultado que se desea obtener (debe
     * ser mayor o igual a 0).
     * @param max El número máximo de resultados que se deben devolver (debe ser
     * @return Una lista con los elementos encontrados dentro del rango
     * especificado.
     * @throws IllegalArgumentException si los parámetros de paginación no son
     * válidos.
     * @throws IllegalStateException si ocurre un error durante la ejecución de
     * la consulta.
     */
    @Override
    public List<T> findRange(int first, int max) throws IllegalArgumentException, IllegalStateException {
        if (first < 0 || max < 0) {
            throw new IllegalArgumentException("first debe ser >= 0 y max debe ser >= 0");
        }
        if (max == 0) {
            return List.of(); // Sin necesidad de ir a la base de datos
        }
        try {
            return getEntityManager().createQuery(
                    "SELECT e FROM " + entity.getSimpleName() + " e ORDER BY e.id" + entity.getSimpleName(),
                    entity)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Cuenta el número total de registros existentes para la entidad en la base
     * de datos.
     *
     * @return La cantidad total de registros de la entidad.
     */
    @Override
    public int contar() {
        return getEntityManager().createQuery(
                "SELECT COUNT(e) FROM " + entity.getSimpleName() + " e",
                Long.class)
                .getSingleResult()
                .intValue();
    }

    /**
     * Registra en el log cada violación de restricción por separado (campo,
     * mensaje y valor recibido), y devuelve un resumen apto para mostrarse al
     * usuario final desde el handler de la vista.
     *
     * @param cve la excepción de validación capturada.
     * @return un resumen de las violaciones, en una sola línea.
     */
    private String registrarViolaciones(ConstraintViolationException cve) {
        StringBuilder resumen = new StringBuilder("Datos inválidos: ");
        for (ConstraintViolation<?> violacion : cve.getConstraintViolations()) {
            String detalle = violacion.getPropertyPath() + " " + violacion.getMessage()
                    + " (valor recibido: " + violacion.getInvalidValue() + ")";
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, detalle);
            resumen.append(violacion.getPropertyPath()).append(" ").append(violacion.getMessage()).append("; ");
        }
        return resumen.toString();
    }

}
