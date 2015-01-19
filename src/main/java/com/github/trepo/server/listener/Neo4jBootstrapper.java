package com.github.trepo.server.listener;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * @author John Clark.
 */
@WebListener
public class Neo4jBootstrapper implements ServletContextListener {

    private Neo4j2Graph neoGraph;
    private VGraph vGraph;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Initializing vGraph...");
        neoGraph = new Neo4j2Graph("/tmp/neo4j");
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
