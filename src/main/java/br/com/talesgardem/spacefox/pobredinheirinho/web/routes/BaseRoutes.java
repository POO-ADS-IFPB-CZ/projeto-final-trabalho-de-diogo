package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class BaseRoutes {
    protected HashMap<String, RouteHandler> getRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> postRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> putRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> deleteRoutes = new HashMap<>();

    protected Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();

    public boolean handleRoutes(String req, BufferedWriter res, String Method, String path) {
        AtomicReference<RouteHandler> route = new AtomicReference<>();
        if (Method.equalsIgnoreCase("get")) route.set(getRoutes.get(path));
        else if (Method.equalsIgnoreCase("post")) route.set(postRoutes.get(path));
        else  if (Method.equalsIgnoreCase("put")) route.set(putRoutes.get(path));
        else if (Method.equalsIgnoreCase("delete")) route.set(deleteRoutes.get(path));

        // routes with *
        if (route.get() == null) {
            if (Method.equalsIgnoreCase("get")) getRoutes.forEach((key, value) -> { if (key.contains("-*") && key.split("-")[0].equalsIgnoreCase(path.split("-")[0])) route.set(value); });
            else if (Method.equalsIgnoreCase("post")) postRoutes.forEach((key, value) -> { if (key.contains("-*") && key.split("-")[0].equalsIgnoreCase(path.split("-")[0])) route.set(value); });
            else if (Method.equalsIgnoreCase("put")) putRoutes.forEach((key, value) -> { if (key.contains("-*") && key.split("-")[0].equalsIgnoreCase(path.split("-")[0])) route.set(value); });
            else if (Method.equalsIgnoreCase("delete")) deleteRoutes.forEach((key, value) -> { if (key.contains("-*") && key.split("-")[0].equalsIgnoreCase(path.split("-")[0])) route.set(value); });
        }

        if (route.get() == null) return false;
        try {
            route.get().handle(path, req, res);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @FunctionalInterface
    public interface RouteHandler {
        void handle(String Path, String req, BufferedWriter res) throws IOException;
    }

    public void writeResponse(BufferedWriter res, String body) throws IOException {
        int length = body.length();

        LocalDateTime now = LocalDateTime.now();
        res.write("HTTP/1.0 200 OK\r\n");
        res.write("Date: " + now + "\r\n");
        res.write("Server: PobreDinheirinhoApp\r\n");
        res.write("Content-Type: application/json\r\n");
        res.write("Content-Length: " + length + "\r\n");
        res.write("\r\n");
        res.write(body);
    }

    protected String getIdFromPath(String Path) {
        String[] idParts = Path.split("-");
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < idParts.length; i++) {
            if (i == 0) continue;
            id.append(idParts[i]);
            if (i != idParts.length - 1) id.append("-");
        }
        return id.toString();
    }
}
