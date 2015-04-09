package com.github.trepo.server.application;

import com.github.trepo.server.factory.VGraphFactory;
import com.github.trepo.vgraph.VGraph;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author John Clark.
 */
public class Traversal extends ResourceConfig {

    public Traversal() {
        packages("com.github.trepo.server.rest.traversal", "com.github.trepo.server.provider");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(VGraphFactory.class).to(VGraph.class).in(RequestScoped.class);
            }
        });
    }
}
