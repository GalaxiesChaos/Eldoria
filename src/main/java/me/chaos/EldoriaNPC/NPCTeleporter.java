package me.chaos.EldoriaNPC;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles the TELEPORT interaction: fixed teleport to a world's spawn or a random safe
 * location inside a radius around it (e.g. a Multiverse farmworld).
 */
public final class NPCTeleporter {

    private static final int MAX_ATTEMPTS = 50;

    private NPCTeleporter() {}

    public static void teleport(Player player, NPCDefinition definition) {
        World world = org.bukkit.Bukkit.getWorld(definition.teleportWorld());
        if (world == null) {
            player.sendMessage(Component.text("Die Welt '" + definition.teleportWorld() + "' existiert nicht (noch nicht geladen?).")
                    .color(NamedTextColor.RED));
            return;
        }

        Location target = definition.teleportRandom()
                ? randomSafeLocation(world, definition.teleportRadius())
                : world.getSpawnLocation();

        if (target == null) {
            player.sendMessage(Component.text("Es wurde kein sicherer Teleportpunkt gefunden. Versuche es erneut.")
                    .color(NamedTextColor.RED));
            return;
        }

        Location finalTarget = target;
        player.teleportAsync(finalTarget).thenAccept(ok ->
                player.sendMessage(Component.text("Du wurdest nach " + world.getName() + " teleportiert.", NamedTextColor.GREEN)));
    }

    /**
     * Picks a random X/Z inside {@code radius} around the world spawn and returns the location
     * on top of the highest safe block, or null if nothing safe was found after {@link #MAX_ATTEMPTS}.
     */
    private static Location randomSafeLocation(World world, int radius) {
        Location spawn = world.getSpawnLocation();

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            int x = spawn.getBlockX() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);
            int z = spawn.getBlockZ() + ThreadLocalRandom.current().nextInt(-radius, radius + 1);

            if (!world.isChunkLoaded(x >> 4, z >> 4) && !world.loadChunk(x >> 4, z >> 4, false)) {
                continue;
            }

            int y = world.getHighestBlockYAt(x, z);
            Block ground = world.getBlockAt(x, y, z);
            Block above = ground.getRelative(BlockFace.UP);

            if (isSafe(ground) && above.getType().isAir()) {
                return new Location(world, x + 0.5, y + 1.0, z + 0.5);
            }
        }
        return null;
    }

    private static boolean isSafe(Block ground) {
        Material type = ground.getType();
        return type.isSolid()
                && !type.hasGravity()          // no sand/gravel that could suffocate
                && !ground.isLiquid()
                && type != Material.MAGMA_BLOCK
                && type != Material.CACTUS
                && type != Material.CAMPFIRE
                && type != Material.SOUL_CAMPFIRE;
    }
}
