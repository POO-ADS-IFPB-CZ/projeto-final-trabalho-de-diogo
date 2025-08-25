package br.com.talesgardem.spacefox.pobredinheirinho.web;

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

    public HttpServer(int port) {
        this.port = port;
        this.router = new Router();
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
            StringBuilder clientInputLine = new StringBuilder();
            while (true) {
                String content = in.readLine();
                if (content == null || content.isEmpty()) break; // end of message
                clientInputLine.append(content);
            }
            if (clientInputLine.isEmpty()) return;
            String[] parts = clientInputLine.toString().split(" ");
            if (parts.length < 2) return; // invalid message
            String method = parts[0];
            String path = parts[1];

            router.handleIncomingRequest(out, method, path);
        } catch (IOException e) {
            System.out.println("Error closing socket:");
            e.printStackTrace();
        }
    }
}
