package com.github.trepo.server.singleton;

import com.github.trepo.vgraph.VGraph;

/**
 * @author John Clark.
 */
public class VGraphSingleton {

    private static VGraphSingleton INSTANCE;

    public static void init(VGraph graphDb) {
        INSTANCE = new VGraphSingleton(graphDb);
    }

    public static VGraphSingleton getInstance() {
        return INSTANCE;
    }

    private final VGraph graphDb;

    private VGraphSingleton(VGraph graphDb) {
        this.graphDb = graphDb;
    }

    public VGraph getGraphDb() {
        return graphDb;
    }
}
