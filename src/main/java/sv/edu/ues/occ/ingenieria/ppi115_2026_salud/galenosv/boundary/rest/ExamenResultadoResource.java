package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenResultadoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenResultado;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

/**
 * Recurso REST para los resultados de una orden de examen.
 *
 * El padre natural es OrdenExamen, que se identifica en la ruta. El
 * cuerpo de la petición solo aporta los datos propios del resultado
 * (resultado, interpretacion, rutaAtestado).
 *
 * @author kardia
 */
@Path("ordenExamen/{idOrdenExamen}/resultado")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExamenResultadoResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ExamenResultadoService erService;

    @Inject
    OrdenExamenService oeService;

    /**
     * Registra un resultado para la orden de examen indicada en la ruta.
     *
     * @param uinfo Información de la URI de la petición, usada para construir
     * la cabecera Location.
     * @param idOrdenExamen Identificador de la orden de examen padre (ruta).
     * @param er Cuerpo de la petición con los datos propios del resultado.
     * @return 201 con Location del nuevo registro; 404 si la orden de
     * examen no existe; 409 si una regla de negocio lo impide; 422 si el cuerpo
     * es nulo; 500 ante un error inesperado.
     */
    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idOrdenExamen") UUID idOrdenExamen,
            ExamenResultado er
    ) {
        if (er == null) {
            return Response.status(422)
                    .header("Wrong-Param", "er")
                    .build();
        }
        try {
            OrdenExamen orden = (OrdenExamen) oeService.buscar(idOrdenExamen);
            if (orden == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idOrdenExamen)
                        .build();
            }
            // El identificador lo genera JPA: si el cliente envía uno, persist()
            // fallaría por tratarlo como entidad desconectada.
            er.setIdExamenResultado(null);
            er.setIdOrdenExamen(orden);
            erService.crear(er);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(er.getIdExamenResultado())).build();
            return Response.created(uri).build();
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Obtiene un resultado por su identificador, verificando que pertenezca a
     * la orden de examen de la ruta.
     *
     * @param idOrdenExamen Identificador de la orden de examen padre (ruta).
     * @param uuid Identificador del resultado.
     * @return 200 con el registro; 404 si no existe o pertenece a otra orden;
     * 422 si el identificador es nulo; 500 ante un error inesperado.
     */
    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idOrdenExamen") UUID idOrdenExamen,
            @PathParam("id") UUID uuid) {
        if (uuid == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }
        try {
            ExamenResultado target = (ExamenResultado) erService.buscar(uuid);
            if (target == null || !perteneceAOrden(target, idOrdenExamen)) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }
            return Response.ok(target)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Lista, de forma paginada, los resultados de la orden de examen indicada.
     * El total de resultados de ESA orden se devuelve en la cabecera
     * Total-registros.
     *
     * @param idOrdenExamen Identificador de la orden de examen padre (ruta).
     * @param first Posición del primer registro (por defecto 0).
     * @param max Cantidad máxima de registros (por defecto y máximo 50).
     * @return 200 con la página solicitada; 404 si la orden de examen no
     * existe; 422 si los parámetros de rango son inválidos; 500 ante un error
     * inesperado.
     */
    @GET
    public Response findByRange(
            @PathParam("idOrdenExamen") UUID idOrdenExamen,
            @QueryParam("primero")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @Max(50)
            @DefaultValue("50") int max
    ) {
        if (first < 0 || max <= 0) {
            return Response.status(422)
                    .header("Wrong-Param", "first,max")
                    .build();
        }
        try {
            if (oeService.buscar(idOrdenExamen) == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idOrdenExamen)
                        .build();
            }
            return Response.ok(erService.findRangeByOrdenExamen(idOrdenExamen, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", erService.contarByOrdenExamen(idOrdenExamen))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Elimina un resultado, verificando que pertenezca a la orden de examen de
     * la ruta.
     *
     * @param idOrdenExamen Identificador de la orden de examen padre (ruta).
     * @param uuid Identificador del resultado a eliminar.
     * @return 204 si se eliminó; 404 si no existe o pertenece a otra orden; 409
     * si una regla de negocio impide la eliminación; 500 ante un error
     * inesperado.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idOrdenExamen") UUID idOrdenExamen,
            @PathParam("id") UUID uuid
    ) {
        try {
            // El service requiere la instancia completa (usa remove de JPA),
            // por eso se busca primero la tupla a partir del identificador.
            ExamenResultado target = (ExamenResultado) erService.buscar(uuid);
            if (target == null || !perteneceAOrden(target, idOrdenExamen)) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }
            erService.eliminar(target);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Verifica, de forma segura ante nulos, que el resultado pertenezca a la
     * orden de examen indicada.
     *
     * @param target Registro a verificar.
     * @param idOrdenExamen Identificador esperado de la orden de examen.
     * @return true si el resultado pertenece a esa orden.
     */
    private boolean perteneceAOrden(ExamenResultado target, UUID idOrdenExamen) {
        return target.getIdOrdenExamen() != null
                && idOrdenExamen != null
                && idOrdenExamen.equals(target.getIdOrdenExamen().getIdOrdenExamen());
    }
}