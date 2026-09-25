package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;

/**
 * Converter genérico entre una entidad JPA y su identificador UUID.
 *
 * Sirve para cualquier componente Faces que reciba una entidad como valor
 * (p:autoComplete, p:selectOneMenu, etc.), siempre que la llave primaria de la
 * entidad sea un {@link UUID}. La clase de la entidad se deduce del tipo de la
 * propiedad enlazada en el atributo value del componente, por lo que no se
 * requiere un converter por entidad.
 */
@FacesConverter(value = "entityConverter", managed = true)
public class EntityConverter implements Converter<Object> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Convierte el texto recibido del navegador (un UUID) en la entidad
     * correspondiente.
     *
     * @param ctx contexto de Faces.
     * @param componente componente que envía el valor.
     * @param valor UUID en texto; nulo o vacío se interpreta como sin selección.
     * @return la entidad encontrada, o null si no hay selección o no existe.
     * @throws ConverterException si el texto no es un UUID válido o el
     * componente no tiene un value enlazado.
     */
    @Override
    public Object getAsObject(FacesContext ctx, UIComponent componente, String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        if (componente.getValueExpression("value") == null) {
            throw new ConverterException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "El componente no tiene un valor enlazado", null));
        }
        try {
            Class<?> tipo = componente.getValueExpression("value").getType(ctx.getELContext());
            return em.find(tipo, UUID.fromString(valor));
        } catch (IllegalArgumentException ex) {
            throw new ConverterException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Valor no válido", valor), ex);
        }
    }

    /**
     * Convierte la entidad en el texto (UUID) que se envía al navegador.
     *
     * @param ctx contexto de Faces.
     * @param componente componente que recibe el valor.
     * @param entidad entidad a convertir; nula devuelve cadena vacía.
     * @return el identificador de la entidad en texto, o cadena vacía.
     */
    @Override
    public String getAsString(FacesContext ctx, UIComponent componente, Object entidad) {
        if (entidad == null) {
            return "";
        }
        Object id = em.getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(entidad);
        return id == null ? "" : id.toString();
    }
}