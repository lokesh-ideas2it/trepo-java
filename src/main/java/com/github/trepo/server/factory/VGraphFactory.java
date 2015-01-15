package com.github.trepo.server.factory;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import org.glassfish.hk2.api.Factory;

import java.util.logging.Logger;

/**
 * @author John Clark.
 */
public class VGraphFactory implements Factory<VGraph> {

    private VGraph graph;

    public VGraphFactory() {
        graph = VGraphSingleton.getInstance().getGraphDb();
    }

    @Override
    public VGraph provide() {
        return graph;
    }

    @Override
    public void dispose(VGraph vGraph) {

    }

}
