package me.chaos.EldoriaDrops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Single entry point of the drop system - call once from {@code Main#onEnable()},
 * after the stats have been loaded:
 * <pre>DropModule.init(this);</pre>
 */
public final class DropModule {

    private DropModule() {}

    public static void init(Plugin plugin) {
        BlockTagLoader.loadTags();   // 1) which block belongs to which tag
        DropLoader.loadDrops();      // 2) which tag drops what
        Bukkit.getPluginManager().registerEvents(new DropListener(), plugin);
    }
}
