package com.github.trepo.server.application;

import com.github.trepo.server.factory.VGraphFactory;
import com.github.trepo.vgraph.VGraph;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configure the pTree Application.
 * @author John Clark.
 */
public class PTree extends ResourceConfig {

    /**
     * Register packages and vGraph factory.
     */
    public PTree() {
        packages("com.github.trepo.ptree.rest", "com.github.trepo.server.provider");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(VGraphFactory.class).to(VGraph.class).in(RequestScoped.class);
            }
        });

    }
}
