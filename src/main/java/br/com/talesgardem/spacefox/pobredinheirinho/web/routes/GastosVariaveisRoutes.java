package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import br.com.talesgardem.spacefox.pobredinheirinho.io.StorageManager;
import br.com.talesgardem.spacefox.pobredinheirinho.schemas.GastoVariavel;
import br.com.talesgardem.spacefox.pobredinheirinho.util.Utils;

import java.io.BufferedWriter;
import java.io.IOException;

public class GastosVariaveisRoutes extends BaseRoutes {
    private StorageManager<GastoVariavel> storage;

    public GastosVariaveisRoutes() {
        this.storage = new StorageManager<>("gastoVariavel.json", GastoVariavel.class);

        this.getRoutes.put("read", this::readData);
        this.postRoutes.put("create", this::addData);
        this.putRoutes.put("update-*", this::editData);
        this.deleteRoutes.put("delete-*", this::deleteData);
    }

    public void readData(String Path, String req, BufferedWriter res) throws IOException {
        String json = gson.toJson(storage.readAll());
        this.writeResponse(res, json);
    }

    public void addData(String Path, String req, BufferedWriter res) throws IOException {
        GastoVariavel data = gson.fromJson(req, GastoVariavel.class);
        data.setId(Utils.getRandomId());
        storage.add(data);
        this.writeResponse(res, req);
    }

    public void editData(String Path, String req, BufferedWriter res) throws IOException {
        gson.fromJson(req, GastoVariavel.class);
        GastoVariavel data = gson.fromJson(req, GastoVariavel.class);
        System.out.println(getIdFromPath(Path));
        data.setId(getIdFromPath(Path));
        boolean result = storage.update(data);
        System.out.println(result);
        this.writeResponse(res, req);
    }

    public void deleteData(String Path, String req, BufferedWriter res) throws IOException {
        storage.delete(getIdFromPath(Path));
        this.writeResponse(res, "{\"ok\": true}");
    }
}
