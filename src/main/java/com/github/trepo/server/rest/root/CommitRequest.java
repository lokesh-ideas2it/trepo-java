package com.github.trepo.server.rest.root;

import com.github.trepo.server.model.CommitRequestModel;
import com.github.trepo.vgraph.Commit;
import com.github.trepo.vgraph.VGraph;
import com.github.trepo.vgraph.VGraphException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author John Clark.
 */
@Path("/commit")
public class CommitRequest {

    /**
     * Our vGraph instance.
     */
    private VGraph graph;

    /**
     * Commit any outstanding changes in vGraph.
     * @param request The Commit Request.
     * @return 201 on success.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(CommitRequestModel request) {

        if (request == null) {
            throw new WebApplicationException("A valid Commit Request is required", Response.Status.BAD_REQUEST);
        }

        request.validate();

        try {
            Commit commit = graph.commit(request.getAuthor(), request.getEmail(), request.getMessage());

            return Response
                    .status(Response.Status.CREATED)
                    .entity(commit)
                    .build();

        } catch (VGraphException e) {
            throw new WebApplicationException("Commit failed - " + e.getMessage(), e);
        }
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
