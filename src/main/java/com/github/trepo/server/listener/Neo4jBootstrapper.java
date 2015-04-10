package com.github.trepo.server.listener;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.github.trepo.vgraph.blueprints.BlueprintsVGraph;
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
     * Our repository identifier.
     */
    private String repository;

    /**
     * Our private Neo4j2 Graph.
     */
    private Neo4j2Graph neoGraph;

    /**
     * Our private vGraph instance.
     */
    private BlueprintsVGraph vGraph;

    public Neo4jBootstrapper(String repo) {
        repository = repo;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Initializing vGraph...");
        String dir = "/tmp/neo4j";
        if (System.getProperty("db") != null) {
            dir = System.getProperty("db");
        }
        neoGraph = new Neo4j2Graph(dir);
        vGraph = new BlueprintsVGraph(neoGraph, repository);
        neoGraph.commit();
        VGraphSingleton.init(vGraph, neoGraph);
        System.out.println("vGraph Initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Stopping vGraph");
        neoGraph.commit();
        vGraph.shutdown();
        System.out.println("vGraph Stopped");
    }
}
