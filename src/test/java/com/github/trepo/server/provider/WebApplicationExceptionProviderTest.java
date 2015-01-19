package com.github.trepo.server.provider;

import com.github.trepo.server.model.ExceptionModel;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class WebApplicationExceptionProviderTest {

    @Test
    public void shouldWork() {
        WebApplicationExceptionProvider provider = new WebApplicationExceptionProvider();

        WebApplicationException e = new WebApplicationException("whoops!", Response.Status.NOT_FOUND);

        Response response = provider.toResponse(e);
        assertThat(response.getStatus()).isEqualTo(404);
        ExceptionModel exceptionModel = (ExceptionModel) response.getEntity();
        assertThat(exceptionModel.getCode()).isEqualTo(404);
        assertThat(exceptionModel.getMsg()).isEqualTo("whoops!");
    }
}
