package com.github.trepo.server.application;

import com.github.trepo.server.factory.VGraphFactory;
import com.github.trepo.vgraph.VGraph;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;

/**
 * @author John Clark.
 */
public class PTree extends ResourceConfig {

    public PTree() {
        packages("com.github.trepo.ptree.rest.core", "com.github.trepo.server.provider");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(VGraphFactory.class).to(VGraph.class).in(Singleton.class);
            }
        });
    }
}
