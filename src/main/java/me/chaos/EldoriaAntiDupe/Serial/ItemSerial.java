package me.chaos.EldoriaAntiDupe.Serial;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/** Reads/writes the unique serial number an item carries in its PersistentDataContainer. */
public final class ItemSerial {

    private static NamespacedKey key;

    private ItemSerial() {}

    /** Must be called once before any other method. */
    public static void init(Plugin plugin) {
        key = new NamespacedKey(plugin, "serial");
    }

    public static UUID read(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return null;
        String raw = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (raw == null) return null;
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null; // forged / corrupt value counts as "no serial"
        }
    }

    /** Writes a fresh serial (overwriting any existing one) and returns it. */
    public static UUID assign(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        UUID id = UUID.randomUUID();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id.toString());
        item.setItemMeta(meta);
        return id;
    }

    public static void strip(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().remove(key);
        item.setItemMeta(meta);
    }
}
