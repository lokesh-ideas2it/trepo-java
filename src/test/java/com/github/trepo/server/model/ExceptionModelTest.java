package com.github.trepo.server.model;

import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class ExceptionModelTest {

    @Test
    public void constructor1_shouldWork() {
        ExceptionModel model = new ExceptionModel(404, "Not Found");

        assertThat(model.getCode()).isEqualTo(404);
        assertThat(model.getMsg()).isEqualTo("Not Found");
    }

    @Test
    public void constructor2_shouldWork() {
        ExceptionModel model = new ExceptionModel(Response.Status.BAD_REQUEST, "errors abound");

        assertThat(model.getCode()).isEqualTo(400);
        assertThat(model.getMsg()).isEqualTo("errors abound");
    }
}
