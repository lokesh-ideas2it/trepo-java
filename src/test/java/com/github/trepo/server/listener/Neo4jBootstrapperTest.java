package com.github.trepo.server.listener;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author John Clark.
 */
public class Neo4jBootstrapperTest {

    @Test
    public void shouldwork() {

        // TODO eventually test contextInitialized/contextDestroyed

        Neo4jBootstrapper b = new Neo4jBootstrapper();

        assertThat(b).isNotNull();
    }
}
