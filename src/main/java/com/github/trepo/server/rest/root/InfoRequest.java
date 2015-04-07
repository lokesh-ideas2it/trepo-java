package com.github.trepo.server.rest.root;

import com.github.trepo.vgraph.VGraph;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author John Clark.
 */
@Path("/info")
public class InfoRequest {

    /**
     * Our vGraph instance.
     */
    private VGraph graph;

    /**
     * Gets metadata about this Trepo Server.
     * @return The metadata.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        return Response
                .status(Response.Status.OK)
                .entity(graph.info())
                .build();
    }

    /**
     * Set our vGraph instance via inject.
     * @param vGraph Our vGraph instance.
     */
    @Inject
    public void setGraph(VGraph vGraph) {
        graph = vGraph;
    }
}
