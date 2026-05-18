package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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

    @SuppressWarnings("unchecked")
    public void getMeasurementValues(RoutingContext ctx) {
        Measurement m = db.find(Measurement.class, Integer.parseInt(ctx.pathParam("id")));
        if (m == null) {
            ctx.response().setStatusCode(404)
                .putHeader("Content-Type", "application/json")
                .end("{\"error\":\"Measurement not found\"}");
            return;
        }

        String fromParam = ctx.request().getParam("from");
        String toParam = ctx.request().getParam("to");

        long from = (fromParam != null) ? Long.parseLong(fromParam) : 0L;
        long to = (toParam != null) ? Long.parseLong(toParam) : 2147483646L;

        List<DataPoint> points = (List<DataPoint>) db.createNativeQuery(
            "SELECT * FROM datapoint WHERE measurement = ? AND timestamp >= ? AND timestamp <= ?", DataPoint.class)
                .setParameter(1, m.getId())
                .setParameter(2, from)
                .setParameter(3, to)
                .getResultList();
                
        JsonObject responseJson = new JsonObject();
        responseJson.put("measurement_id", m.getId());

        JsonArray dataArray = new JsonArray();
        for (DataPoint dp : points) {
            JsonObject pointJson = new JsonObject();
            pointJson.put("timestamp", dp.getTimestamp());
            pointJson.put("value", dp.getValue());
            dataArray.add(pointJson);
        }
        responseJson.put("values", dataArray);

        ctx.json(responseJson);
    
        }

    
    }


