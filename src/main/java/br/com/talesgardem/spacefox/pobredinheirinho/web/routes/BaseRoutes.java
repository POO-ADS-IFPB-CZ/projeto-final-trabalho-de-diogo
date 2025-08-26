package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import java.util.HashMap;

public abstract class BaseRoutes {
    private HashMap<String, Runnable> getRoutes = new HashMap<>();
    private HashMap<String, Runnable> postRoutes = new HashMap<>();
    private HashMap<String, Runnable> putRoutes = new HashMap<>();
    private HashMap<String, Runnable> deleteRoutes = new HashMap<>();

    public boolean handleRoutes(String Method, String path) {
        Runnable route = null;
        if (Method.equalsIgnoreCase("get")) route = getRoutes.get(path);
        else if (Method.equalsIgnoreCase("post")) route  = postRoutes.get(path);
        else  if (Method.equalsIgnoreCase("put")) route = putRoutes.get(path);
        else if (Method.equalsIgnoreCase("delete")) route = deleteRoutes.get(path);
        if (route == null) return false;
        route.run();
        return true;
    }
}
