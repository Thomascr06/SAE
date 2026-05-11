package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.json.JsonObject;
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

    public void createPerson(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        String firstName = body.getString("first_name");
        String lastName = body.getString("last_name");

        if (firstName == null || firstName.trim().isEmpty()) {
            ctx.response().setStatusCode(500);
            ctx.json("Missing first_name");
            return;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            ctx.response().setStatusCode(500);
            ctx.json("Missing last_name");
            return;
        }

        this.db.getTransaction().begin();
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);

        Integer gridId = body.getInteger("grid");
        if (gridId != null) {
            Grid grid = this.db.find(Grid.class, gridId);
            if (grid != null) {
                p.setGrid(grid);
            } else {
                ctx.fail(404);
                this.db.getTransaction().rollback();
                return;
            }
        }

        this.db.persist(p);
        this.db.getTransaction().commit();
        JsonObject response = new JsonObject().put("id", p.getId());
        ctx.json(response);
    }

    public void deletePerson(RoutingContext ctx) {
        this.db.getTransaction().begin();
        Person p = db.find(Person.class, Integer.parseInt(ctx.pathParam("id")));
        if (p == null) {
            this.db.getTransaction().rollback();
            ctx.response().setStatusCode(404);
            ctx.json("Person not found");
            return;
        }

        try {
            db.remove(p);
            this.db.getTransaction().commit();
            ctx.json("Deleted successfully");
        } catch (Exception e) {
            this.db.getTransaction().rollback();
            ctx.response().setStatusCode(500);
            ctx.json("Error during deletion");
        }
    }
}
