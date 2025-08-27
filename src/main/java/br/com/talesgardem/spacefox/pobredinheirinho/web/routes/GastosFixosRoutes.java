package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import br.com.talesgardem.spacefox.pobredinheirinho.io.StorageManager;
import br.com.talesgardem.spacefox.pobredinheirinho.schemas.GastoFixo;
import br.com.talesgardem.spacefox.pobredinheirinho.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

public class GastosFixosRoutes extends BaseRoutes {
    private StorageManager<GastoFixo> storage;

    public GastosFixosRoutes() {
        this.storage = new StorageManager<>("gastoFixo.json", GastoFixo.class);

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
        GastoFixo data = gson.fromJson(req, GastoFixo.class);
        data.setId(Utils.getRandomId());
        storage.add(data);
        this.writeResponse(res, req);
    }

    public void editData(String Path, String req, BufferedWriter res) throws IOException {
        gson.fromJson(req, GastoFixo.class);
        GastoFixo data = gson.fromJson(req, GastoFixo.class);
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
