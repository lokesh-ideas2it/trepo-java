package com.github.trepo.server.provider;

import com.github.trepo.server.model.ExceptionModel;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Maps any other exception.
 * @author John Clark.
 */
@Provider
public class ExceptionProvider implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        // TODO put stackTrace into traditional logging
        e.printStackTrace();
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionModel(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage()))
                .build();
    }
}
