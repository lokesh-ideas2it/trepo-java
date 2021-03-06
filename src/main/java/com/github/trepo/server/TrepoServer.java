package com.github.trepo.server;

import com.github.trepo.server.application.NPipes;
import com.github.trepo.server.application.PTree;
import com.github.trepo.server.application.Root;
import com.github.trepo.server.application.Traversal;
import com.github.trepo.server.listener.Neo4jBootstrapper;
import com.github.trepo.server.singleton.VGraphSingleton;
import com.github.trepo.vgraph.VGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

/**
 * @author John Clark.
 */
public class TrepoServer {


    public static void main(String [] args) throws Exception {

        // Set logging type/level
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

        int port = 8081;
        String portOption = System.getProperty("port");
        if (portOption != null) {
            port = Integer.parseInt(portOption);
        }

        // Setup Server
        final Server server = new Server(port);
        ServletContextHandler context
                = new ServletContextHandler(server, "/");

        // Add vGraph bootstrapper
        context.addEventListener(new Neo4jBootstrapper("http://localhost:"+port));

        // Root
        ServletHolder root = new ServletHolder(new ServletContainer(new Root()));
        context.addServlet(root, "/*");

        // Traversal
        ServletHolder traversal = new ServletHolder(new ServletContainer(new Traversal()));
        context.addServlet(traversal, "/traversal/*");

        // nPipes
        ServletHolder nPipes = new ServletHolder(new ServletContainer(new NPipes()));
        context.addServlet(nPipes, "/npipes/*");

        // pTree
        ServletHolder pTree = new ServletHolder(new ServletContainer(new PTree()));
        context.addServlet(pTree, "/graph/*");

        FilterHolder holder = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "HEAD,GET,POST,PUT,DELETE,PATCH");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting Down...");
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        server.start();
        server.join();


        //uncomment to allow for debugging

        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);


    }
}
