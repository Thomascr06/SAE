package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import fr.imta.smartgrid.model.Sensor;
import fr.imta.smartgrid.model.WindTurbine;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class IngressHandler {
    private EntityManager db;

        public IngressHandler(EntityManager db) {
            this.db = db;
        }

        public void Receivewindturbinemeasurement(RoutingContext ctx) {
            try {
        JsonObject body = ctx.body().asJsonObject();
        int turbineId = body.getInteger("windturbine");
        
        WindTurbine turbine = db.find(WindTurbine.class, turbineId);

        if (turbine == null) {
            ctx.response().setStatusCode(404)
               .putHeader("content-type", "application/json")
               .end("\"Wind turbine not found\"");
            return;
        }

        JsonObject data = body.getJsonObject("data");
        double power = data.getDouble("power");
        double speed = data.getDouble("speed");
        long timestamp = body.getLong("timestamp");

        Measurement powerMeas = null;
        Measurement speedMeas = null;
        Measurement energyMeas = null;

        
        for (Measurement m : turbine.getMeasurements()) {
            if (m.getName().equals("power")) powerMeas = m;
            if (m.getName().equals("speed")) speedMeas = m;
            if (m.getName().equals("total_energy_produced")) energyMeas = m;
        }

        db.getTransaction().begin();

        
        if (powerMeas != null) {
            DataPoint dpPower = new DataPoint();
            dpPower.setMeasurement(powerMeas);
            dpPower.setTimestamp(timestamp);
            dpPower.setValue(power);
            db.persist(dpPower); 
        }

        if (speedMeas != null) {
            DataPoint dpSpeed = new DataPoint();
            dpSpeed.setMeasurement(speedMeas);
            dpSpeed.setTimestamp(timestamp);
            dpSpeed.setValue(speed);
            db.persist(dpSpeed);
        }

        if (energyMeas != null) {
            double currentTotalEnergy = 0.0;
            
            try {
                Double lastValue = (Double) db.createNativeQuery(
                        "SELECT d.value FROM datapoint d WHERE d.measurement = ? ORDER BY d.timestamp DESC LIMIT 1")
                        .setParameter(1, energyMeas.getId())
                        .getSingleResult();
                if (lastValue != null) {
                    currentTotalEnergy = lastValue;
                }
            } catch (Exception noResult) {
                
                currentTotalEnergy = 0.0;
            }

            double addedEnergy = power * 60.0;
            double newTotalEnergy = currentTotalEnergy + addedEnergy;
        
            DataPoint dpEnergy = new DataPoint();
            dpEnergy.setMeasurement(energyMeas);
            dpEnergy.setTimestamp(timestamp);
            dpEnergy.setValue(newTotalEnergy);
            db.persist(dpEnergy);
        }

        db.getTransaction().commit();

        
        ctx.response().setStatusCode(200)
           .putHeader("content-type", "text/plain")
           .end("success");

    } catch (Exception e) {
        if (db.getTransaction().isActive()) {
            db.getTransaction().rollback();
        }
        ctx.response().setStatusCode(500)
           .putHeader("content-type", "application/json")
           .end("\"Invalid JSON payload\"");
    }
        }

    }



