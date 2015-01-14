package com.github.trepo.server.provider;

import com.github.trepo.server.model.ExceptionModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author John Clark.
 */
@Provider
public class ExceptionProvider implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionModel(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage()))
                .build();
    }
}
