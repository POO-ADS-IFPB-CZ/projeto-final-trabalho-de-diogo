package br.com.talesgardem.spacefox.pobredinheirinho.web;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Router {
    public static final String routesFile = "routes.txt";
    public static final String notFoundPage = "pages/404/index.html";
    private final List<String[]> routes = new LinkedList<>(); // Method, Path, RouteFile

    Router() {
        String[] routesLines = readFile(Router.routesFile).split(";");
        for  (String route : routesLines) {
            String[] components = route.split("\\|");
            if (components.length != 3 || !components[0].contains("\"") || !components[2].contains("\"")) continue;
            String[] splitPath = components[0].split("\"");
            String[] splitFile = components[2].split("\"");
            if (splitFile.length == 0) continue;
            String path = "";
            if (splitPath.length > 1) path = splitPath[1].trim().replaceAll("/[\r\n]+/g", "");
            String method = components[1];
            String file =  splitFile[1].trim().replaceAll("/[\r\n]+/g", "");
            routes.add(new String[]{method, path, file});
        }
    }

    private String readFile(String path) {
        URL filePath = getClass().getResource("/web/" + path);
        if (filePath == null) throw new NullPointerException("File: /web/" + path + " not found!");
        StringBuilder routesData = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.getFile()))) {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                routesData.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return routesData.toString();
    }

    public void sendResponse(BufferedWriter res, int status, String body, String mimeType) throws IOException {
        if (status < 100 || status > 599) throw new RuntimeException("Invalid status provided! " + status);

        int length = body.length();

        if (Objects.equals(mimeType, "js")) mimeType = "javascript";

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        res.write("HTTP/1.0 " + status + " OK\r\n");
        res.write("Date: " + now.format(formatter) + "\r\n");
        res.write("Server: PobreDinheirinhoApp\r\n");
        res.write("Content-Type: text/" + mimeType + "\r\n");
        res.write("Content-Length: " + bytes.length + "\r\n");
        res.write("Access-Control-Allow-Origin: *\r\n");
        res.write("\r\n");
        res.write(body);
    }

    public void handleIncomingRequest(BufferedWriter res, String method, String path) throws IOException {
        String parsedPath = path.toLowerCase();
        if (parsedPath.startsWith("/")) parsedPath = parsedPath.substring(1);
        if (parsedPath.endsWith("/")) parsedPath = parsedPath.substring(0, parsedPath.length() - 1);
        if (!parsedPath.contains(".")) parsedPath = parsedPath + ".html";
        String finalParsedPath = parsedPath;
        System.out.println(finalParsedPath + " <----");
        List<String[]> foundedPath = routes.stream().filter(s -> s[0].equalsIgnoreCase(method)).filter(s -> {
            String sPath = s[1];
            if (!sPath.contains("."))  sPath = sPath + ".html";
            return sPath.equalsIgnoreCase(finalParsedPath);
        }).toList();

        if (foundedPath.isEmpty()) {
            sendResponse(res, 404, readFile(notFoundPage), "html");
            return;
        }

        String file = foundedPath.getFirst()[2];
        System.out.println("file: " + file);
        String MimeType = Arrays.stream(file.split("\\.")).toList().getLast();
        sendResponse(res, 200, readFile(file), MimeType);
    }
}
