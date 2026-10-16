package me.chaos.EldoriaAntiDupe.Serial;

import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

/**
 * Decides which items get a serial number. Only single (amount == 1) items can be tracked,
 * because stacked items are interchangeable by design and cannot be told apart.
 */
public final class ProtectedItemPolicy {

    private final AntiDupeConfig config;

    public ProtectedItemPolicy(AntiDupeConfig config) {
        this.config = config;
    }

    public boolean isProtected(ItemStack item) {
        if (item == null || item.getType().isAir() || item.getAmount() != 1) return false;

        Material type = item.getType();
        boolean byMaterial = config.protectedMaterials.contains(type)
                || (config.protectShulkers && Tag.SHULKER_BOXES.isTagged(type))
                || (config.protectBundles && isBundle(type));
        if (byMaterial && item.getMaxStackSize() == 1) return true;

        if (!item.hasItemMeta()) return false;
        for (NamespacedKey key : item.getItemMeta().getPersistentDataContainer().getKeys()) {
            String full = key.toString();
            for (String prefix : config.protectedKeyPrefixes) {
                if (full.startsWith(prefix)) return true;
            }
        }
        return false;
    }

    private static boolean isBundle(Material type) {
        String name = type.name();
        return name.equals("BUNDLE") || name.endsWith("_BUNDLE");
    }
}
