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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoExamen;

/**
 *
 * @author kardia
 */
// Lo normal seria acceder a este tipo de datos desde
// el modulo de Examen (como en la interfaz PrimeFaces),
// por ello el padre es examen. No se guarda referencia en este path
// a ProcedimientoPaso para no alargar demasiado la URL
// y evitar posibles incoherencias, mejor referenciar
// en el Path de los metodos
@Path("examen/{idExamen}/paso")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProcedimientoPasoExamenResource implements Serializable {

    @Inject
    ProcedimientoPasoExamenService ppeService;

    @Inject
    ProcedimientoPasoService ppService;

    @Inject
    ExamenService exService;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idExamen") UUID idExamen,
            ProcedimientoPasoExamen ppe
    ) {
        if (ppe == null
                || ppe.getIdProcedimientoPaso() == null
                || ppe.getIdProcedimientoPaso().getIdProcedimientoPaso() == null) {
            return Response.status(422)
                    .header("Wrong-Param", "ppe")
                    .build();
        }
        try {
            Examen examen = (Examen) exService.buscar(idExamen);
            if (examen == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idExamen)
                        .build();
            }
            // Acceso engorroso y que pareciera redundante (aunque no lo es)
            // para navegar al identificador de una entidad ajena
            // (El primer getIdProcedimientoPaso es la referencia hacia la entidad nada mas)
            //
            // TODO: refactorizar el mapeo de la navegacion en las entidades
            ProcedimientoPaso paso = (ProcedimientoPaso) ppService
                    .buscar(ppe.getIdProcedimientoPaso().getIdProcedimientoPaso());
            if (paso == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", "idPaso")
                        .build();
            }
            ppe.setIdExamen(examen);
            ppe.setIdProcedimientoPaso(paso);
            ppeService.crear(ppe);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(ppe.getIdProcedimientoPasoExamen())).build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{id}")
    public Response findById(
            // Asigna el valor del parametro idExamen definido
            // en el Path propio de la clase
            @PathParam("idExamen") UUID idExamen,
            @PathParam("id") UUID uuid) {

        if (uuid != null) {
            try {
                ProcedimientoPasoExamen target = (ProcedimientoPasoExamen) ppeService
                        .buscar(uuid);
                if (target == null || !target.getIdExamen().getIdExamen().equals(idExamen)) {
                    return Response.status(404).header("Entity-Not-Found", uuid).build();
                }
                if (target != null) {
                    return Response.ok(target)
                            .type(MediaType.APPLICATION_JSON)
                            .build();
                } else {
                    return Response.status(404)
                            .header("Entity-Not-Found", uuid)
                            .build();
                }
            } catch (Exception ex) {
                return Response.status(500)
                        .header("Application-Error", ex.getMessage())
                        .build();
            }
        } else {
            return Response.status(422)
                    .header("Wrong-Param", uuid)
                    .build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByRange(
            // Asigna el valor del parametro idExamen definido
            // en el Path propio de la clase
            @PathParam("idExamen") UUID idExamen,
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
            return Response.ok(ppeService.findRange(first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", ppeService.contar())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idExamen") UUID idExamen,
            @PathParam("id") UUID idPasoExamen
    ) {
        try {
            // Busca la tupla entera a partir del id proporcionado
            // ya que el service utiliza el flush de jakarta quee
            // requiere una instancia entera a eliminar
            ProcedimientoPasoExamen target = (ProcedimientoPasoExamen) ppeService
                    .buscar(idPasoExamen);

            // Verifica que la busqueda sea exitosa y que ademas
            // pertenezca al mismo examen que la ruta API esta
            // referenciando
            if (target == null || !target.getIdExamen().getIdExamen().equals(idExamen)) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPasoExamen)
                        .build();
            }

            ppeService.eliminar(target);
            return Response.noContent().build();

            // Excepcion de tipo logica de negocion (creada por Kevin)
            // para imposibilitar eliminar tuplas que ya poseen
            // registros hijos referenciandole
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }

}
