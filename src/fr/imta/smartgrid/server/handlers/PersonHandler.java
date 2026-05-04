package fr.imta.smartgrid.server.handlers;

import java.util.List;

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
}
