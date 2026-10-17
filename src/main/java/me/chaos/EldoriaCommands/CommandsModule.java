package me.chaos.EldoriaCommands;

import me.chaos.eldoriaBase.Main;

import java.util.List;

/** Einstiegspunkt: einmal aus Main#onEnable aufrufen. */
public final class CommandsModule {

    private CommandsModule() {}

    public static void init(Main plugin) {
        plugin.registerCommand("farmwelt", List.of("farm"),
                new WorldTeleportCommand(plugin, "worlds.farmwelt", "eldoria.farmwelt", "Farmwelt"));
        plugin.registerCommand("plotwelt", List.of("plots"),
                new WorldTeleportCommand(plugin, "worlds.plotwelt", "eldoria.plotwelt", "Plotwelt"));
        plugin.registerCommand("spawn",
                new WorldTeleportCommand(plugin, "worlds.spawn", "eldoria.spawn", "Spawn"));
        plugin.registerCommand("ec", List.of("enderchest"), new EnderChestCommand());
        plugin.registerCommand("rtp", List.of("randomtp", "wild"), new RandomTpCommand(plugin));
    }
}
