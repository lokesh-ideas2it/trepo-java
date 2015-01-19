package com.github.trepo.server.rest.root;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author John Clark.
 */
@Path("/")
public class Root {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        return Response
                .status(Response.Status.OK)
                .entity("true")
                .build();
    }
}
