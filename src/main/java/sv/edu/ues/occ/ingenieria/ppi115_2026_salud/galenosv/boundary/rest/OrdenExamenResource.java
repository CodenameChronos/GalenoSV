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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.OrdenExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.OrdenExamen;

/**
 * Recurso REST para las órdenes de examen emitidas durante un paso de un
 * procedimiento de consulta.
 *
 * Una orden de examen solo referencia al paso de consulta que la origina (no
 * referencia directamente a un examen), por lo que el único dato de relación es
 * el padre, que se toma de la ruta. El cuerpo de la petición únicamente aporta
 * los datos propios de la orden (por ejemplo, {@code indicaciones}).
 * 
 * @author kardia
 */
@Path("consultaProcedimientoPaso/{idConsultaProcedimientoPaso}/ordenExamen")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrdenExamenResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    OrdenExamenService oeService;

    @Inject
    ConsultaProcedimientoPasoService cppService;

    /**
     * Emite una orden de examen asociada al paso de consulta indicado en la
     * ruta.
     *
     * @param uinfo Información de la URI de la petición, usada para construir
     * la cabecera Location.
     * @param idConsultaProcedimientoPaso Identificador del paso de consulta
     * padre (ruta).
     * @param oe Cuerpo de la petición con los datos propios de la orden.
     * @return 201 con Location del nuevo registro; 404 si el paso de
     * consulta no existe; 409 si una regla de negocio lo impide; 422 si el
     * cuerpo es nulo; 500 ante un error inesperado.
     */
    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idConsultaProcedimientoPaso") UUID idConsultaProcedimientoPaso,
            OrdenExamen oe
    ) {
        if (oe == null) {
            return Response.status(422)
                    .header("Wrong-Param", "oe")
                    .build();
        }
        try {
            ConsultaProcedimientoPaso cpp = (ConsultaProcedimientoPaso) cppService
                    .buscar(idConsultaProcedimientoPaso);
            if (cpp == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsultaProcedimientoPaso)
                        .build();
            }
            // El identificador lo genera JPA: si el cliente envía uno, persist()
            // fallaría por tratarlo como entidad desconectada.
            oe.setIdOrdenExamen(null);
            oe.setIdConsultaProcedimientoPaso(cpp);
            oeService.crear(oe);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(oe.getIdOrdenExamen())).build();
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
     * Obtiene una orden de examen por su identificador, verificando que
     * pertenezca al paso de consulta de la ruta.
     *
     * @param idConsultaProcedimientoPaso Identificador del paso de consulta
     * padre (ruta).
     * @param uuid Identificador de la orden de examen.
     * @return 200 con el registro; 404 si no existe o pertenece a otro paso de
     * consulta; 422 si el identificador es nulo; 500 ante un error inesperado.
     */
    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idConsultaProcedimientoPaso") UUID idConsultaProcedimientoPaso,
            @PathParam("id") UUID uuid) {
        if (uuid == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }
        try {
            OrdenExamen target = (OrdenExamen) oeService.buscar(uuid);
            if (target == null || !perteneceAPaso(target, idConsultaProcedimientoPaso)) {
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
     * Lista, de forma paginada, las órdenes de examen del paso de consulta
     * indicado. El total de órdenes de ESE paso se devuelve en la cabecera
     * {@code Total-registros}.
     *
     * @param idConsultaProcedimientoPaso Identificador del paso de consulta
     * padre (ruta).
     * @param first Posición del primer registro (por defecto 0).
     * @param max Cantidad máxima de registros (por defecto y máximo 50).
     * @return 200 con la página solicitada; 404 si el paso de consulta no
     * existe; 422 si los parámetros de rango son inválidos; 500 ante un error
     * inesperado.
     */
    @GET
    public Response findByRange(
            @PathParam("idConsultaProcedimientoPaso") UUID idConsultaProcedimientoPaso,
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
            if (cppService.buscar(idConsultaProcedimientoPaso) == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsultaProcedimientoPaso)
                        .build();
            }
            return Response.ok(oeService.findRangeByConsultaProcedimientoPaso(
                    idConsultaProcedimientoPaso, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros",
                            oeService.contarByConsultaProcedimientoPaso(idConsultaProcedimientoPaso))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Elimina una orden de examen, verificando que pertenezca al paso de
     * consulta de la ruta.
     *
     * @param idConsultaProcedimientoPaso Identificador del paso de consulta
     * padre (ruta).
     * @param uuid Identificador de la orden de examen a eliminar.
     * @return 204 si se eliminó; 404 si no existe o pertenece a otro paso de
     * consulta; 409 si tiene registros hijos (resultados) que la referencian;
     * 500 ante un error inesperado.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idConsultaProcedimientoPaso") UUID idConsultaProcedimientoPaso,
            @PathParam("id") UUID uuid
    ) {
        try {
            // El service requiere la instancia completa (usa remove de JPA),
            // por eso se busca primero la tupla a partir del identificador.
            OrdenExamen target = (OrdenExamen) oeService.buscar(uuid);
            if (target == null || !perteneceAPaso(target, idConsultaProcedimientoPaso)) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }
            oeService.eliminar(target);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            // La tupla tiene registros hijos (resultados) que la referencian.
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
     * Verifica, de forma segura ante nulos, que la orden pertenezca al paso de
     * consulta indicado.
     *
     * @param target Registro a verificar.
     * @param idConsultaProcedimientoPaso Identificador esperado del padre.
     * @return true si la orden pertenece a ese paso de consulta.
     */
    private boolean perteneceAPaso(OrdenExamen target, UUID idConsultaProcedimientoPaso) {
        return target.getIdConsultaProcedimientoPaso() != null
                && idConsultaProcedimientoPaso != null
                && idConsultaProcedimientoPaso.equals(
                        target.getIdConsultaProcedimientoPaso().getIdConsultaProcedimientoPaso());
    }
}