package com.github.trepo.server;

import com.github.trepo.server.application.PTree;
import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.github.trepo.vgraph.util.Property;
import com.sun.net.httpserver.HttpServer;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author John Clark.
 */
public class Server {

    static Logger logger = Logger.getLogger("TESTING");

    public static void main(String [] args) {

/*
        Neo4j2Graph g = new Neo4j2Graph("/tmp/neo4j");

        logger.log(Level.INFO, "Printing Vertexes");
        for (Vertex v: g.getVertices()) {
            logger.log(Level.INFO, "Vertex: "+v.getId()+" - "+v.getProperty(Property.ID));
        }



        final VGraph graph = new VGraph(g, "localhost:8081");
        graph.addNode("label");

        graph.shutdown();
*/

        logger.log(Level.INFO, "STARTING");

        final Neo4j2Graph g = new Neo4j2Graph("/tmp/neo4j");

        logger.log(Level.INFO, "-----Vertexes");
        for (Vertex v: g.getVertices()) {
            System.out.println("Vertex: " + v.getId());
            System.out.println("ID: " + v.getProperty(Property.ID));
            System.out.println("LABEL: " + v.getProperty(Property.LABEL));
            System.out.println("REPO: " + v.getProperty(Property.REPO));
        }
        logger.log(Level.INFO, "-----Vertexes");

        final VGraph graph = new VGraph(g, "localhost:8081");
        VGraphSingleton.init(graph, g);

        System.out.println("init " + graph);


        URI baseUri = UriBuilder.fromUri("http://localhost/graph/").port(8081).build();
        ResourceConfig config = new PTree();
        final HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                //g.commit();
                System.out.println("Shutting down");
                logger.log(Level.INFO, "-----Vertexes");
                for (Vertex v: g.getVertices()) {
                    System.out.println("Vertex: " + v.getId());
                    System.out.println("ID: " + v.getProperty(Property.ID));
                    System.out.println("LABEL: " + v.getProperty(Property.LABEL));
                    System.out.println("REPO: " + v.getProperty(Property.REPO));

                    System.out.println("Keys");
                    for (String k: v.getPropertyKeys()) {
                        System.out.println(k);
                    }

                }
                graph.shutdown();
                server.stop(0);
            }
        });
        /*
        uncomment to allow for debugging

        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
        */

    }
}
