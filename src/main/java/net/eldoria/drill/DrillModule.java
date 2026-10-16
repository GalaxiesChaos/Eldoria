package net.eldoria.drill;

import net.eldoria.drill.command.DrillCommand;
import net.eldoria.drill.content.DefaultContent;
import net.eldoria.drill.data.DrillKeys;
import net.eldoria.drill.listener.MenuListener;
import net.eldoria.drill.listener.MiningListener;
import net.eldoria.drill.listener.MiningSpeedTask;
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
