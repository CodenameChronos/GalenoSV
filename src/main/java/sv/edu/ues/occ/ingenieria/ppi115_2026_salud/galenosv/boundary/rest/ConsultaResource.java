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
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ConsultaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Consulta;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;

@Path("persona-rol/{idPersonaRol}/consulta")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaResource implements Serializable {

    @Inject
    ConsultaService service;

    @Inject
    PersonaRolService personaRolService;

    @POST
    public Response create(
            @Context UriInfo uriInfo,
            @PathParam("idPersonaRol") UUID idPersonaRol,
            Consulta consulta) {

        if (consulta == null) {
            return Response.status(422)
                    .header("Wrong-Param", "consulta")
                    .build();
        }

        try {
            PersonaRol personaRol
                    = (PersonaRol) personaRolService.buscar(idPersonaRol);

            if (personaRol == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersonaRol)
                        .build();
            }

            // La URL determina cuál es el padre de la consulta.
            consulta.setIdPersonaRol(personaRol);

            service.crear(consulta);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(consulta.getIdConsulta()))
                            .build())
                    .build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("idPersonaRol") UUID idPersonaRol,
            @PathParam("id") UUID idConsulta) {

        if (idConsulta == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }

        try {
            Consulta target = (Consulta) service.buscar(idConsulta);

            if (target == null
                    || target.getIdPersonaRol() == null
                    || !idPersonaRol.equals(
                            target.getIdPersonaRol().getIdPersonaRol())) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsulta)
                        .build();
            }

            return Response.ok(target).build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }

    @GET
    public Response findByRange(
            @PathParam("idPersonaRol") UUID idPersonaRol,
            @QueryParam("primero") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") @Max(50) int max) {

        if (first < 0 || max <= 0 || max > 50) {
            return Response.status(422)
                    .header("Wrong-Param", "primero,max")
                    .build();
        }

        try {
            PersonaRol personaRol
                    = (PersonaRol) personaRolService.buscar(idPersonaRol);

            if (personaRol == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersonaRol)
                        .build();
            }

            return Response.ok(
                    service.findRangeByCriterio(
                            "idPersonaRol", personaRol, first, max))
                    .header("Total-registros",
                            service.contarByCriterio(
                                    "idPersonaRol", personaRol))
                    .build();

        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idPersonaRol") UUID idPersonaRol,
            @PathParam("id") UUID idConsulta) {

        try {
            Consulta target = (Consulta) service.buscar(idConsulta);

            if (target == null
                    || target.getIdPersonaRol() == null
                    || !idPersonaRol.equals(
                            target.getIdPersonaRol().getIdPersonaRol())) {
                return Response.status(404)
                        .header("Entity-Not-Found", idConsulta)
                        .build();
            }

            service.eliminar(target);
            return Response.noContent().build();

        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }
}
