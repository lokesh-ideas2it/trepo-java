package com.github.trepo.server.provider;

import com.github.trepo.server.model.ExceptionModel;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * @author John Clark.
 */
@Provider
public class WebApplicationExceptionProvider implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException e) {

        return Response
                .status(e.getResponse().getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionModel(e.getResponse().getStatus(), e.getMessage()))
                .build();
    }
}
