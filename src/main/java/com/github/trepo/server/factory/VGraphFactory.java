package com.github.trepo.server.factory;

import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.glassfish.hk2.api.Factory;

/**
 * @author John Clark.
 */
public class VGraphFactory implements Factory<VGraph> {

    private VGraph graph;

    public VGraphFactory() {
        graph = new VGraph(new TinkerGraph(), "localhost:8080");
    }

    @Override
    public VGraph provide() {
        return graph;
    }

    @Override
    public void dispose(VGraph vGraph) {

    }
}
