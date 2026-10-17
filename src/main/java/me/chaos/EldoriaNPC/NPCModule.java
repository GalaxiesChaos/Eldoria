package me.chaos.EldoriaNPC;

import me.chaos.eldoriaBase.Main;
import org.bukkit.Bukkit;

/**
 * Single entry point of the NPC system - call once from {@code Main#onEnable()}:
 * <pre>NPCModule.init(this);</pre>
 * Registers the manager + listener and the admin command, loads {@code npcs.yml} and
 * restores previously placed NPCs one second after startup (worlds must be loaded first).
 */
public final class NPCModule {

    private static NPCManager manager;

    private NPCModule() {}

    public static void init(Main plugin) {
        NPCRegistry.reload(plugin);
        manager = new NPCManager(plugin);

        Bukkit.getPluginManager().registerEvents(new NPCListener(plugin, manager), plugin);
        plugin.registerCommand("npc", new NPCCommand(plugin, manager));

        // Delayed: adopt persisted entities from the worlds and respawn missing NPCs
        Bukkit.getScheduler().runTaskLater(plugin, manager::restoreSpawned, 20L);
    }

    /** Call from {@code Main#onDisable()} - flushes placed NPC positions to npcs-data.yml. */
    public static void shutdown() {
        if (manager != null) {
            manager.save();
        }
    }
}
