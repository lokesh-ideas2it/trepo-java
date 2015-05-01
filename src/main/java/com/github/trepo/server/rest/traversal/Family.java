package com.github.trepo.server.rest.traversal;

import com.github.trepo.npipes.Query;
import com.github.trepo.npipes.Traversal;
import com.github.trepo.npipes.TraversalBuilder;
import com.github.trepo.ptree.model.core.PersonNameModel;
import com.github.trepo.ptree.model.what.BirthModel;
import com.github.trepo.ptree.model.what.DeathModel;
import com.github.trepo.ptree.model.what.NameModel;
import com.github.trepo.ptree.ref.Key;
import com.github.trepo.ptree.ref.Label;
import com.github.trepo.vgraph.Direction;
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
        Iterator<Node> itr;

        if (person == null || !person.getLabel().equals(Label.PERSON)) {
            throw new WebApplicationException("Person Not Found", Response.Status.NOT_FOUND);
        }

        Query query = new Query();

        // Name
        query.addTraversal(new TraversalBuilder()
                .v(id)
                .set("traversal", "name")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        // Birth
        query.addTraversal(new TraversalBuilder()
                .v(id)
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
                .v(id)
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
                .v(id)
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
                .v(id)
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
                .v(id)
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
                .v(id)
                .set("traversal", "child")
                .out(Label.BIRTH_FATHER_REF, Label.BIRTH_MOTHER_REF)
                .in(Label.BIRTH_CHILD_REF)
                .store(SpecialProperty.ID, "id")
                .store(SpecialProperty.REPO, "repo")
                .out(Label.NAME_PERSON_REF)
                .store(Key.NAME_NAME, "name")
                .traversal());

        query.execute(graph);

        // Loop through and execute external requests
        // TODO


        // Collate results
        LinkedHashMap<String, Object> family = new LinkedHashMap<>();
        ArrayList<Object> parents = new ArrayList<>();
        ArrayList<Object> spouses = new ArrayList<>();
        ArrayList<Object> children = new ArrayList<>();


        family.put("id", id);
        family.put("repo", person.getRepo());

        for (Traversal traversal: query.getTraversals().values()) {
            HashMap<String, Object> payload = traversal.getPayload();
            Object typeObject = payload.get("traversal");
            String type = "";

            if (typeObject != null && typeObject instanceof String) {
                type = (String) typeObject;
            }

            switch (type) {
                case "name":
                    family.put("name", payload.get("name"));
                    break;
                case "birth":
                    // TODO
                    break;
                case "death":
                    // TODO
                    break;
                case "mother":
                    // TODO
                    break;
                case "father":
                    // TODO
                    break;
                case "spouse":
                    // TODO filter out self
                    break;
                case "child":
                    // TODO
                    break;
            }
        }

        family.put("query", query);

        return Response
                .status(Response.Status.OK)
                .entity(family)
                .build();

        /**
        LinkedHashMap<String, Object> family = new LinkedHashMap<>();

        family.put("id", id);
        family.put("repo", person.getRepo());


        // Get Name
        NameModel name = null;
        for (Node node: person.getNodes(Direction.OUT, Label.NAME_PERSON_REF)) {
            name = new NameModel(graph, node.getId());
            name.readFromGraph();
            break;
        }
        family.put("name", name);

        // Get Birth
        BirthModel birth = null;
        for (Node node: person.getNodes(Direction.OUT, Label.BIRTH_CHILD_REF)) {
            birth = new BirthModel(graph, node.getId());
            birth.readFromGraph();
            break;
        }
        family.put("birth", birth);

        // Get Death
        DeathModel death = null;
        for (Node node: person.getNodes(Direction.OUT, Label.DEATH_PERSON_REF)) {
            death = new DeathModel(graph, node.getId());
            death.readFromGraph();
            break;
        }
        family.put("death", death);

        // Get Parents
        HashSet<PersonNameModel> parents = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.BIRTH_CHILD_REF)) {
            itr = node.getNodes(Direction.IN, Label.BIRTH_FATHER_REF).iterator();
            if (itr.hasNext()) {
                PersonNameModel father = new PersonNameModel(graph, itr.next().getId());
                father.readFromGraph();
                parents.add(father);
            }
            itr = node.getNodes(Direction.IN, Label.BIRTH_MOTHER_REF).iterator();
            if (itr.hasNext()) {
                PersonNameModel mother = new PersonNameModel(graph, itr.next().getId());
                mother.readFromGraph();
                parents.add(mother);
            }
        }
        family.put("parents", parents);

        // Get Spouses
        HashSet<PersonNameModel> spouses = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.MARRIAGE_SPOUSE_REF)) {
            for (Node spouse: node.getNodes(Direction.IN, Label.MARRIAGE_SPOUSE_REF)) {
                if (!person.getId().equals(spouse.getId())) {
                    PersonNameModel spouseModel = new PersonNameModel(graph, spouse.getId());
                    spouseModel.readFromGraph();
                    spouses.add(spouseModel);
                }
            }
        }
        family.put("spouses", spouses);

        // Get Children
        HashSet<PersonNameModel> children = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.BIRTH_FATHER_REF, Label.BIRTH_MOTHER_REF)) {
            itr = node.getNodes(Direction.IN, Label.BIRTH_CHILD_REF).iterator();
            if (itr.hasNext()) {
                PersonNameModel child = new PersonNameModel(graph, itr.next().getId());
                child.readFromGraph();
                children.add(child);
            }
        }
        family.put("children", children);

        return Response
                .status(Response.Status.OK)
                .entity(family)
                .build();
        */

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
