package fr.imta.smartgrid.server.handlers;

import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class PersonHandler {
    private EntityManager db;

    public PersonHandler(EntityManager db) {
        this.db = db;
    }

    public void getPersons(RoutingContext ctx) {
        // TODO
        ctx.json("todo");
    }
}
