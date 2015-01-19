package com.github.trepo.server.rest.root;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Root returns a set of metadata.
 * @author John Clark.
 */
@Path("/")
public class Root {

    /**
     * Gets metadata about this Trepo Server.
     * @return The metadata.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {

        Properties properties = getProperties();

        LinkedHashMap<String, String> metadata = new LinkedHashMap<>();
        metadata.put("name", properties.getProperty("name"));
        metadata.put("version", properties.getProperty("version"));
        metadata.put("pTree", properties.getProperty("ptree"));
        metadata.put("vGraph", properties.getProperty("vgraph"));
        metadata.put("graph", properties.getProperty("graph"));
        metadata.put("server", properties.getProperty("server"));


        return Response
                .status(Response.Status.OK)
                .entity(metadata)
                .build();
    }

    /**
     * Loads the generated properties file.
     * @return The properties file.
     */
    public Properties getProperties() {
        Properties props = new Properties();

        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("trepo.properties");
            props.load(stream);
        } catch (Exception e) {
            return props;
        }

        return props;
    }
}
