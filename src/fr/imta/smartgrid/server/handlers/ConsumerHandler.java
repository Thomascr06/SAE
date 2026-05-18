package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Consumer;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ConsumerHandler {
    private EntityManager db;

    public ConsumerHandler(EntityManager db) {
        this.db = db;
    }

    public void getConsumer(RoutingContext ctx) {
        List<Consumer> consumers = db.createQuery("SELECT c FROM Consumer c", Consumer.class).getResultList();
        JsonArray array = new JsonArray();
        for (Consumer consumer : consumers) {
            array.add(consumer.toJSON());
        }
        ctx.response().setStatusCode(200);
        ctx.json(array);
    }
}
