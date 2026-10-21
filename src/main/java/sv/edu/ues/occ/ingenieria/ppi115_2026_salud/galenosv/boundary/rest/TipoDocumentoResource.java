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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;

import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.TipoDocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
@Path("tipoDocumento")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TipoDocumentoResource implements Serializable {

    @Inject
    private TipoDocumentoService service;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            TipoDocumento tdoc
    ) {
        if (tdoc == null) {
            return Response.status(422)
                    .header("Wrong-Param", "tdoc")
                    .build();
        }
        tdoc.setActivo(Boolean.TRUE);
        try {
            service.crear(tdoc);
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(tdoc.getIdTipoDocumento())).build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") UUID uuid) {
        if (uuid != null) {
            try {
                TipoDocumento target = (TipoDocumento) service.buscar(uuid);
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
            return Response.ok(service.findRange(first, max))
                    .type(MediaType.APPLICATION_JSON)
                    .header("Total-registros", service.contar())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID uuid) {
        try {
            // Busca la tupla entera a partir del id proporcionado
            // ya que el service utiliza el flush de jakarta quee
            // requiere una instancia entera a eliminar
            TipoDocumento target = (TipoDocumento) service.buscar(uuid);
            if (target == null) {
                return Response.status(404)
                        .header("Entity-Not-Found", uuid)
                        .build();
            }

            service.eliminar(target);
            return Response.noContent().build();
            
            // Excepcion de tipo logica de negocion (creada por Kevin)
            // para imposibilitar eliminar tuplas que ya poseen
            // registros hijos referenciandole
        } catch (ReglaNegocioException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.TEXT_PLAIN)
                    .entity(ex.getMessage())
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500)
                    .build();
        }
    }

}
