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

    public void getSensorDetail(RoutingContext ctx) {
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

    public void updateSensor(RoutingContext ctx) {
        String idStr = ctx.pathParam("id");
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("\"Invalid ID\"");
            return;
        }

        io.vertx.core.json.JsonObject body = ctx.body().asJsonObject();
        if (body == null) {
            ctx.response().setStatusCode(400).end("\"Invalid JSON\"");
            return;
        }

        db.getTransaction().begin();
        Sensor s = db.find(Sensor.class, id);
        if (s == null) {
            db.getTransaction().rollback();
            ctx.response().setStatusCode(404)
                    .putHeader("content-type", "application/json")
                    .end("\"Sensor not found\"");
            return;
        }

        if (body.containsKey("name"))
            s.setName(body.getString("name"));
        if (body.containsKey("description"))
            s.setDescription(body.getString("description"));

        if (body.containsKey("owners")) {
            io.vertx.core.json.JsonArray ownersArray = body.getJsonArray("owners");
            s.getOwners().clear();
            for (int i = 0; i < ownersArray.size(); i++) {
                Person p = db.find(Person.class, ownersArray.getInteger(i));
                if (p != null) {
                    s.getOwners().add(p);
                }
            }
        }

        if (s instanceof fr.imta.smartgrid.model.Producer) {
            if (body.containsKey("power_source"))
                ((fr.imta.smartgrid.model.Producer) s).setPowerSource(body.getString("power_source"));
        }
        if (s instanceof fr.imta.smartgrid.model.Consumer) {
            if (body.containsKey("max_power"))
                ((fr.imta.smartgrid.model.Consumer) s).setMaxPower(body.getDouble("max_power"));
        }
        if (s instanceof fr.imta.smartgrid.model.WindTurbine) {
            if (body.containsKey("height"))
                ((fr.imta.smartgrid.model.WindTurbine) s).setHeight(body.getDouble("height"));
            if (body.containsKey("blade_length"))
                ((fr.imta.smartgrid.model.WindTurbine) s).setBladeLength(body.getDouble("blade_length"));
        }
        if (s instanceof fr.imta.smartgrid.model.SolarPanel) {
            if (body.containsKey("efficiency"))
                ((fr.imta.smartgrid.model.SolarPanel) s).setEfficiency(body.getFloat("efficiency"));
        }
        if (s instanceof fr.imta.smartgrid.model.EVCharger) {
            if (body.containsKey("type"))
                ((fr.imta.smartgrid.model.EVCharger) s).setType(body.getString("type"));
            if (body.containsKey("maxAmp"))
                ((fr.imta.smartgrid.model.EVCharger) s).setMaxAmp(body.getInteger("maxAmp"));
            if (body.containsKey("voltage"))
                ((fr.imta.smartgrid.model.EVCharger) s).setVoltage(body.getInteger("voltage"));
        }

        db.merge(s);
        db.getTransaction().commit();

        ctx.response().setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(s.toJSON().encode());
    }
}