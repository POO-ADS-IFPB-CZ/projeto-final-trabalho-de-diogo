package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

public abstract class BaseRoutes {
    protected HashMap<String, RouteHandler> getRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> postRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> putRoutes = new HashMap<>();
    protected HashMap<String, RouteHandler> deleteRoutes = new HashMap<>();

    public boolean handleRoutes(String req, BufferedWriter res, String Method, String path) {
        RouteHandler route = null;
        if (Method.equalsIgnoreCase("get")) route = getRoutes.get(path);
        else if (Method.equalsIgnoreCase("post")) route  = postRoutes.get(path);
        else  if (Method.equalsIgnoreCase("put")) route = putRoutes.get(path);
        else if (Method.equalsIgnoreCase("delete")) route = deleteRoutes.get(path);
        if (route == null) return false;
        try {
            route.handle(req, res);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @FunctionalInterface
    public interface RouteHandler {
        void handle(String req, BufferedWriter res) throws IOException;
    }

    public String parseRequest(String req) {
        if (req == null) return null;

        // Try CRLF first (\r\n\r\n)
        int index = req.indexOf("\r\n\r\n");
        if (index != -1) return req.substring(index + 4);

        // Try LF only (\n\n)
        index = req.indexOf("\n\n");
        if (index != -1) return req.substring(index + 2);

        return "";
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
}
