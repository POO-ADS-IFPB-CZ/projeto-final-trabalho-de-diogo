package br.com.talesgardem.spacefox.pobredinheirinho.web;

import br.com.talesgardem.spacefox.pobredinheirinho.web.routes.DynamicRouter;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final int THREAD_POOL_SIZE = 10;
    private final int port;
    private final Router router;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private DynamicRouter dynamicRouter;

    public HttpServer(int port) {
        this.port = port;
        this.router = new Router();
        this.dynamicRouter = new DynamicRouter();
    }

    public void start() {
        running = true;
        try (ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("Servidor parado.");
    }

    void handleClient(Socket clientSocket) {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            // 1. Lendo a primeira linha: METHOD PATH HTTP/1.1
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) return; // requisição inválida

            String method = requestParts[0];
            String path = requestParts[1];

            // 2. Lendo headers e extraindo Content-Length
            String line;
            int contentLength = 0;
            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(" ")[1]);
                }
            }

            // 3. Lendo o body exato
            char[] bodyChars = new char[contentLength];
            int read = 0;
            while (read < contentLength) {
                int r = in.read(bodyChars, read, contentLength - read);
                if (r == -1) break;
                read += r;
            }
            String body = new String(bodyChars);

            if (dynamicRouter.handleRequest(body, method, path, out)) return;
            router.handleIncomingRequest(out, method, path);
        } catch (IOException e) {
            System.out.println("Error closing socket:");
            e.printStackTrace();
        }
    }
}
