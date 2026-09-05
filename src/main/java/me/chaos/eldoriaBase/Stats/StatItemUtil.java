package me.chaos.eldoriaBase.Stats;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Reads/writes stat bonuses stored directly on an {@link ItemStack} via its
 * {@link PersistentDataContainer}. This is what lets armor pieces or a held tool
 * "grant" stats - {@link StatManager} sums these up together with menu-granted bonuses.
 */
public class StatItemUtil {

    private static Plugin plugin;

    /** Must be called once in {@code Main#onEnable()}, before any get/set/removeStat call. */
    public static void init(Plugin owningPlugin) {
        plugin = owningPlugin;
    }

    private static NamespacedKey keyFor(String statId) {
        return new NamespacedKey(plugin, "stat_" + statId);
    }

    public static double getStat(ItemStack item, Stat stat) {
        return getStat(item, stat.getId());
    }

    public static double getStat(ItemStack item, String statId) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return 0.0;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        Double value = pdc.get(keyFor(statId), PersistentDataType.DOUBLE);
        return value == null ? 0.0 : value;
    }

    /** Sets (overwrites) how much of a stat this item grants while equipped/held. */
    public static void setStat(ItemStack item, Stat stat, double value) {
        setStat(item, stat.getId(), value);
    }

    public static void setStat(ItemStack item, String statId, double value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(keyFor(statId), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    public static void removeStat(ItemStack item, Stat stat) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().remove(keyFor(stat.getId()));
        item.setItemMeta(meta);
    }
}