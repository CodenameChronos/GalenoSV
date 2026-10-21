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
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.DocumentoService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.PersonaService;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.control.ReglaNegocioException;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Documento;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.Persona;

/**
 *
 * @author kardia
 */

/*  Depende de persona y tipoDocumento,
    teoricamente funciona tanto como /tipoDocumento/{idTipoDocumento}/documento
    tambien pero hace mas sentido buscar basado en la persona
 */
@Path("persona/{idPersona}/documento")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentoResource implements Serializable {

    @Inject
    DocumentoService service;

    @Inject
    PersonaService peService;

    @POST
    public Response create(
            @Context UriInfo uinfo,
            // Asigna el valor del parametro idPersona definido
            // en el Path propio de la clase
            @PathParam("idPersona") UUID idPersona,
            Documento doc) {
        if (doc == null) {
            return Response.status(422)
                    .header("Wrong-Param", "doc")
                    .build();
        }
        try {
            Persona persona = (Persona) peService.buscar(idPersona);
            if (persona == null) {
                return Response.status(404).header("Entity-Not-Found", idPersona).build();
            }
            service.crear(doc);
            // Estas 3 lineas se pueden simplificar anidando metodos
            // pero prefiero mantener el ejemplo del inge
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path(String.valueOf(doc.getIdDocumento())).build();
            return Response.created(uri).build();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{id}")
    public Response findById(
            // Asigna el valor del parametro idPersona definido
            // en el Path propio de la clase
            @PathParam("idPersona") UUID idPersona,
            @PathParam("id") UUID uuid) {

        if (uuid != null) {
            try {
                Documento target = (Documento) service.buscar(uuid);
                if (target == null || !target.getIdPersona().getIdPersona().equals(idPersona)) {
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
            // Asigna el valor del parametro idPersona definido
            // en el Path propio de la clase
            @PathParam("idPersona") UUID idPersona,
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
                    .header("Application-Error", ex.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(
            @PathParam("idPersona") UUID idPersona,
            @PathParam("id") UUID idDocumento
    ) {
        try {
            // Busca la tupla entera a partir del id proporcionado
            // ya que el service utiliza el flush de jakarta quee
            // requiere una instancia entera a eliminar
            Documento target = (Documento) service.buscar(idDocumento);

            // Verifica que la busqueda sea exitosa y que ademas
            // pertenezca a la misma persona que la ruta API esta
            // referenciando
            if (target == null || !target.getIdPersona().getIdPersona().equals(idPersona)) {
                return Response.status(404)
                        .header("Entity-Not-Found", idDocumento)
                        .build();
            }

            service.eliminar(target);
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
