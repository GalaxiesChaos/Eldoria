package me.chaos.eldoriaBase.Utils;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ReadWriteHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void Write(JsonElement data, String Path) {
        writeJson(Path + ".json", data);
    }

    public static JsonElement Read(String Path) {
        return readJson(Path + ".json");
    }

    private static void writeJson(String filePath, JsonElement data) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("Konnte Ordner nicht erstellen: " + parent.getAbsolutePath());
        }

        // FIXED: always overwrite. The old `if (!file.exists())` guard froze every file
        // after its first save, causing permanent data loss and rollback dupes.
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            throw new RuntimeException("Konnte Datei nicht schreiben: " + filePath, e);
        }
    }

    private static JsonElement readJson(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return new JsonObject();

        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            JsonElement el = JsonParser.parseReader(r);
            // JsonNull/empty guard: getAsJsonObject() on JsonNull crashed PlayerJoinListener
            return (el == null || el.isJsonNull() || !el.isJsonObject()) ? new JsonObject() : el;
        } catch (IOException | JsonParseException e) {
            throw new RuntimeException("Konnte Datei nicht lesen: " + filePath, e);
        }
    }
}
