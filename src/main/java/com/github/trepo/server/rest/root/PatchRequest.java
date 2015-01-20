package com.github.trepo.server.rest.root;

import com.github.trepo.vgraph.Commit;
import com.github.trepo.vgraph.VGraph;
import com.github.trepo.vgraph.exception.VGraphException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Patch vGraph with a commit.
 * @author John Clark.
 */
@Path("/patch")
public class PatchRequest {

    /**
     * Our vGraph instance.
     */
    private VGraph graph;

    /**
     * Patch vGraph with the supplied commit.
     * @param commit The commit to apply.
     * @return 204.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Commit commit) {

        if (commit == null) {
            throw new WebApplicationException("A valid Commit is required", Response.Status.BAD_REQUEST);
        }

        try {
            commit.validate();
        } catch (VGraphException e) {
            throw new WebApplicationException("Invalid commit - " + e.getMessage(), e, Response.Status.BAD_REQUEST);
        }

        try {
            graph.patch(commit);

            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();

        } catch (VGraphException e) {
            throw new WebApplicationException("Patch failed - " + e.getMessage(), e);
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
