package br.com.talesgardem.spacefox.pobredinheirinho.web.routes;

import br.com.talesgardem.spacefox.pobredinheirinho.schemas.GastoFixo;
import br.com.talesgardem.spacefox.pobredinheirinho.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;

public class GastosFixosRoutes extends BaseRoutes {
    public GastosFixosRoutes() {
        this.getRoutes.put("read", this::readData);
    }

    public void readData(String req, BufferedWriter res) throws IOException {
        GastoFixo gasto1 = new GastoFixo();
        gasto1.setId(Utils.getRandomId());
        gasto1.setDescription("Compra 1");
        gasto1.setData(new Date());
        gasto1.setValor(129.32f);

        GastoFixo[] gastosFixos = new GastoFixo[1];
        gastosFixos[0] = gasto1;

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .create();
        String json = gson.toJson(gastosFixos);
        this.writeResponse(res, json);
    }

    public void addData(String req, BufferedWriter res) throws IOException {

    }

    public void editData(String req, BufferedWriter res) throws IOException {

    }

    public void deleteData(String req, BufferedWriter res) throws IOException {

    }
}
