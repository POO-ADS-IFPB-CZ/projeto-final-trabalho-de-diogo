package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamicRouter {
    public HashMap<String, BaseRoutes> routes = new HashMap<>();

    public DynamicRouter() {
        routes.put("profits", new LucrosRoutes());
        routes.put("pinExpenses", new GastosFixosRoutes());
        routes.put("varExpenses", new GastosVariaveisRoutes());
    }

    public boolean handleRequest(String req, String method, String path, BufferedWriter res) {
        System.out.println("Request: " + path);
        AtomicBoolean found = new AtomicBoolean(false);
        routes.forEach((s, v) -> {
            String[] splitted = path.split("/");
            if (splitted.length == 3 && splitted[1].equalsIgnoreCase(s)) {
                if (v.handleRoutes(req, res, method, splitted[2])) found.set(true);
            }
        });
        return found.get();
    }
}
