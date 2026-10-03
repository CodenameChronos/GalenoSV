package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control;

import jakarta.ejb.ApplicationException;

/**
 * Violación de una regla de negocio (por ejemplo: "ya existe un paso inicial").
 *
 * Al estar anotada con @ApplicationException, el contenedor EJB NO la envuelve
 * en una EJBException: llega tal cual a quien llamó al servicio, y el mensaje
 * se puede mostrar directamente al usuario. rollback = true revierte la
 * transacción en curso al lanzarla.
 */
@ApplicationException(rollback = true)
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }

}