package me.chaos.EldoriaCommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Teleportiert zum Spawn einer Welt, deren Name in der config.yml steht
 * (z. B. worlds.farmwelt). Eine Klasse für /farmwelt, /plotwelt und /spawn.
 * Multiverse-Welten sind normale Bukkit-Welten, Bukkit.getWorld reicht daher.
 */
public class WorldTeleportCommand implements BasicCommand {

    private final Main plugin;
    private final String worldConfigPath;
    private final String permission;
    private final String displayName;

    public WorldTeleportCommand(Main plugin, String worldConfigPath, String permission, String displayName) {
        this.plugin = plugin;
        this.worldConfigPath = worldConfigPath;
        this.permission = permission;
        this.displayName = displayName;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        String worldName = plugin.getConfig().getString(worldConfigPath);
        World world = worldName == null ? null : Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(Component.text(displayName + " ist aktuell nicht verfügbar.")
                    .color(NamedTextColor.RED));
            return;
        }

        Location target = world.getSpawnLocation().add(0.5, 0, 0.5);
        target.setYaw(player.getYaw());
        target.setPitch(player.getPitch());

        player.teleportAsync(target).thenAccept(success -> {
            if (success) {
                player.sendMessage(Component.text("Teleportiert: " + displayName).color(NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("Teleport fehlgeschlagen.").color(NamedTextColor.RED));
            }
        });
    }

    @Override
    public String permission() {
        return permission;
    }
}
