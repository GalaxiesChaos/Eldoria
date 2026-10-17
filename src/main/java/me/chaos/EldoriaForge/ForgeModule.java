package me.chaos.EldoriaForge;

import me.chaos.EldoriaForge.data.PlayerForgeData;
import me.chaos.EldoriaForge.listener.ForgeMenuListener;
import me.chaos.EldoriaForge.recipe.DefaultRecipes;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/** Single entry point, same pattern as the other Eldoria modules: call from Main#onEnable. */
public final class ForgeModule {

    private static JavaPlugin plugin;

    private ForgeModule() {
    }

    public static void init(JavaPlugin owner) {
        plugin = owner;
        DefaultRecipes.register();
        PluginManager pm = owner.getServer().getPluginManager();
        pm.registerEvents(new ForgeMenuListener(), owner);
        ForgeCommand.register(owner);
        new ForgeTask().runTaskTimer(owner, 20L, 20L);
    }

    public static JavaPlugin plugin() {
        return plugin;
    }

    /** Convenience lookup used by menus, listener and task. */
    public static PlayerForgeData forgeData(Player player) {
        if (plugin == null) {
            return null;
        }
        PlayerData data = ((Main) plugin).getHandler().getPlayerDataHandler().getPlayerData(player);
        return data != null ? data.getForgeData() : null;
    }
}
