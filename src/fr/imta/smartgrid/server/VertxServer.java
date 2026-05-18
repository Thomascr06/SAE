package fr.imta.smartgrid.server;

import java.util.Map;

import org.eclipse.persistence.logging.SessionLog;

import fr.imta.smartgrid.server.handlers.GridHandler;
import fr.imta.smartgrid.server.handlers.SensorHandler;
import fr.imta.smartgrid.server.handlers.PersonHandler;
import fr.imta.smartgrid.server.handlers.MeasurementHandler;
import fr.imta.smartgrid.server.handlers.IngressHandler;
import fr.imta.smartgrid.server.handlers.ProducerHandler;
import fr.imta.smartgrid.server.handlers.ConsumerHandler;

import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Sensor;

import fr.imta.smartgrid.model.Producer;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

public class VertxServer {
    private Vertx vertx;
    private EntityManager db; // database object

    public VertxServer() {
        this.vertx = Vertx.vertx();

        // setup database connexion
        Map<String, String> properties = Map.of(
                LOGGING_LEVEL, SessionLog.WARNING_LABEL // change to FINE_LABEL to get details on SQL query to database
        );

        var emf = Persistence.createEntityManagerFactory("smart-grid", properties);
        db = emf.createEntityManager();
    }

    public void start() {
        Router router = Router.router(vertx);

        // add handlers for payload parsing and to allow swagger to send requests
        router.route().handler(BodyHandler.create());
        router.route().handler(
                CorsHandler.create().addOrigin("*").allowedMethod(HttpMethod.DELETE).allowedMethod(HttpMethod.PUT).allowedMethod(HttpMethod.POST));

        // create handlers and registers routes
        GridHandler gh = new GridHandler(db);
        router.get("/grids").handler(gh::getIds);
        router.get("/grid/:id").handler(gh::getById);

        // add methods to GridHandler to handle other grid related routes

        PersonHandler ph = new PersonHandler(db);
        router.get("/persons").handler(ph::getPersons);
        router.get("/person/:id").handler(ph::getPersonById);
        router.put("/person").handler(ph::createPerson);
        router.delete("/person/:id").handler(ph::deletePerson);
        router.post("/person/:id").handler(ph::UpdatePerson);
        // same as GridHandler

        MeasurementHandler mh = new MeasurementHandler(db);
        router.get("/measurements").handler(mh::getMeasurements);
        router.get("/measurement/:id").handler(mh::getMeasurementById);

        SensorHandler sh = new SensorHandler(db);
        router.get("/sensors/:kind").handler(sh::getSensorByKind);
        router.get("/sensor/:id").handler(sh::getSensorDetail);
        

        IngressHandler ih = new IngressHandler(db);
        router.post("/ingress/windturbine").handler(ih::Receivewindturbinemeasurement);
        router.post("/ingress/solarpanel").handler(ih::Receivesolarpanelmeasurement);

        // do the same for other routes
        // ...
        ProducerHandler proh = new ProducerHandler(db);
        router.get("/producers").handler(proh::getProducer);

        ConsumerHandler conh = new ConsumerHandler(db);
        router.get("/consumers").handler(conh::getConsumer);

        // start the server
        vertx.createHttpServer().requestHandler(router).listen(8080)
                .onSuccess(e -> System.out.println("Server is listening on localhost:" + e.actualPort()))
                .onFailure(e -> {
                    System.out.println("Cannot start server, got error: " + e.getLocalizedMessage());
                    System.exit(1);
                });
    }

    public static void main(String[] args) {
        new VertxServer().start();
    }
}
