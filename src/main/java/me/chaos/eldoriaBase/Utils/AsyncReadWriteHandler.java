package me.chaos.eldoriaBase.Utils;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * AsyncReadWriteHandler provides asynchronous JSON read/write helpers.
 * Methods return CompletableFuture so callers can chain or handle completion
 * without blocking the main server thread.
 */
public class AsyncReadWriteHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Simple async helpers using the common ForkJoinPool. These are safe for file I/O
    // because they run off the main thread. Callers should handle completion/exception.

    public static CompletableFuture<Void> writeAsync(JsonElement data, String path) {
        return CompletableFuture.runAsync(() -> writeJsonSync(path + ".json", data));
    }

    public static CompletableFuture<JsonElement> readAsync(String path) {
        return CompletableFuture.supplyAsync(() -> readJsonSync(path + ".json"));
    }

    // Allow callers to supply a custom executor (recommended for production/server pools)
    public static CompletableFuture<Void> writeAsync(JsonElement data, String path, Executor executor) {
        return CompletableFuture.runAsync(() -> writeJsonSync(path + ".json", data), executor);
    }

    public static CompletableFuture<JsonElement> readAsync(String path, Executor executor) {
        return CompletableFuture.supplyAsync(() -> readJsonSync(path + ".json"), executor);
    }

    // Synchronous helpers used internally. These are the same logic as a blocking impl,
    // but are invoked from an async task in the public API.
    private static void writeJsonSync(String filePath, JsonElement data) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs() && !parent.exists()) {
                throw new RuntimeException("Failed to create directories for path: " + parent.getAbsolutePath());
            }
        }

        // Always write/overwrite the file (fixes bug in the original handler which only wrote when file didn't exist).
        try (FileOutputStream fos = new FileOutputStream(file, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter bufferedWriter = new BufferedWriter(osw)) {

            GSON.toJson(data, bufferedWriter);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonElement readJsonSync(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            return new JsonObject();
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(isr)) {

            return JsonParser.parseReader(bufferedReader);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
