package me.chaos.EldoriaAntiDupe.Alert;

import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Reports incidents to the console, to online staff (permission) and to antidupe-incidents.log. */
public final class DupeAlert {

    private final JavaPlugin plugin;
    private final AntiDupeConfig config;
    private final Path incidentLog;
    private final Map<String, Long> throttle = new HashMap<>();

    public DupeAlert(JavaPlugin plugin, AntiDupeConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.incidentLog = plugin.getDataFolder().toPath().resolve("antidupe-incidents.log");
    }

    public void dupeFound(UUID serial, String type, String where, boolean enforced) {
        send("Duplikat erkannt: " + type + " [" + shortId(serial) + "] bei " + where
                + (enforced ? " -> Kopien entfernt (Quarantaene), Original neu nummeriert" : " (nur Log-Modus)"));
    }

    public void revokedRemoved(UUID serial, String type, String where) {
        send("Widerrufene Kopie entfernt: " + type + " [" + shortId(serial) + "] bei " + where);
    }

    public void revokedBlocked(UUID serial, String context) {
        send("Widerrufene Kopie blockiert [" + shortId(serial) + "]: " + context);
    }

    public void rateLimited(Player player, int clicks) {
        if (throttled("rate:" + player.getUniqueId(), 10_000L)) return;
        send("Klick-Limit: " + player.getName() + " hat " + clicks + " Inventar-Aktionen in 1s ausgeloest (geblockt)");
    }

    public void portalBlocked(Entity entity) {
        if (throttled("portal:" + entity.getUniqueId(), 10_000L)) return;
        Location loc = entity.getLocation();
        String world = loc.getWorld() == null ? "?" : loc.getWorld().getName();
        send("Portal-Transfer geblockt: " + entity.getType() + " bei " + world + " "
                + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
    }

    public void circuitBreaker(int removals, int max) {
        send("NOTBREMSE: " + removals + " Entfernungen in einem Scan (Limit " + max
                + ") - es wurde nichts entfernt. Vermutlich ein Fehlalarm, bitte pruefen!");
    }

    private void send(String text) {
        plugin.getLogger().warning("[AntiDupe] " + text);

        Component message = Component.text("[AntiDupe] ", NamedTextColor.RED)
                .append(Component.text(text, NamedTextColor.GRAY));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(config.alertPermission)) {
                player.sendMessage(message);
            }
        }
        append(text);
    }

    private void append(String text) {
        String line = Instant.now() + " " + text + System.lineSeparator();
        Runnable write = () -> {
            try {
                Files.createDirectories(incidentLog.getParent());
                Files.writeString(incidentLog, line, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ignored) {
                // logging must never break gameplay
            }
        };
        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, write);
        } else {
            write.run();
        }
    }

    private boolean throttled(String key, long cooldownMs) {
        long now = System.currentTimeMillis();
        Long last = throttle.get(key);
        if (last != null && now - last < cooldownMs) return true;
        throttle.put(key, now);
        return false;
    }

    private static String shortId(UUID serial) {
        return serial.toString().substring(0, 8);
    }
}
