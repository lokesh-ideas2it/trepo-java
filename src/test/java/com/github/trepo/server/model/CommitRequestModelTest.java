package com.github.trepo.server.model;

import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class CommitRequestModelTest {

    @Test
    public void constructor_shouldWork() {
        CommitRequestModel model = new CommitRequestModel("author", "email", "message");

        assertThat(model.getAuthor()).isEqualTo("author");
        assertThat(model.getEmail()).isEqualTo("email");
        assertThat(model.getMessage()).isEqualTo("message");
    }

    /**
     * validate
     */
    @Test
    public void validate_shouldErrorOnNullAuthor() {
        CommitRequestModel model = new CommitRequestModel(null, "email", "message");

        try {
            model.validate();
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getMessage()).isEqualTo("author missing");
        }
    }

    @Test
    public void validate_shouldErrorOnNullEmail() {
        CommitRequestModel model = new CommitRequestModel("author", null, "message");

        try {
            model.validate();
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getMessage()).isEqualTo("email missing");
        }
    }

    @Test
    public void validate_shouldErrorOnNullMessage() {
        CommitRequestModel model = new CommitRequestModel("author", "email", null);

        try {
            model.validate();
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(400);
            assertThat(e.getMessage()).isEqualTo("message missing");
        }
    }

    @Test
    public void validate_shouldWork() {
        CommitRequestModel model = new CommitRequestModel("author", "email", "message");
        model.validate();
    }
}
