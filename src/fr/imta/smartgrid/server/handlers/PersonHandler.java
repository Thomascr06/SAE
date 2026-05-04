package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class PersonHandler {
    private EntityManager db;

    public PersonHandler(EntityManager db) {
        this.db = db;
    }

    public void getPersons(RoutingContext ctx) {
        List<Integer> persons = db.createNativeQuery("SELECT p.id from person as p").getResultList();

        ctx.json(persons);
    }
    public void getPersonById(RoutingContext ctx) {
        Person g = db.find(Person.class, Integer.parseInt(ctx.pathParam("id")));
        if (g == null) {
            ctx.fail(404);
        } else {
            ctx.json(g.toJSON());
        }
    }
}
