package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;
import sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.entity.TipoDocumento;

/**
 *
 * @author kardia
 */
@Path("")
public class TipoDocumentoResource implements Serializable {
 
    @POST
    public Response crear( @Context UriInfo uinfo,
            TipoDocumento t) {
        if (t == null) {
            return Response.status(422).header("Wrong format", "tipoDocumento").build();
        } 
        t.setActivo(Boolean.TRUE);
        try {
            UriBuilder uBuilder = uinfo.getAbsolutePathBuilder();
            URI uri = uBuilder.path("").build();
            return Response.created(uri).build();
        } catch(Exception ex){}
        return null;
    }
 
    @GET
    @Path("{id}")
    public Response findById(
            @PathParam("id")
                    UUID uuid
                    ) {
        if (uuid != null) {
            try {
                //tipoDocumentoService.findById;
            } catch(Exception ex) {
                return Response.status(500).header("error", ex.getMessage()).build();
            }
        } else {
            return Response.status(422).header("Wrong format", "tipoDocumento").build();
        }
        return null;
    }
    
}
