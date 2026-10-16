package me.chaos.EldoriaAntiDupe.Guards;

import me.chaos.EldoriaAntiDupe.Alert.DupeAlert;
import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import me.chaos.EldoriaAntiDupe.Serial.ItemSerial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 1) Click-rate limit: many lag/macro dupes need an inhuman number of inventory actions per second.
 * 2) Creative: a creative clone copies the item's PDC including its serial. Resetting the serial on
 *    every creative slot-set / clone gives each copy a fresh identity, so admins never trigger
 *    false dupe alerts and creative can't be used to smuggle duplicate serials into survival.
 */
public final class InventoryAbuseListener implements Listener {

    private final JavaPlugin plugin;
    private final AntiDupeConfig config;
    private final DupeAlert alert;
    private final Map<UUID, ArrayDeque<Long>> windows = new HashMap<>();

    public InventoryAbuseListener(JavaPlugin plugin, AntiDupeConfig config, DupeAlert alert) {
        this.plugin = plugin;
        this.config = config;
        this.alert = alert;
    }

    // ------------------------------------------------------------------ click limit

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && exceedsLimit(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player player && exceedsLimit(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        windows.remove(event.getPlayer().getUniqueId());
    }

    private boolean exceedsLimit(Player player) {
        if (!config.clickLimitEnabled || player.hasPermission(config.bypassPermission)) return false;

        long now = System.currentTimeMillis();
        ArrayDeque<Long> window = windows.computeIfAbsent(player.getUniqueId(), id -> new ArrayDeque<>());
        while (!window.isEmpty() && now - window.peekFirst() > 1000L) {
            window.pollFirst();
        }
        window.addLast(now);

        if (window.size() <= config.maxClicksPerSecond) return false;
        alert.rateLimited(player, window.size());
        return true;
    }

    // ------------------------------------------------------------------ creative clones

    /** Slot-set packets from the creative inventory (moving or spawning items). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreativeSet(InventoryCreativeEvent event) {
        if (!config.creativeResetSerial) return;

        ItemStack cursor = event.getCursor();
        if (cursor.getType().isAir() || ItemSerial.read(cursor) == null) return;

        ItemStack clean = cursor.clone();
        ItemSerial.strip(clean);
        event.setCursor(clean);
    }

    /** Middle-click clone in creative: the clone lands on the cursor AFTER this event, so fix it next tick. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreativeClone(InventoryClickEvent event) {
        if (!config.creativeResetSerial || event.getAction() != InventoryAction.CLONE_STACK) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            ItemStack cursor = player.getItemOnCursor();
            if (!cursor.getType().isAir() && ItemSerial.read(cursor) != null) {
                ItemStack clean = cursor.clone();
                ItemSerial.strip(clean);
                player.setItemOnCursor(clean);
            }
        });
    }
}
