package com.github.trepo.server.rest.traversal;

import com.github.trepo.npipes.*;
import com.github.trepo.npipes.client.NPipesClient;
import com.github.trepo.ptree.ref.Key;
import com.github.trepo.ptree.ref.Label;
import com.github.trepo.vgraph.Node;
import com.github.trepo.vgraph.SpecialProperty;
import com.github.trepo.vgraph.VGraph;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

/**
 * @author John Clark.
 */
@Path("/family/{id}")
public class Family {

    /**
     * Our vGraph instance.
     */
    private VGraph graph;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response putId(@PathParam("id") String id) {

        Node person = graph.getNode(id);
        NPipesClient client = new NPipesClient();

        if (person == null || !person.getLabel().equals(Label.PERSON)) {
            throw new WebApplicationException("Person Not Found", Response.Status.NOT_FOUND);
        }

        Query query = new Query();

        // Name
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "name")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        // Birth
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "birth")
                .out(Label.BIRTH_CHILD_REF)
                .as("birth")
                .out(Label.BIRTH_DATE_REF)
                .store(Key.DATE_ORIGINAL, "original")
                .store(Key.DATE_FORMAL, "formal")
                .back("birth")
                .out(Label.BIRTH_PLACE_REF)
                .store(Key.PLACE_NAME, "place")
                .traversal());

        // Death
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "death")
                .out(Label.DEATH_PERSON_REF)
                .as("death")
                .out(Label.DEATH_DATE_REF)
                .store(Key.DATE_ORIGINAL, "original")
                .store(Key.DATE_FORMAL, "formal")
                .back("death")
                .out(Label.DEATH_PLACE_REF)
                .store(Key.PLACE_NAME, "place")
                .traversal());

        // Mother
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "mother")
                .out(Label.BIRTH_CHILD_REF)
                .in(Label.BIRTH_MOTHER_REF)
                .store(SpecialProperty.ID, "id")
                .store(SpecialProperty.REPO, "repo")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        // Father
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "father")
                .out(Label.BIRTH_CHILD_REF)
                .in(Label.BIRTH_FATHER_REF)
                .store(SpecialProperty.ID, "id")
                .store(SpecialProperty.REPO, "repo")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        // Spouse
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "spouse")
                .out(Label.MARRIAGE_SPOUSE_REF)
                .in(Label.MARRIAGE_SPOUSE_REF)
                .store(SpecialProperty.ID, "id")
                .store(SpecialProperty.REPO, "repo")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        // Child
        query.addTraversal(new TraversalBuilder()
                .n(id)
                .set("traversal", "child")
                .out(Label.BIRTH_FATHER_REF, Label.BIRTH_MOTHER_REF)
                .in(Label.BIRTH_CHILD_REF)
                .store(SpecialProperty.ID, "id")
                .store(SpecialProperty.REPO, "repo")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        client.execute(query, graph);

        // Collate results
        LinkedHashMap<String, Object> family = new LinkedHashMap<>();
        ArrayList<Object> parents = new ArrayList<>();
        ArrayList<Object> spouses = new ArrayList<>();
        ArrayList<Object> children = new ArrayList<>();


        family.put("id", id);
        family.put("repo", person.getRepo());
        family.put("name", null);
        family.put("birth", null);
        family.put("death", null);

        for (Traversal traversal: query.getTraversals().values()) {
            // Skip non complete/finished traversals
            if (traversal.getStatus() != Status.FINISHED) {
                continue;
            }
            HashMap<String, Object> payload = traversal.getPayload();

            switch (traversal.getFromPayload("traversal", String.class)) {
                case "name":
                    family.put("name", payload.get("name"));
                    break;
                case "birth":
                    HashMap<String, Object> birth = new HashMap<>();
                    birth.put("formal", payload.get("formal"));
                    birth.put("original", payload.get("original"));
                    birth.put("place", payload.get("place"));
                    family.put("birth", birth);
                    break;
                case "death":
                    HashMap<String, Object> death = new HashMap<>();
                    death.put("formal", payload.get("formal"));
                    death.put("original", payload.get("original"));
                    death.put("place", payload.get("place"));
                    family.put("death", death);
                    break;
                case "mother":
                    HashMap<String, Object> mother = new HashMap<>();
                    mother.put("id", payload.get("id"));
                    mother.put("repo", payload.get("repo"));
                    mother.put("name", payload.get("name"));
                    parents.add(mother);
                    break;
                case "father":
                    HashMap<String, Object> father = new HashMap<>();
                    father.put("id", payload.get("id"));
                    father.put("repo", payload.get("repo"));
                    father.put("name", payload.get("name"));
                    parents.add(father);
                    break;
                case "spouse":
                    if (id.equals(payload.get("id"))) {
                        continue;
                    }
                    HashMap<String, Object> spouse = new HashMap<>();
                    spouse.put("id", payload.get("id"));
                    spouse.put("repo", payload.get("repo"));
                    spouse.put("name", payload.get("name"));
                    spouses.add(spouse);
                    break;
                case "child":
                    HashMap<String, Object> child = new HashMap<>();
                    child.put("id", payload.get("id"));
                    child.put("repo", payload.get("repo"));
                    child.put("name", payload.get("name"));
                    children.add(child);
                    break;
            }
        }

        family.put("parents", parents);
        family.put("spouses", spouses);
        family.put("children", children);
        family.put("query", query);

        return Response
                .status(Response.Status.OK)
                .entity(family)
                .build();
    }

    /**
     * Set our vGraph instance via inject.
     * @param vGraph Our vGraph instance.
     */
    @Inject
    public void setGraph(VGraph vGraph) {
        graph = vGraph;
    }
}
