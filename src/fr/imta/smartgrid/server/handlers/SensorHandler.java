package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class SensorHandler {
    private EntityManager db;

    public SensorHandler(EntityManager db) {
        this.db = db;
    }


  
    public void getSensorByKind(RoutingContext ctx) {
        String kind = ctx.pathParam("kind");

        @SuppressWarnings("unchecked")
        List<Integer> ids = db.createNativeQuery("SELECT s.id from sensor as s where s.dtype = ?")
                .setParameter(1, kind)
                .getResultList();

        ctx.json(ids);
    }

    public void getSensorDetail(RoutingContext ctx){
                Sensor s = db.find(Sensor.class, Integer.parseInt(ctx.pathParam("id")));
                if (s == null) {
                ctx.response().setStatusCode(404)
               .putHeader("content-type", "application/json")
               .end("\"Sensor not found\"");
            return;
                 } else {
                    ctx.json(s.toJSON());
                }        
    }
}