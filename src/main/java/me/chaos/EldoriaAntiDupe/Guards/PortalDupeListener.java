package me.chaos.EldoriaAntiDupe.Guards;

import me.chaos.EldoriaAntiDupe.Alert.DupeAlert;
import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

/**
 * Classic dupe family: an entity that carries an inventory (chest minecart/boat, hopper minecart,
 * donkey/mule/llama with chest) is sent through a nether/end portal at a chunk border, and the
 * entity ends up saved in both dimensions/chunks. Such entities simply may not use portals;
 * riders count too, because passengers travel with their vehicle.
 */
public final class PortalDupeListener implements Listener {

    private final AntiDupeConfig config;
    private final DupeAlert alert;

    public PortalDupeListener(AntiDupeConfig config, DupeAlert alert) {
        this.config = config;
        this.alert = alert;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPortal(EntityPortalEvent event) {
        if (!config.portalBlock) return;

        Entity entity = event.getEntity();
        if (carriesInventory(entity)) {
            event.setCancelled(true);
            alert.portalBlocked(entity);
        }
    }

    private boolean carriesInventory(Entity entity) {
        if (entity instanceof StorageMinecart || entity instanceof HopperMinecart || entity instanceof ChestBoat) {
            return true;
        }
        if (entity instanceof ChestedHorse horse && horse.isCarryingChest()) {
            return true;
        }
        for (Entity passenger : entity.getPassengers()) {
            if (carriesInventory(passenger)) return true;
        }
        return false;
    }
}
