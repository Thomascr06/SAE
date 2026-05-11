package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Measurement;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class MeasurementHandler {
    private EntityManager db;

    public MeasurementHandler(EntityManager db) {
        this.db = db;
    }

    public void getMeasurements(RoutingContext ctx) {
        List<Integer> measurements = db.createNativeQuery("SELECT m.id from measurement as m").getResultList();

        ctx.json(measurements);
    }

    public void getMeasurementById(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.fail(404);
        } else {
            ctx.json(m.toJSON());
        }
    }
}
