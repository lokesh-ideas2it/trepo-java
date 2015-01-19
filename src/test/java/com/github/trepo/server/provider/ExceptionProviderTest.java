package com.github.trepo.server.provider;

import com.github.trepo.server.model.ExceptionModel;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class ExceptionProviderTest {

    @Test
    public void shouldWork() {
        ExceptionProvider provider = new ExceptionProvider();

        Exception e = new Exception("Bad things are afoot");

        Response response = provider.toResponse(e);

        assertThat(response.getStatus()).isEqualTo(500);
        ExceptionModel exceptionModel = (ExceptionModel) response.getEntity();
        assertThat(exceptionModel.getCode()).isEqualTo(500);
        assertThat(exceptionModel.getMsg()).isEqualTo("Bad things are afoot");
    }
}
