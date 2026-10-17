package me.chaos.EldoriaItems.Item;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/** Holds the NamespacedKey(s) used to store item data in each ItemStack's PersistentDataContainer. */
public class EldoriaItemKeys {
    private static NamespacedKey DATA_KEY;

    /** Must be called once from Main#onEnable before anything else in this package is used. */
    public static void init(JavaPlugin plugin) {
        DATA_KEY = new NamespacedKey(plugin, "eldoria_item_data");
    }

    public static NamespacedKey dataKey() {
        if (DATA_KEY == null) {
            throw new IllegalStateException("EldoriaItemKeys.init(plugin) was not called yet! Call it in Main#onEnable.");
        }
        return DATA_KEY;
    }
}
