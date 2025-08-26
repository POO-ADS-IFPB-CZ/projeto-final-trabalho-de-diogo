package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import java.util.HashMap;

public class DynamicRouter {
    public HashMap<String, BaseRoutes> routes = new HashMap<>();

    DynamicRouter() {
        routes.put("profits", new LucrosRoutes());
        routes.put("pinExpenses", new GastosFixosRoutes());
        routes.put("varExpenses", new GastosVariaveisRoutes());
    }
}
