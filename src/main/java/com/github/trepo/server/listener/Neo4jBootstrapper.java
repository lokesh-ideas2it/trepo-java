package com.github.trepo.server.listener;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Bootstrap and shutdown our Neo4j/vGraph instance.
 * @author John Clark.
 */
@WebListener
public class Neo4jBootstrapper implements ServletContextListener {

    /**
     * Our private vGraph instance.
     */
    private VGraph vGraph;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Initializing vGraph...");
        Neo4j2Graph neoGraph = new Neo4j2Graph("/tmp/neo4j");
        vGraph = new VGraph(neoGraph, "localhost:8081");
        VGraphSingleton.init(vGraph, neoGraph);
        System.out.println("vGraph Initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Stopping vGraph");
        vGraph.shutdown();
        System.out.println("vGraph Stopped");
    }
}
