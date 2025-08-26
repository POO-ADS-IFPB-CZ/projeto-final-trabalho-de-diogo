package br.com.talesgardem.spacefox.pobredinheirinho;

import br.com.talesgardem.spacefox.pobredinheirinho.web.HttpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private HttpServer server;

    public void StartSocket() {
        new Thread(() -> {
            int port = 8080;
            server = new HttpServer(port);
            server.start();
        }).start();
    }

    @Override
    public void start(Stage stage) throws IOException {
        StartSocket();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Pobre Dinheirinho");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            t.consume();

            if (server != null) {
                try {
                    server.stop();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            stage.close();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) { launch(); }
}