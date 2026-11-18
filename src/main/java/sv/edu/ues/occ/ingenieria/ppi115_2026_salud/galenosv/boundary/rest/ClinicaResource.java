package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;

/**
 *
 * @author kardia
 */
@Path("clinica")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicaResource implements Serializable {

    @Inject
    private ClinicaService service;

    @Inject
    private PersonaRolService personaRolService;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            Clinica clinica
    ) {
        if (clinica == null) {
            return Response.status(422)
                    .header("Wrong-Param", "clinica")
                    .build();
        }

        clinica.setActivo(Boolean.TRUE);

        try {
            service.crear(clinica);

            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(
                    String.valueOf(clinica.getIdClinica())
            ).build();

            return Response.created(uri).build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("id") UUID id,
            Clinica clinica) {
        if (clinica == null) {
            return Response.status(422).header("Wrong-Param", "clinica").build();
        }
        try {
            Clinica existente = (Clinica) service.buscar(id);
            if (existente == null) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }

            clinica.setIdClinica(id);
            if (clinica.getActivo() == null) {
                clinica.setActivo(existente.getActivo());
            }

            service.actualizar(clinica);
            return Response.noContent().build();
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID uuid) {
        if (uuid != null) {
            try {
                Clinica target = (Clinica) service.buscar(uuid);

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
    @Produces({MediaType.APPLICATION_JSON})
    public Response findByRange(
            @QueryParam("first")
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
            return Response.ok(service.findRange(first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contar())
                    .build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{idClinica}/personal")
    public Response findPersonal(
            @PathParam("idClinica") UUID idClinica,
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @Max(50) @DefaultValue("50") int max) {
        if (first < 0 || max <= 0) {
            return Response.status(422).header("Wrong-Param", "first,max").build();
        }
        try {
            if (service.buscar(idClinica) == null) {
                return Response.status(404).header("Entity-Not-Found", idClinica).build();
            }
            return Response.ok(personaRolService.listarPersonalPorClinica(idClinica, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", personaRolService.contarPersonalPorClinica(idClinica))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID uuid) {
        try {
            Clinica target = (Clinica) service.buscar(uuid);

            if (target == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }

            service.eliminar(target);
            return Response.noContent().build();

        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }
}
