package com.github.trepo.server.model;

import org.testng.annotations.Test;

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

    @Test
    public void validate_shouldErrorOnNullAuthor() {
        // TODO
    }

    @Test
    public void validate_shouldErrorOnNullEmail() {
        // TODO
    }

    @Test
    public void validate_shouldErrorOnNullMessage() {
        // TODO
    }
}
