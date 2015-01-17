package com.github.trepo.server.listener;

import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.github.trepo.vgraph.util.Property;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author John Clark.
 */
@WebListener
public class Neo4jBootstrapper implements ServletContextListener {

    Logger logger = Logger.getLogger("TESTING");

    private VGraph graph;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.log(Level.INFO, "STARTING");

        Neo4j2Graph g = new Neo4j2Graph("/tmp/neo4j");
        //g.setCheckElementsInTransaction(true);

        logger.log(Level.INFO, "-----Vertexes");
        for (Vertex v: g.getVertices()) {
            logger.log(Level.INFO, "Vertex: "+v.getId()+" - "+v.getProperty(Property.ID));
        }

        graph = new VGraph(g, "localhost:8081");
        VGraphSingleton.init(graph, g);

        for (String idx: g.getIndexedKeys(Vertex.class)) {
            logger.log(Level.INFO, "-----IDX: "+idx);
        }
        g.commit();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.log(Level.INFO, "STOPPING");
        if (graph != null) {
            graph.shutdown();
        }
    }
}
