package com.github.trepo.server.rest.traversal;

import com.github.trepo.npipes.Query;
import com.github.trepo.npipes.Step;
import com.github.trepo.npipes.Traversal;
import com.github.trepo.npipes.TraversalBuilder;
import com.github.trepo.npipes.gson.StepTypeAdapter;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
        HashMap<String, Query> outstandingQueries = new HashMap<>();
        for (Traversal traversal: query.getTraversals().values()) {
            if (traversal.isComplete()) {
                continue;
            }
            String repo = traversal.getPath().get(traversal.getPath().size()-1).getRepo();
            if (!outstandingQueries.containsKey(repo)) {
                outstandingQueries.put(repo, new Query());
            }
            outstandingQueries.get(repo).addTraversal(traversal);
        }

        if (outstandingQueries.size() > 0) {
            Gson gson = new GsonBuilder()
                    .registerTypeHierarchyAdapter(Step.class, new StepTypeAdapter())
                    .setPrettyPrinting()
                    .create();
            for (String repo: outstandingQueries.keySet()) {

                try {
                    HttpResponse<String> response = Unirest.post(repo + "/npipes/query")
                            .header("Content-Type", "application/json")
                            .body(gson.toJson(outstandingQueries.get(repo)))
                            .asString();

                    //System.out.println(response.getBody());

                    Query returnedQuery = gson.fromJson(response.getBody(), Query.class);

                    for (Traversal returnedTraversal: returnedQuery.getTraversals().values()) {
                        query.addTraversal(returnedTraversal);
                    }

                } catch (UnirestException e) {
                    e.printStackTrace();
                }


            }
        }

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
            if (!traversal.isComplete() || traversal.getStep() < traversal.getSteps().size()) {
                continue;
            }
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
