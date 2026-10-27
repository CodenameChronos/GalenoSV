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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;

/**
 * Recurso REST para los procedimientos aplicados durante una consulta.
 *
 * El padre de este recurso es Consulta, por lo que la consulta
 * se identifica en la ruta y no en el cuerpo de la petición. El procedimiento
 * aplicado sí viaja en el cuerpo, como referencia anidada 
 * @author kardia
 */

@Path("consulta/{idConsulta}/procedimiento")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaProcedimientoResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ConsultaProcedimientoService conspService;

    @Inject
    ConsultaService consultaService;

    @Inject
    ProcedimientoService procedimientoService;

    /**
     * Asocia un procedimiento a la consulta indicada en la ruta.
     *
     * @param uinfo Información de la URI de la petición, usada para construir
     * la cabecera Location.
     * @param idConsulta Identificador de la consulta padre (ruta).
     * @param consp Cuerpo de la petición; debe incluir la referencia al
     * procedimiento.
     * @return 201 con Location del nuevo registro; 404 si la consulta
     * o el procedimiento no existen; 409 si una regla de negocio lo impide;
     * 422 si el cuerpo es inválido; 500 ante un error inesperado.
     */
    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idConsulta") UUID idConsulta,
            ConsultaProcedimiento consp
    ) {
        if (consp == null
                || consp.getIdProcedimiento() == null
                || consp.getIdProcedimiento().getIdProcedimiento() == null) {
            return Response.status(422)
                    .header("Wrong-Param", "consp")
                    .build();
        }
        try {
            Consulta consulta = (Consulta) consultaService.buscar(idConsulta);
            if (consulta == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsulta)
                        .build();
            }
            // El primer getIdProcedimiento es la referencia a la entidad y el
            // segundo su identificador (ver TODO de ProcedimientoPasoExamenResource).
            Procedimiento procedimiento = (Procedimiento) procedimientoService
                    .buscar(consp.getIdProcedimiento().getIdProcedimiento());
            if (procedimiento == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", "idProcedimiento")
                        .build();
            }
            // El identificador lo genera JPA: si el cliente envía uno, persist()
            // fallaría por tratarlo como entidad desconectada.
            consp.setIdConsultaProcedimiento(null);
            consp.setIdConsulta(consulta);
            consp.setIdProcedimiento(procedimiento);
            conspService.crear(consp);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(consp.getIdConsultaProcedimiento())).build();
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
     * Obtiene un procedimiento de consulta por su identificador, verificando
     * que pertenezca a la consulta de la ruta.
     *
     * @param idConsulta Identificador de la consulta padre (ruta).
     * @param uuid Identificador del procedimiento de consulta.
     * @return 200 con el registro; 404 si no existe o pertenece a otra
     * consulta; 422 si el identificador es nulo; 500 ante un error inesperado.
     */
    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idConsulta") UUID idConsulta,
            @PathParam("id") UUID uuid) {
        if (uuid == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }
        try {
            ConsultaProcedimiento target = (ConsultaProcedimiento) conspService.buscar(uuid);
            if (target == null || !perteneceAConsulta(target, idConsulta)) {
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
     * Lista, de forma paginada, los procedimientos de la consulta indicada.
     * El total de registros de ESA consulta se devuelve en la cabecera
     * Total-registros.
     *
     * @param idConsulta Identificador de la consulta padre (ruta).
     * @param first Posición del primer registro (por defecto 0).
     * @param max Cantidad máxima de registros (por defecto y máximo 50).
     * @return 200 con la página solicitada; 404 si la consulta no existe; 422
     * si los parámetros de rango son inválidos; 500 ante un error inesperado.
     */
    @GET
    public Response findByRange(
            @PathParam("idConsulta") UUID idConsulta,
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
            if (consultaService.buscar(idConsulta) == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsulta)
                        .build();
            }
            return Response.ok(conspService.listarPorConsulta(idConsulta, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", conspService.contarPorConsulta(idConsulta))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Elimina un procedimiento de consulta, verificando que pertenezca a la
     * consulta de la ruta.
     *
     * @param idConsulta Identificador de la consulta padre (ruta).
     * @param uuid Identificador del procedimiento de consulta a eliminar.
     * @return 204 si se eliminó; 404 si no existe o pertenece a otra consulta;
     * 409 si tiene registros hijos que lo referencian; 500 ante un error
     * inesperado.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idConsulta") UUID idConsulta,
            @PathParam("id") UUID uuid
    ) {
        try {
            // El service requiere la instancia completa (usa remove de JPA),
            // por eso se busca primero la tupla a partir del identificador.
            ConsultaProcedimiento target = (ConsultaProcedimiento) conspService.buscar(uuid);
            if (target == null || !perteneceAConsulta(target, idConsulta)) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }
            conspService.eliminar(target);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            // La tupla tiene registros hijos (pasos) que la referencian.
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
     * Verifica, de forma segura ante nulos, que el registro pertenezca a la
     * consulta indicada.
     *
     * @param target Registro a verificar.
     * @param idConsulta Identificador de la consulta esperada.
     * @return true si el registro pertenece a esa consulta.
     */
    private boolean perteneceAConsulta(ConsultaProcedimiento target, UUID idConsulta) {
        return target.getIdConsulta() != null
                && idConsulta != null
                && idConsulta.equals(target.getIdConsulta().getIdConsulta());
    }
}