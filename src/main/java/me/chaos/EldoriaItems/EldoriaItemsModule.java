package me.chaos.EldoriaItems;

import me.chaos.EldoriaItems.Item.DefaultItemTypes;
import me.chaos.EldoriaItems.Item.EldoriaItemKeys;
import me.chaos.EldoriaItems.Item.Obtain.OreReplacementListener;
import me.chaos.EldoriaItems.Item.Stats.DefaultStatTypes;
import me.chaos.EldoriaItems.Sets.ItemSetHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Wires up the whole custom item system in one call. Add this to Main#onEnable:
 * <pre>{@code
 * EldoriaItemsModule.init(this);
 * }</pre>
 * Call it before any of your own item-loading code (e.g. ExampleItemsLoader.load()),
 * since that code registers items/enchants/stats which need the default stats/types
 * and PDC keys to already exist.
 */
public class EldoriaItemsModule {
    private static ItemSetHandler setHandler;

    public static void init(JavaPlugin plugin) {
        EldoriaItemKeys.init(plugin);

        DefaultItemTypes.registerDefaults();
        DefaultStatTypes.registerDefaults();

        setHandler = new ItemSetHandler();
        setHandler.startPeriodicCheck(plugin);
        Bukkit.getPluginManager().registerEvents(setHandler, plugin);
        Bukkit.getPluginManager().registerEvents(new OreReplacementListener(), plugin);
    }

    public static ItemSetHandler getSetHandler() {
        return setHandler;
    }
}
