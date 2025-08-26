module br.com.talesgardem.spacefox.pobredinheirinho {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.web;


    opens br.com.talesgardem.spacefox.pobredinheirinho to javafx.fxml;
    exports br.com.talesgardem.spacefox.pobredinheirinho;
}