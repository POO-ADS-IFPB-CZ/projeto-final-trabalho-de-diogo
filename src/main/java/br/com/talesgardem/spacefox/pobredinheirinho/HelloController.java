package br.com.talesgardem.spacefox.pobredinheirinho;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HelloController {
    @FXML
    private Button HomeButton;

    @FXML
    private Button LucrosButton;

    @FXML
    private Button GastosFixosButton;

    @FXML
    private Button GastosVariaveisButton;

    @FXML
    private WebView webview;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        webEngine = webview.getEngine();
        webEngine.load("http://localhost:8080");
        webEngine.setJavaScriptEnabled(true);
        webEngine.setOnAlert(event -> System.out.println(event.getData()));
        webEngine.setOnError(event -> System.err.println(event.getMessage()));
    }

    @FXML
    protected void onHome() {
        webEngine.load("http://localhost:8080");
    }

    @FXML
    protected void onLucros() {
        webEngine.load("http://localhost:8080/lucros");
    }

    @FXML
    protected void onGastosFixos() {
        webEngine.load("http://localhost:8080/gastosfixos");
    }

    @FXML
    protected void onGastosVariaveis() {
        webEngine.load("http://localhost:8080/gastosVariaveis");
    }
}