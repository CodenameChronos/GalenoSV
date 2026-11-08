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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.RolService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Procedimiento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Rol;

@Path("procedimiento/{idProcedimiento}/paso")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcedimientoPasoResource implements Serializable {

    @Inject
    ProcedimientoPasoService service;

    @Inject
    ProcedimientoService procedimientoService;

    @Inject
    RolService rolService;

    @Inject
    Validator validator;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idProcedimiento") UUID idProcedimiento,
            ProcedimientoPaso paso) {
        if (paso == null) {
            return Response.status(422).header("Wrong-Param", "paso").build();
        }
        try {
            Procedimiento procedimiento = (Procedimiento) procedimientoService.buscar(idProcedimiento);
            if (procedimiento == null) {
                return Response.status(404).header("Entity-Not-Found", idProcedimiento).build();
            }
            // el rol es opcional; si viene, debe existir
            Response rolInvalido = resolverRol(paso);
            if (rolInvalido != null) {
                return rolInvalido;
            }
            paso.setIdProcedimientoPaso(null); // el id lo genera la BD
            paso.setIdProcedimiento(procedimiento); // manda la ruta, no el body
            Response invalida = validar(paso);
            if (invalida != null) {
                return invalida;
            }
            service.crear(paso);
            URI uri = uinfo.getAbsolutePathBuilder()
                    .path(String.valueOf(paso.getIdProcedimientoPaso()))
                    .build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    private Response validar(ProcedimientoPaso paso) {
        Set<ConstraintViolation<ProcedimientoPaso>> violaciones = validator.validate(paso);
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
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @PathParam("id") UUID id) {
        if (id == null) {
            return Response.status(422).header("Wrong-Param", "id").build();
        }
        try {
            ProcedimientoPaso target = (ProcedimientoPaso) service.buscar(id);
            if (target == null
                    || !target.getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
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
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @QueryParam("primero") @DefaultValue("0") int first,
            @QueryParam("max") @Max(50) @DefaultValue("50") int max) {
        if (first < 0 || max <= 0) {
            return Response.status(422).header("Wrong-Param", "first,max").build();
        }
        try {
            if (procedimientoService.buscar(idProcedimiento) == null) {
                return Response.status(404).header("Entity-Not-Found", idProcedimiento).build();
            }
            String propiedad = "idProcedimiento.idProcedimiento";
            return Response.ok(service.findRangeByCriterio(propiedad, idProcedimiento, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contarByCriterio(propiedad, idProcedimiento))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    // Devuelve null si el rol es válido (o no viene); si no, la respuesta de error
    private Response resolverRol(ProcedimientoPaso paso) {
        if (paso.getIdRol() == null) {
            return null; // el rol es opcional
        }
        UUID idRol = paso.getIdRol().getIdRol();
        if (idRol == null) {
            return Response.status(422).header("Wrong-Param", "idRol").build();
        }
        Rol rol = (Rol) rolService.buscar(idRol);
        if (rol == null) {
            return Response.status(404).header("Entity-Not-Found", idRol).build();
        }
        paso.setIdRol(rol);
        return null;
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @PathParam("id") UUID id,
            ProcedimientoPaso paso) {
        if (paso == null) {
            return Response.status(422).header("Wrong-Param", "paso").build();
        }
        try {
            ProcedimientoPaso existente = (ProcedimientoPaso) service.buscar(id);
            if (existente == null
                    || !existente.getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            Response rolInvalido = resolverRol(paso);
            if (rolInvalido != null) {
                return rolInvalido;
            }
            paso.setIdProcedimientoPaso(id); // manda la ruta, no el body
            paso.setIdProcedimiento(existente.getIdProcedimiento()); // el paso no cambia de procedimiento
            Response invalida = validar(paso);
            if (invalida != null) {
                return invalida;
            }
            service.actualizar(paso);
            return Response.noContent().build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @PathParam("id") UUID id) {
        try {
            ProcedimientoPaso target = (ProcedimientoPaso) service.buscar(id);
            if (target == null
                    || !target.getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
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
