package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import java.io.BufferedWriter;
import java.io.IOException;

public class GastosFixosRoutes extends BaseRoutes {
    public GastosFixosRoutes() {
        this.getRoutes.put("read", this::readData);
    }

    public void readData(String req, BufferedWriter res) throws IOException {
        System.out.println(req);
    }
}
