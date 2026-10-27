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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ConsultaProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;

/**
 * Recurso REST para los pasos ejecutados dentro de un procedimiento de
 * consulta.
 * 
 * El padre es ConsultaProcedimiento, que se identifica en la
 * ruta. El paso del procedimiento (idProcedimientoPaso) viaja en el
 * cuerpo como referencia anidada, y opcionalmente también la persona-rol que lo
 * ejecuta (idPersonaRol). No se incluye la consulta en la ruta para no
 * alargarla: se alcanza desde el propio procedimiento de consulta.
 *
 * @author kardia
 */
@Path("consultaProcedimiento/{idConsultaProcedimiento}/paso")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaProcedimientoPasoResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    ConsultaProcedimientoPasoService consppService;

    @Inject
    ConsultaProcedimientoService conspService;

    @Inject
    ProcedimientoPasoService ppService;

    @Inject
    PersonaRolService personaRolService;

    /**
     * Registra un paso dentro del procedimiento de consulta indicado en la
     * ruta. Valida que el paso pertenezca al mismo procedimiento que se está
     * aplicando en la consulta, para evitar incoherencias entre ambos.
     *
     * @param uinfo Información de la URI de la petición, usada para construir
     * la cabecera Location.
     * @param idConsultaProcedimiento Identificador del procedimiento de
     * consulta padre (ruta).
     * @param cpp Cuerpo de la petición; debe incluir la referencia al paso del
     * procedimiento.
     * @return 201 con Location del nuevo registro; 404 si el
     * procedimiento de consulta, el paso o la persona-rol no existen; 409 si el
     * paso no corresponde al procedimiento o una regla de negocio lo impide;
     * 422 si el cuerpo es inválido; 500 ante un error inesperado.
     */
    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idConsultaProcedimiento") UUID idConsultaProcedimiento,
            ConsultaProcedimientoPaso conspp
    ) {
        if (conspp == null
                || conspp.getIdProcedimientoPaso() == null
                || conspp.getIdProcedimientoPaso().getIdProcedimientoPaso() == null) {
            return Response.status(422)
                    .header("Wrong-Param", "conspp")
                    .build();
        }
        try {
            ConsultaProcedimiento consp = (ConsultaProcedimiento) conspService
                    .buscar(idConsultaProcedimiento);
            if (consp == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsultaProcedimiento)
                        .build();
            }
            // El primer getIdProcedimientoPaso es la referencia a la entidad y el
            // segundo su identificador (ver TODO de ProcedimientoPasoExamenResource).
            ProcedimientoPaso paso = (ProcedimientoPaso) ppService
                    .buscar(conspp.getIdProcedimientoPaso().getIdProcedimientoPaso());
            if (paso == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", "idProcedimientoPaso")
                        .build();
            }
            if (!pasoPerteneceAlProcedimiento(consp, paso)) {
                return Response.status(Response.Status.CONFLICT)
                        .type(MediaType.TEXT_PLAIN)
                        .entity("El paso no pertenece al procedimiento aplicado en la consulta")
                        .build();
            }
            // La persona-rol es opcional (la columna admite nulo).
            if (conspp.getIdPersonaRol() != null) {
                if (conspp.getIdPersonaRol().getIdPersonaRol() == null) {
                    return Response.status(422)
                            .header("Wrong-Param", "idPersonaRol")
                            .build();
                }
                PersonaRol personaRol = (PersonaRol) personaRolService
                        .buscar(conspp.getIdPersonaRol().getIdPersonaRol());
                if (personaRol == null) {
                    return Response.status(404)
                            .header("Entity-Not-Found", "idPersonaRol")
                            .build();
                }
                conspp.setIdPersonaRol(personaRol);
            }
            // El identificador lo genera JPA: si el cliente envía uno, persist()
            // fallaría por tratarlo como entidad desconectada.
            conspp.setIdConsultaProcedimientoPaso(null);
            conspp.setIdConsultaProcedimiento(consp);
            conspp.setIdProcedimientoPaso(paso);
            consppService.crear(conspp);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(conspp.getIdConsultaProcedimientoPaso())).build();
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
     * Obtiene un paso de consulta por su identificador, verificando que
     * pertenezca al procedimiento de consulta de la ruta.
     *
     * @param idConsultaProcedimiento Identificador del procedimiento de
     * consulta padre (ruta).
     * @param uuid Identificador del paso de consulta.
     * @return 200 con el registro; 404 si no existe o pertenece a otro
     * procedimiento de consulta; 422 si el identificador es nulo; 500 ante un
     * error inesperado.
     */
    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idConsultaProcedimiento") UUID idConsultaProcedimiento,
            @PathParam("id") UUID uuid) {
        if (uuid == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }
        try {
            ConsultaProcedimientoPaso target = (ConsultaProcedimientoPaso) consppService.buscar(uuid);
            if (target == null || !perteneceAProcedimientoConsulta(target, idConsultaProcedimiento)) {
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
     * Lista, de forma paginada, los pasos del procedimiento de consulta
     * indicado. El total de pasos de ESE procedimiento se devuelve en la
     * cabecera Total-registros.
     *
     * @param idConsultaProcedimiento Identificador del procedimiento de
     * consulta padre (ruta).
     * @param first Posición del primer registro (por defecto 0).
     * @param max Cantidad máxima de registros (por defecto y máximo 50).
     * @return 200 con la página solicitada; 404 si el procedimiento de consulta
     * no existe; 422 si los parámetros de rango son inválidos; 500 ante un
     * error inesperado.
     */
    @GET
    public Response findByRange(
            @PathParam("idConsultaProcedimiento") UUID idConsultaProcedimiento,
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
            if (conspService.buscar(idConsultaProcedimiento) == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsultaProcedimiento)
                        .build();
            }
            return Response.ok(consppService.listarPorConsultaProcedimiento(idConsultaProcedimiento, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", consppService.contarPorConsultaProcedimiento(idConsultaProcedimiento))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    /**
     * Elimina un paso de consulta, verificando que pertenezca al procedimiento
     * de consulta de la ruta.
     *
     * @param idConsultaProcedimiento Identificador del procedimiento de
     * consulta padre (ruta).
     * @param uuid Identificador del paso de consulta a eliminar.
     * @return 204 si se eliminó; 404 si no existe o pertenece a otro
     * procedimiento de consulta; 409 si tiene registros hijos (órdenes de
     * examen) que lo referencian; 500 ante un error inesperado.
     */
    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idConsultaProcedimiento") UUID idConsultaProcedimiento,
            @PathParam("id") UUID uuid
    ) {
        try {
            // El service requiere la instancia completa (usa remove de JPA),
            // por eso se busca primero la tupla a partir del identificador.
            ConsultaProcedimientoPaso target = (ConsultaProcedimientoPaso) consppService.buscar(uuid);
            if (target == null || !perteneceAProcedimientoConsulta(target, idConsultaProcedimiento)) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }
            consppService.eliminar(target);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            // La tupla tiene registros hijos (órdenes de examen) que la referencian.
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
     * Verifica, de forma segura ante nulos, que el paso de consulta pertenezca
     * al procedimiento de consulta indicado.
     *
     * @param target Registro a verificar.
     * @param idConsultaProcedimiento Identificador esperado del padre.
     * @return true si el registro pertenece a ese procedimiento de
     * consulta.
     */
    private boolean perteneceAProcedimientoConsulta(ConsultaProcedimientoPaso target, UUID idConsultaProcedimiento) {
        return target.getIdConsultaProcedimiento() != null
                && idConsultaProcedimiento != null
                && idConsultaProcedimiento.equals(
                        target.getIdConsultaProcedimiento().getIdConsultaProcedimiento());
    }

    /**
     * Verifica que el paso del procedimiento corresponda al mismo
     * procedimiento que se está aplicando en la consulta.
     *
     * @param consp Procedimiento de consulta (padre).
     * @param paso Paso del procedimiento que se desea registrar.
     * @return true si ambos referencian el mismo procedimiento.
     */
    private boolean pasoPerteneceAlProcedimiento(ConsultaProcedimiento consp, ProcedimientoPaso paso) {
        return consp.getIdProcedimiento() != null
                && paso.getIdProcedimiento() != null
                && consp.getIdProcedimiento().getIdProcedimiento() != null
                && consp.getIdProcedimiento().getIdProcedimiento()
                        .equals(paso.getIdProcedimiento().getIdProcedimiento());
    }
}