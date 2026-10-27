
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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ClinicaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaRolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Clinica;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.PersonaRol;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Path("persona/{idPersona}/persona-rol")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonaRolResource implements Serializable {

    @Inject
    PersonaRolService service;

    @Inject
    PersonaService personaService;

    @Inject
    RolService rolService;

    @Inject
    ClinicaService clinicaService;

    @POST
    public Response create(
            @Context UriInfo uriInfo,
            @PathParam("idPersona") UUID idPersona,
            PersonaRol personaRol) {

        if (personaRol == null
                || personaRol.getIdRol() == null
                || personaRol.getIdRol().getIdRol() == null
                || personaRol.getIdClinica() == null
                || personaRol.getIdClinica().getIdClinica() == null) {
            return Response.status(422)
                    .header("Wrong-Param", "personaRol")
                    .build();
        }

        try {
            Persona persona = (Persona) personaService.buscar(idPersona);
            if (persona == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersona)
                        .build();
            }

            UUID idRol = personaRol.getIdRol().getIdRol();
            Rol rol = (Rol) rolService.buscar(idRol);

            if (rol == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idRol)
                        .build();
            }

            UUID idClinica = personaRol.getIdClinica().getIdClinica();
            Clinica clinica =
                    (Clinica) clinicaService.buscar(idClinica);

            if (clinica == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idClinica)
                        .build();
            }

            personaRol.setIdPersona(persona);
            personaRol.setIdRol(rol);
            personaRol.setIdClinica(clinica);

            service.crear(personaRol);

            return Response.created(
                    uriInfo.getAbsolutePathBuilder()
                            .path(String.valueOf(
                                    personaRol.getIdPersonaRol()))
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
            @PathParam("idPersona") UUID idPersona,
            @PathParam("id") UUID idPersonaRol) {

        if (idPersonaRol == null) {
            return Response.status(422)
                    .header("Wrong-Param", "id")
                    .build();
        }

        try {
            PersonaRol target =
                    (PersonaRol) service.buscar(idPersonaRol);

            if (target == null
                    || target.getIdPersona() == null
                    || !idPersona.equals(
                            target.getIdPersona().getIdPersona())) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersonaRol)
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
            @PathParam("idPersona") UUID idPersona,
            @QueryParam("primero") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") @Max(50) int max) {

        if (first < 0 || max <= 0 || max > 50) {
            return Response.status(422)
                    .header("Wrong-Param", "primero,max")
                    .build();
        }

        try {
            Persona persona = (Persona) personaService.buscar(idPersona);

            if (persona == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersona)
                        .build();
            }

            return Response.ok(
                    service.findRangeByCriterio(
                            "idPersona", persona, first, max))
                    .header("Total-registros",
                            service.contarByCriterio("idPersona", persona))
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
            @PathParam("idPersona") UUID idPersona,
            @PathParam("id") UUID idPersonaRol) {

        try {
            PersonaRol target =
                    (PersonaRol) service.buscar(idPersonaRol);

            if (target == null
                    || target.getIdPersona() == null
                    || !idPersona.equals(
                            target.getIdPersona().getIdPersona())) {
                return Response.status(404)
                        .header("Entity-Not-Found", idPersonaRol)
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
            return Response.status(500)
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }
}
