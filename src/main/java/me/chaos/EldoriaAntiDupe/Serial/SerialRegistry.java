package me.chaos.EldoriaAntiDupe.Serial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Keeps (1) every revoked serial - any item still carrying one is a leftover copy of a detected
 * dupe and gets deleted wherever it shows up - and (2) a quarantine of removed items so admins can
 * restore false positives. Everything is touched from the main thread only; only the final file
 * write runs on a background thread, using a prebuilt JSON string.
 */
public final class SerialRegistry {

    public record QuarantineEntry(int id, long time, String serial, String label, String type, String data) {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int MAX_QUARANTINE = 200;
    private static final int MAX_TRACKED_HOLDERS = 50_000;

    private final JavaPlugin plugin;
    private final Path file;
    private final Set<UUID> revoked = new HashSet<>();
    private final Deque<QuarantineEntry> quarantine = new ArrayDeque<>();
    private final Map<UUID, String> lastHolder = new LinkedHashMap<UUID, String>(1024, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, String> eldest) {
            return size() > MAX_TRACKED_HOLDERS;
        }
    };
    private final ExecutorService io = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "EldoriaAntiDupe-IO");
        thread.setDaemon(true);
        return thread;
    });

    private int nextId = 1;
    private boolean dirty;

    public SerialRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = plugin.getDataFolder().toPath().resolve("antidupe-registry.json");
    }

    // ------------------------------------------------------------------ revoked serials

    public boolean isRevoked(UUID serial) {
        return revoked.contains(serial);
    }

    public void revoke(UUID serial) {
        if (revoked.add(serial)) dirty = true;
    }

    public int revokedCount() {
        return revoked.size();
    }

    // ------------------------------------------------------------------ last known holder (RAM only)

    public void noteHolder(UUID serial, String holderKey) {
        lastHolder.put(serial, holderKey);
    }

    public String lastHolder(UUID serial) {
        return lastHolder.get(serial);
    }

    // ------------------------------------------------------------------ quarantine

    public void quarantine(ItemStack item, UUID serial, String label) {
        try {
            String data = Base64.getEncoder().encodeToString(item.serializeAsBytes());
            quarantine.addLast(new QuarantineEntry(nextId++, System.currentTimeMillis(),
                    serial.toString(), label, item.getType().name(), data));
            while (quarantine.size() > MAX_QUARANTINE) quarantine.removeFirst();
            dirty = true;
        } catch (Exception e) {
            plugin.getLogger().warning("[AntiDupe] Quarantaene fehlgeschlagen: " + e.getMessage());
        }
    }

    public List<QuarantineEntry> getQuarantine() {
        return List.copyOf(quarantine);
    }

    /** Removes the entry (so it can only be restored once!) and returns the item, or null. */
    public ItemStack takeQuarantine(int id) {
        for (Iterator<QuarantineEntry> it = quarantine.iterator(); it.hasNext(); ) {
            QuarantineEntry entry = it.next();
            if (entry.id() != id) continue;
            try {
                ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(entry.data()));
                it.remove();
                dirty = true;
                return item;
            } catch (Exception e) {
                plugin.getLogger().warning("[AntiDupe] Quarantaene-Eintrag " + id + " nicht lesbar: " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ persistence

    public void load() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            if (root.has("revoked")) {
                for (JsonElement element : root.getAsJsonArray("revoked")) {
                    try {
                        revoked.add(UUID.fromString(element.getAsString()));
                    } catch (IllegalArgumentException ignored) {
                        // skip broken entry
                    }
                }
            }
            if (root.has("quarantine")) {
                for (JsonElement element : root.getAsJsonArray("quarantine")) {
                    JsonObject o = element.getAsJsonObject();
                    quarantine.addLast(new QuarantineEntry(
                            o.get("id").getAsInt(), o.get("time").getAsLong(), o.get("serial").getAsString(),
                            o.get("label").getAsString(), o.get("type").getAsString(), o.get("data").getAsString()));
                }
            }
            if (root.has("nextId")) nextId = root.get("nextId").getAsInt();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "[AntiDupe] Registry beschaedigt - wird gesichert und neu angelegt", e);
            try {
                Files.move(file, file.resolveSibling("antidupe-registry.corrupt-" + System.currentTimeMillis() + ".json"));
            } catch (IOException ignored) {
                // nothing more we can do
            }
        }
    }

    /** Builds the JSON on the main thread, writes it on the IO thread. No-op if nothing changed. */
    public void saveAsync() {
        if (!dirty) return;
        String json = buildJson();
        dirty = false;
        io.execute(() -> write(json));
    }

    /** Synchronous final save for onDisable. */
    public void saveNow() {
        String json = buildJson();
        io.shutdown();
        try {
            io.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        write(json);
    }

    private String buildJson() {
        JsonObject root = new JsonObject();

        JsonArray revokedArray = new JsonArray();
        for (UUID id : revoked) revokedArray.add(id.toString());
        root.add("revoked", revokedArray);

        JsonArray quarantineArray = new JsonArray();
        for (QuarantineEntry entry : quarantine) {
            JsonObject o = new JsonObject();
            o.addProperty("id", entry.id());
            o.addProperty("time", entry.time());
            o.addProperty("serial", entry.serial());
            o.addProperty("label", entry.label());
            o.addProperty("type", entry.type());
            o.addProperty("data", entry.data());
            quarantineArray.add(o);
        }
        root.add("quarantine", quarantineArray);
        root.addProperty("nextId", nextId);

        return GSON.toJson(root);
    }

    private void write(String json) {
        try {
            Files.createDirectories(file.getParent());
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, json, StandardCharsets.UTF_8);
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "[AntiDupe] Registry konnte nicht gespeichert werden", e);
        }
    }
}
