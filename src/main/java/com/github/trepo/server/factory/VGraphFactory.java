package com.github.trepo.server.factory;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import org.glassfish.hk2.api.Factory;
import org.glassfish.jersey.process.internal.RequestScoped;

/**
 * This is the factory that provides our vGraph instance.
 * Because the auto-transactions in Neo4j2Graph are ThreadLocal, we must perform a
 * commit AFTER EVERY REQUEST and IN THE SAME THREAD AS THE REQUEST.
 * Failure to do so results in open transactions on Shutdown, and lost data.
 * This is why this Factory is RequestScoped, so we can call commit.
 * It uses a traditional Singleton to provide the vGraph and Neo4j Instance.
 * @author John Clark.
 */
public class VGraphFactory implements Factory<VGraph> {

    /**
     * Our vGraph Instance.
     */
    private VGraph graph;

    /**
     * Our Neo4j Instance.
     */
    private Neo4j2Graph neo4j2Graph;

    /**
     * Save an instance to vGraph and Neo4j.
     */
    public VGraphFactory() {
        graph = VGraphSingleton.getInstance().getGraphDb();
        neo4j2Graph = VGraphSingleton.getInstance().getNeo4j2Graph();
    }

    /**
     * Return an instance of vGraph.
     * @return vGraph.
     */
    @Override
    @RequestScoped
    public VGraph provide() {
        return graph;
    }

    /**
     * Commit any changes to Neo4j.
     * @param vGraph Our vGraph instance.
     */
    @Override
    public void dispose(VGraph vGraph) {
        neo4j2Graph.commit();
    }

}
