module br.com.talesgardem.spacefox.pobredinheirinho {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.web;
    requires com.google.gson;


    opens br.com.talesgardem.spacefox.pobredinheirinho to javafx.fxml;
    opens br.com.talesgardem.spacefox.pobredinheirinho.schemas to com.google.gson;
    exports br.com.talesgardem.spacefox.pobredinheirinho;
}