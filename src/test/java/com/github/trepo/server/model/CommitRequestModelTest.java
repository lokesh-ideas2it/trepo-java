package com.github.trepo.server.model;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class CommitRequestModelTest {

    @Test
    public void shouldWork() {
        CommitRequestModel model = new CommitRequestModel("author", "email", "message");

        assertThat(model.getAuthor()).isEqualTo("author");
        assertThat(model.getEmail()).isEqualTo("email");
        assertThat(model.getMessage()).isEqualTo("message");
    }
}
