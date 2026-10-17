package me.chaos.EldoriaDrill.data;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/** PDC keys. Call {@link #init(JavaPlugin)} once on startup. */
public final class DrillKeys {

    /** Drill instance id (UUID string). Cheap to read, used to identify the held drill. */
    public static NamespacedKey ID;
    /** Drill state as JSON (delta only: base id, fuel, installed upgrade items). */
    public static NamespacedKey DATA;
    /** Upgrade/module template id on upgrade items. */
    public static NamespacedKey UPGRADE;

    private DrillKeys() {
    }

    public static void init(JavaPlugin plugin) {
        ID = new NamespacedKey(plugin, "drill_id");
        DATA = new NamespacedKey(plugin, "drill_data");
        UPGRADE = new NamespacedKey(plugin, "drill_upgrade");
    }
}
