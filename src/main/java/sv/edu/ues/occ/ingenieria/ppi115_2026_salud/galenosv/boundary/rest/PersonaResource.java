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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

/**
 *
 * @author peraza
 */
@Path("persona")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonaResource implements Serializable {

    @Inject
    PersonaService service;

    @Inject
    Validator validator;

    @POST
    public Response create(@Context UriInfo uinfo, Persona persona) {
        if (persona == null) {
            return Response.status(422).header("Wrong-Param", "persona").build();
        }
        persona.setIdPersona(null); // el id lo genera la BD, nunca el cliente
        Response invalida = validar(persona);
        if (invalida != null) {
            return invalida;
        }
        try {
            service.crear(persona);
            URI uri = uinfo.getAbsolutePathBuilder()
                    .path(String.valueOf(persona.getIdPersona()))
                    .build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    private Response validar(Persona persona) {
        Set<ConstraintViolation<Persona>> violaciones = validator.validate(persona);
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
    public Response findById(@PathParam("id") UUID id) {
        if (id == null) {
            return Response.status(422).header("Wrong-Param", "id").build();
        }
        try {
            Persona target = (Persona) service.buscar(id);
            if (target == null) {
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
            @QueryParam("primero") @DefaultValue("0") int first,
            @QueryParam("max") @Max(50) @DefaultValue("50") int max) {
        if (first < 0 || max <= 0) {
            return Response.status(422).header("Wrong-Param", "first,max").build();
        }
        try {
            return Response.ok(service.findRange(first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contar())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") UUID id, Persona persona) {
        if (persona == null) {
            return Response.status(422).header("Wrong-Param", "persona").build();
        }
        try {
            Persona existente = (Persona) service.buscar(id);
            if (existente == null) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            persona.setIdPersona(id); // manda la ruta, no el body
            // fechaCreacion se inicializa con new Date() al deserializar;
            // sin esta línea, cada PUT pisaría la fecha original
            persona.setFechaCreacion(existente.getFechaCreacion());
            Response invalida = validar(persona);
            if (invalida != null) {
                return invalida;
            }
            service.actualizar(persona);
            return Response.noContent().build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        try {
            Persona target = (Persona) service.buscar(id);
            if (target == null) {
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
