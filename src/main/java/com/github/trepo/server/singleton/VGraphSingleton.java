package com.github.trepo.server.singleton;

import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;

/**
 * @author John Clark.
 */
public class VGraphSingleton {

    private static VGraphSingleton INSTANCE;

    public static void init(VGraph graphDb, Neo4j2Graph neo4j2Graph) {
        INSTANCE = new VGraphSingleton(graphDb, neo4j2Graph);
    }

    public static VGraphSingleton getInstance() {
        return INSTANCE;
    }

    private final VGraph graphDb;

    private final Neo4j2Graph neo4j2Graph;

    private VGraphSingleton(VGraph graphDb, Neo4j2Graph neo4j2Graph) {
        this.graphDb = graphDb;
        this.neo4j2Graph = neo4j2Graph;
    }

    public VGraph getGraphDb() {
        return graphDb;
    }

    public Neo4j2Graph getNeo4j2Graph() {
        return neo4j2Graph;
    }
}
