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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoSecuenciaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoPasoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ProcedimientoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPaso;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.ProcedimientoPasoSecuencia;

@Path("procedimiento/{idProcedimiento}/secuencia")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProcedimientoPasoSecuenciaResource implements Serializable {

    @Inject
    ProcedimientoPasoSecuenciaService service;

    @Inject
    ProcedimientoPasoService pasoService;

    @Inject
    ProcedimientoService procedimientoService;

    @Inject
    Validator validator;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            @PathParam("idProcedimiento") UUID idProcedimiento,
            ProcedimientoPasoSecuencia secuencia) {
        if (secuencia == null
                || secuencia.getIdProcedimientoPasoReferencia() == null
                || secuencia.getIdProcedimientoPaso() == null
                || secuencia.getIdProcedimientoPaso().getIdProcedimientoPaso() == null) {
            return Response.status(422).header("Wrong-Param", "secuencia").build();
        }
        try {
            if (procedimientoService.buscar(idProcedimiento) == null) {
                return Response.status(404).header("Entity-Not-Found", idProcedimiento).build();
            }
            UUID idHijo = secuencia.getIdProcedimientoPaso().getIdProcedimientoPaso();
            UUID idPadre = secuencia.getIdProcedimientoPasoReferencia();
            ProcedimientoPaso hijo = pasoDelProcedimiento(idProcedimiento, idHijo);
            if (hijo == null) {
                return Response.status(404).header("Entity-Not-Found", idHijo).build();
            }
            ProcedimientoPaso padre = pasoDelProcedimiento(idProcedimiento, idPadre);
            if (padre == null) {
                return Response.status(404).header("Entity-Not-Found", idPadre).build();
            }
            secuencia.setIdProcedimientoPasoSecuencia(null); // el id lo genera la BD
            secuencia.setIdProcedimientoPaso(hijo);          // entidad real, no el stub del JSON
            Response invalida = validar(secuencia);
            if (invalida != null) {
                return invalida;
            }
            String conflicto = validarCadena(idProcedimiento, padre, hijo);
            if (conflicto != null) {
                return Response.status(Response.Status.CONFLICT).entity(conflicto).build();
            }
            service.crear(secuencia);
            URI uri = uinfo.getAbsolutePathBuilder()
                    .path(String.valueOf(secuencia.getIdProcedimientoPasoSecuencia()))
                    .build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    /**
     * El paso si existe Y pertenece al procedimiento de la ruta; si no, null.
     */
    private ProcedimientoPaso pasoDelProcedimiento(UUID idProcedimiento, UUID idPaso) {
        ProcedimientoPaso paso = (ProcedimientoPaso) pasoService.buscar(idPaso);
        if (paso == null || !paso.getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
            return null;
        }
        return paso;
    }

    /**
     * Reglas de la cadena de pasos. Devuelve el mensaje del conflicto, o null
     * si todo está bien.
     */
    private String validarCadena(UUID idProcedimiento, ProcedimientoPaso padre, ProcedimientoPaso hijo) {
        if (Boolean.TRUE.equals(padre.getIndicaFin())) {
            return "El paso de referencia indica fin de rama; no se le pueden agregar pasos siguientes.";
        }
        if (pasoService.contarHijos(padre.getIdProcedimientoPaso()) > 0) {
            return "El paso de referencia ya tiene un paso siguiente; cada paso solo puede tener uno.";
        }
        Map<UUID, UUID> siguiente = new HashMap<>();   // padre -> hijo
        Set<UUID> conAnterior = new HashSet<>();
        for (ProcedimientoPasoSecuencia borde : service.listarPorProcedimiento(idProcedimiento)) {
            UUID idHijoBorde = borde.getIdProcedimientoPaso().getIdProcedimientoPaso();
            siguiente.put(borde.getIdProcedimientoPasoReferencia(), idHijoBorde);
            conAnterior.add(idHijoBorde);
        }
        if (conAnterior.contains(hijo.getIdProcedimientoPaso())) {
            return "El paso ya tiene un paso anterior; cada paso solo puede tener uno.";
        }
        // Ciclo: si bajando desde el hijo se llega al padre, el borde cerraría un lazo.
        Set<UUID> visitados = new HashSet<>();
        UUID actual = hijo.getIdProcedimientoPaso();
        while (actual != null && visitados.add(actual)) {
            if (actual.equals(padre.getIdProcedimientoPaso())) {
                return "La secuencia crearía un ciclo entre los pasos.";
            }
            actual = siguiente.get(actual);
        }
        return null;
    }

    private Response validar(ProcedimientoPasoSecuencia secuencia) {
        Set<ConstraintViolation<ProcedimientoPasoSecuencia>> violaciones = validator.validate(secuencia);
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
            ProcedimientoPasoSecuencia target = (ProcedimientoPasoSecuencia) service.buscar(id);
            if (target == null
                    || !target.getIdProcedimientoPaso().getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
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
            String propiedad = "idProcedimientoPaso.idProcedimiento.idProcedimiento";
            return Response.ok(service.findRangeByCriterio(propiedad, idProcedimiento, first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contarByCriterio(propiedad, idProcedimiento))
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).header("Application-Error", ex.getMessage()).build();
        }
    }

    /**
     * Solo se puede cambiar el tipo de secuencia; los pasos del borde se
     * conservan.
     */
    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @PathParam("id") UUID id,
            ProcedimientoPasoSecuencia cambios) {
        if (cambios == null) {
            return Response.status(422).header("Wrong-Param", "secuencia").build();
        }
        try {
            ProcedimientoPasoSecuencia existente = (ProcedimientoPasoSecuencia) service.buscar(id);
            if (existente == null
                    || !existente.getIdProcedimientoPaso().getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
                return Response.status(404).header("Entity-Not-Found", id).build();
            }
            existente.setTipoSecuencia(cambios.getTipoSecuencia());
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
            @PathParam("idProcedimiento") UUID idProcedimiento,
            @PathParam("id") UUID id) {
        try {
            ProcedimientoPasoSecuencia target = (ProcedimientoPasoSecuencia) service.buscar(id);
            if (target == null
                    || !target.getIdProcedimientoPaso().getIdProcedimiento().getIdProcedimiento().equals(idProcedimiento)) {
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
