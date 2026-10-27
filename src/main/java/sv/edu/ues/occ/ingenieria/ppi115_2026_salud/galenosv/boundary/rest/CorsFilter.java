/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.ppi115_2026_salud.galenosv.boundary.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author kardia
 */
@Provider
public class CorsFilter implements ContainerResponseFilter{

    public static final String METODOS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    public static final int MAX_AGE = 60 * 60 * 45;
    public static final String DEFAULT_ALLOWED_METHODS = "origin, accept, content-type, Total-registros, Wrong-Param, Entity-Not-Found, Application-Error";
    public static final String DEFAULT_EXPOSED_HEADERS = "location, info, Total-registros, Wrong-Param, Entity-Not-Found, Application-Error";
    
    @Override
    public void filter(ContainerRequestContext ctx, ContainerResponseContext respCtx) throws IOException {
        MultivaluedMap<String, Object> headers = respCtx.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", getAllowedHeaders(ctx));
        headers.add("Access-Control-Expose-Headers", getExposedHeaders(ctx));
        headers.add("Access-Control-Allow-Credentials", "true");
        headers.add("Access-Control-Allow-Methods", METODOS);
        headers.add("Access-Control-Max-Age", MAX_AGE);
    }
    
    String getAllowedHeaders (ContainerRequestContext ctx) {
        List<String> headers = ctx.getHeaders().get("Access-Control-Allow-Headers");
        return crearListaHeaders(headers, DEFAULT_ALLOWED_METHODS);
    }
    
    String getExposedHeaders (ContainerRequestContext ctx) {
        List<String> headers = ctx.getHeaders().get("Access-Control-Expose-Headers");
        return crearListaHeaders(headers, DEFAULT_EXPOSED_HEADERS);
    }
    
    String crearListaHeaders(List<String> headers, String defaultHeaders) {
        if ( headers == null || headers.isEmpty()) {
            return defaultHeaders;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            sb.append(header);
            sb.append(", ");
        }
        sb.append(defaultHeaders);
        return sb.toString();
    }
    
}
