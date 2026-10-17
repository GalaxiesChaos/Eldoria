package me.chaos.EldoriaDrill;

import me.chaos.EldoriaDrill.command.DrillCommand;
import me.chaos.EldoriaDrill.content.DefaultContent;
import me.chaos.EldoriaDrill.data.DrillKeys;
import me.chaos.EldoriaDrill.listener.MenuListener;
import me.chaos.EldoriaDrill.listener.MiningListener;
import me.chaos.EldoriaDrill.listener.MiningSpeedTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/** Single entry point, same pattern as the other Eldoria modules: call from Main#onEnable. */
public final class DrillModule {

    private static JavaPlugin plugin;

    private DrillModule() {
    }

    public static void init(JavaPlugin owner) {
        init(owner, true);
    }

    /**
     * @param registerDefaults false = skip the example bases/upgrades/fuels and register your own
     *                         through the registries before or right after this call
     */
    public static void init(JavaPlugin owner, boolean registerDefaults) {
        plugin = owner;
        DrillKeys.init(owner);
        if (registerDefaults) {
            DefaultContent.register();
        }
        PluginManager pm = owner.getServer().getPluginManager();
        pm.registerEvents(new MiningListener(), owner);
        pm.registerEvents(new MenuListener(owner), owner);
        DrillCommand.register(owner);
        new MiningSpeedTask().runTaskTimer(owner, 20L, 10L);
    }

    public static JavaPlugin plugin() {
        return plugin;
    }
}
