package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ExamenTipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoExamenService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Examen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ExamenTipoExamen;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoExamen;

@Path("examen/{idExamen}/tipoExamen")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExamenTipoExamenResource implements Serializable {

    @Inject
    ExamenTipoExamenService service;
    @Inject
    ExamenService examenService;
    @Inject
    TipoExamenService tipoExamenService;
    @Inject
    Validator validator;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idExamen") UUID idExamen,
            ExamenTipoExamen relacion) {
        if (relacion == null
                || relacion.getIdTipoExamen() == null
                || relacion.getIdTipoExamen().getIdTipoExamen() == null) {
            return Response.status(422).header("Wrong-Param", "relacion").build();
        }
        try {
            Examen examen = (Examen) examenService.buscar(idExamen);
            if (examen == null) {
                return Response.status(404).header("Entity-Not-Found", idExamen).build();
            }
            UUID idTipo = relacion.getIdTipoExamen().getIdTipoExamen();
            TipoExamen tipo = (TipoExamen) tipoExamenService.buscar(idTipo);
            if (tipo == null) {
                return Response.status(404).header("Entity-Not-Found", idTipo).build();
            }
            if (service.existeRelacion(idExamen, idTipo)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("El examen ya tiene asignado ese tipo de examen.")
                        .build();
            }
            relacion.setIdExamenTipoExamen(null);   // el id lo genera la BD
            relacion.setFechaCreacion(new Date());  // la fecha la pone el servidor
            relacion.setIdExamen(examen);           // manda la ruta, no el body
            relacion.setIdTipoExamen(tipo);         // entidad gestionada, no el stub del JSON
            Response invalida = validar(relacion);
            if (invalida != null) {
                return invalida;
            }
            service.crear(relacion);
            URI uri = uinfo.getAbsolutePathBuilder()
                    .path(String.valueOf(relacion.getIdExamenTipoExamen()))
                    .build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    private Response validar(ExamenTipoExamen relacion) {
        Set<ConstraintViolation<ExamenTipoExamen>> violaciones = validator.validate(relacion);
        if (violaciones.isEmpty()) {
            return null;
        }
        List<String> mensajes = violaciones.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return Response.status(422).entity(mensajes).build();
    }

    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idExamen") UUID idExamen,
            @PathParam("id") UUID id) {
        if (id == null) {
            return Response.status(422).header("Wrong-Param", "id").build();
        }
        try {
            ExamenTipoExamen target = (ExamenTipoExamen) service.buscar(id);
            if (target == null || !target.getIdExamen().getIdExamen().equals(idExamen)) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            return Response.ok(target).type(MediaType.APPLICATION_JSON).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @GET
    public Response findByRange(
            @PathParam("idExamen") UUID idExamen,
            @QueryParam("primero") @DefaultValue("0") int first,
            @QueryParam("max") @Max(50) @DefaultValue("50") int max) {
        if (first < 0 || max <= 0) {
            return Response.status(422).header("Wrong-Param", "first,max").build();
        }
        try {
            if (examenService.buscar(idExamen) == null) {
                return Response.status(404).header("Entity-Not-Found", idExamen).build();
            }
            return Response.ok(service.findByIdExamen(idExamen, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contarPorIdExamen(idExamen))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    /**
     * Solo se puede modificar la observación; examen, tipo y fecha se
     * conservan.
     */
    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("idExamen") UUID idExamen,
            @PathParam("id") UUID id,
            ExamenTipoExamen cambios) {
        if (cambios == null) {
            return Response.status(422).header("Wrong-Param", "relacion").build();
        }
        try {
            ExamenTipoExamen existente = (ExamenTipoExamen) service.buscar(id);
            if (existente == null || !existente.getIdExamen().getIdExamen().equals(idExamen)) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            existente.setObservaciones(cambios.getObservaciones());
            Response invalida = validar(existente);
            if (invalida != null) {
                return invalida;
            }
            service.actualizar(existente);
            return Response.noContent().build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idExamen") UUID idExamen,
            @PathParam("id") UUID id) {
        try {
            ExamenTipoExamen target = (ExamenTipoExamen) service.buscar(id);
            if (target == null || !target.getIdExamen().getIdExamen().equals(idExamen)) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            service.eliminar(target);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }
}
