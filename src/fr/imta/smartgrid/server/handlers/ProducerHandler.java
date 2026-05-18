package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Producer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ProducerHandler {
    private EntityManager db;

    public ProducerHandler(EntityManager db) {
        this.db = db;
    }

    public void getProducer(RoutingContext ctx) {
        List<Producer> producers= db.createQuery("SELECT p FROM Producer p",Producer.class).getResultList();
        JsonArray array = new JsonArray();
        for (Producer producer : producers) {
            array.add(producer.toJSON());
        }
        ctx.response().setStatusCode(200);
        ctx.json(array);
        
    }


}
