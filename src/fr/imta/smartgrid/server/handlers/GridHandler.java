package fr.imta.smartgrid.server.handlers;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GridHandler {
    private EntityManager db;

    public GridHandler(EntityManager db) {
        this.db = db;
    }

    public void getGrids(RoutingContext ctx) {
        List<Integer> grids = db.createNativeQuery("SELECT g.id from grid as g").getResultList();

        ctx.json(grids);
    }
    public void getGrids(RoutingContext ctx){
        grid = db.find(Grid.classInteger.parseInt(ctx.pathParam("id")));
        if (g == null) {
            ctx.fail(404);
        } else {
            ctx.json(g.toJSON());
        }
    }
}
