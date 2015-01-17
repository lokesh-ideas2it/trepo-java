package com.github.trepo.server.factory;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.process.internal.RequestScoped;

import java.util.logging.Logger;

/**
 * @author John Clark.
 */
public class VGraphFactory implements Factory<VGraph> {

    private VGraph graph;

    private Neo4j2Graph neo4j2Graph;

    public VGraphFactory() {
        graph = VGraphSingleton.getInstance().getGraphDb();
        neo4j2Graph = VGraphSingleton.getInstance().getNeo4j2Graph();
    }

    @Override
    @RequestScoped
    public VGraph provide() {
        System.out.println("providing " + graph);
        return graph;
    }

    @Override
    public void dispose(VGraph vGraph) {
        System.out.println("Cleaning up " + graph);
        neo4j2Graph.commit();
    }

}
