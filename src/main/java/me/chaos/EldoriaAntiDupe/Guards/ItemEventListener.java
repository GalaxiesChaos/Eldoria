package me.chaos.EldoriaAntiDupe.Guards;

import me.chaos.EldoriaAntiDupe.Alert.DupeAlert;
import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import me.chaos.EldoriaAntiDupe.ProtectedGui;
import me.chaos.EldoriaAntiDupe.Serial.DupeScanner;
import me.chaos.EldoriaAntiDupe.Serial.ItemSerial;
import me.chaos.EldoriaAntiDupe.Serial.ProtectedItemPolicy;
import me.chaos.EldoriaAntiDupe.Serial.SerialRegistry;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Event-driven part of the serial system. Hooks are deliberately cheap (no hopper events) - the
 * scanner does the heavy lifting; these only make detection faster at the moments dupes happen.
 */
public final class ItemEventListener implements Listener {

    private final AntiDupeConfig config;
    private final SerialRegistry registry;
    private final ProtectedItemPolicy policy;
    private final DupeScanner scanner;
    private final DupeAlert alert;

    public ItemEventListener(AntiDupeConfig config, SerialRegistry registry, ProtectedItemPolicy policy,
                             DupeScanner scanner, DupeAlert alert) {
        this.config = config;
        this.registry = registry;
        this.policy = policy;
        this.scanner = scanner;
        this.alert = alert;
    }

    /** Every dropped/spawned protected item gets a serial; revoked ones never enter the world. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item entity = event.getEntity();
        ItemStack stack = entity.getItemStack();

        UUID serial = ItemSerial.read(stack);
        if (serial != null) {
            if (config.enforce() && registry.isRevoked(serial)) {
                event.setCancelled(true);
                alert.revokedBlocked(serial, "Item-Spawn");
            }
            return;
        }

        if (policy.isProtected(stack)) {
            ItemSerial.assign(stack);
            entity.setItemStack(stack);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!config.enforce()) return;

        UUID serial = ItemSerial.read(event.getItem().getItemStack());
        if (serial == null || !registry.isRevoked(serial)) return;

        event.setCancelled(true);
        event.getItem().remove();
        alert.revokedBlocked(serial, "Aufheben durch " + event.getEntity().getName());
    }

    /**
     * Stamp BEFORE other plugins compute drops/keep-inventory copies, so a copy that both drops and
     * stays in the inventory (classic death dupe) carries the same serial and gets caught.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        scanner.stampPlayer(event.getEntity());
        scanner.requestScan();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        scanner.requestScan();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder(false) instanceof ProtectedGui) return;
        scanner.requestScan();
    }
}
