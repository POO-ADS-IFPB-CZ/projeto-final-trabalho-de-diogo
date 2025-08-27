package br.com.talesgardem.spacefox.pobredinheirinho.io;

import br.com.talesgardem.spacefox.pobredinheirinho.schemas.Schema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class StorageManager<T extends Schema> {

    private final Path filePath;
    private final Gson gson;
    private final Class<T> clazz;

    public StorageManager(String fileName, Class<T> clazz) {
        this.clazz = clazz;

        String userHome = System.getProperty("user.home");
        Path dirPath = Paths.get(userHome, "pobredinheirinho");
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Não foi possível criar o diretório: " + dirPath, e);
            }
        }

        this.filePath = dirPath.resolve(fileName);
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filePath.toFile()))) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                throw new RuntimeException("Não foi possível criar o arquivo: " + this.filePath, e);
            }
        }

        this.gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .setPrettyPrinting()
                .create();
    }

    public void saveAll(List<T> items) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
            String json = gson.toJson(items);
            writer.write(json);
        }
    }

    public List<T> readAll() throws IOException {
        String content = new String(Files.readAllBytes(filePath));
        Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        List<T> items = gson.fromJson(content, listType);
        return items != null ? items : new ArrayList<>();
    }

    public void add(T item) throws IOException {
        List<T> items = readAll();
        items.add(item);
        saveAll(items);
    }

    public Path getFilePath() {
        return filePath;
    }

    public boolean update(T item) throws IOException {
        List<T> items = readAll();
        boolean updated = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                updated = true;
                break;
            }
        }
        if (updated) {
            saveAll(items);
        }
        return updated;
    }

    public boolean delete(String id) throws IOException {
        List<T> items = readAll();
        boolean removed = items.removeIf(item -> item.getId().equals(id));
        if (removed) {
            saveAll(items);
        }
        return removed;
    }
}
