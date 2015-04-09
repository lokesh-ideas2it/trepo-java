package com.github.trepo.server.rest.traversal;

import com.github.trepo.ptree.model.core.PersonModel;
import com.github.trepo.ptree.model.what.BirthModel;
import com.github.trepo.ptree.model.what.DeathModel;
import com.github.trepo.ptree.model.what.NameModel;
import com.github.trepo.ptree.ref.Label;
import com.github.trepo.vgraph.Direction;
import com.github.trepo.vgraph.Node;
import com.github.trepo.vgraph.VGraph;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

        HashMap<String, Object> family = new HashMap<>();

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
        HashSet<PersonModel> parents = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.BIRTH_CHILD_REF)) {
            itr = node.getNodes(Direction.IN, Label.BIRTH_FATHER_REF).iterator();
            if (itr.hasNext()) {
                PersonModel father = new PersonModel(graph, itr.next().getId());
                father.readFromGraph();
                parents.add(father);
            }
            itr = node.getNodes(Direction.IN, Label.BIRTH_MOTHER_REF).iterator();
            if (itr.hasNext()) {
                PersonModel mother = new PersonModel(graph, itr.next().getId());
                mother.readFromGraph();
                parents.add(mother);
            }
        }
        family.put("parents", parents);

        // Get Spouses
        HashSet<PersonModel> spouses = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.MARRIAGE_SPOUSE_REF)) {
            for (Node spouse: node.getNodes(Direction.IN, Label.MARRIAGE_SPOUSE_REF)) {
                if (!person.getId().equals(spouse.getId())) {
                    PersonModel spouseModel = new PersonModel(graph, spouse.getId());
                    spouseModel.readFromGraph();
                    spouses.add(spouseModel);
                }
            }
        }
        family.put("spouses", spouses);

        // Get Children
        HashSet<PersonModel> children = new HashSet<>();
        for (Node node: person.getNodes(Direction.OUT, Label.BIRTH_FATHER_REF, Label.BIRTH_MOTHER_REF)) {
            itr = node.getNodes(Direction.IN, Label.BIRTH_CHILD_REF).iterator();
            if (itr.hasNext()) {
                PersonModel child = new PersonModel(graph, itr.next().getId());
                child.readFromGraph();
                children.add(child);
            }
        }
        family.put("children", children);

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
