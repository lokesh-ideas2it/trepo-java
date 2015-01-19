package com.github.trepo.server.application;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author John Clark.
 */
public class Root extends ResourceConfig {

    public Root() {
        packages("com.github.trepo.server.rest.root", "com.github.trepo.server.provider");
    }
}
