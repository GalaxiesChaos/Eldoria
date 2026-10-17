package me.chaos.EldoriaCommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * /rtp - zufälliger, sicherer Teleport in der Farmwelt (Ring um einen Mittelpunkt).
 * Chunks werden async geladen, danach wird die Position geprüft; bei Misserfolg
 * wird bis zu randomtp.max-attempts mal neu gewürfelt.
 * Gedacht für eine Overworld-Farmwelt (Nether/End haben keine Oberfläche).
 */
public class RandomTpCommand implements BasicCommand {

    private static final String BYPASS = "eldoria.randomtp.bypass";

    private static final Set<Material> UNSAFE = EnumSet.of(
            Material.LAVA, Material.WATER, Material.CACTUS, Material.MAGMA_BLOCK,
            Material.FIRE, Material.SOUL_FIRE, Material.CAMPFIRE, Material.SOUL_CAMPFIRE,
            Material.SWEET_BERRY_BUSH, Material.POWDER_SNOW, Material.POINTED_DRIPSTONE,
            Material.COBWEB, Material.WITHER_ROSE
    );

    private final Main plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private final Set<UUID> searching = ConcurrentHashMap.newKeySet();

    public RandomTpCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        String worldName = plugin.getConfig().getString("randomtp.world", "farmwelt");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage(Component.text("Die Farmwelt ist aktuell nicht verfügbar.").color(NamedTextColor.RED));
            return;
        }

        UUID uuid = player.getUniqueId();
        if (searching.contains(uuid)) {
            player.sendMessage(Component.text("Suche läuft bereits...").color(NamedTextColor.RED));
            return;
        }

        if (!player.hasPermission(BYPASS)) {
            long cooldownMs = plugin.getConfig().getLong("randomtp.cooldown-seconds", 60) * 1000L;
            long remaining = cooldowns.getOrDefault(uuid, 0L) + cooldownMs - System.currentTimeMillis();
            if (remaining > 0) {
                player.sendMessage(Component.text("Bitte warte noch " + ((remaining / 1000) + 1) + "s.")
                        .color(NamedTextColor.RED));
                return;
            }
        }

        searching.add(uuid);
        player.sendMessage(Component.text("Suche eine sichere Position...").color(NamedTextColor.GRAY));
        attempt(player, world, plugin.getConfig().getInt("randomtp.max-attempts", 15));
    }

    private void attempt(Player player, World world, int attemptsLeft) {
        UUID uuid = player.getUniqueId();

        if (!player.isOnline()) {
            searching.remove(uuid);
            return;
        }
        if (attemptsLeft <= 0) {
            searching.remove(uuid);
            player.sendMessage(Component.text("Keine sichere Position gefunden, versuche es erneut.")
                    .color(NamedTextColor.RED));
            return;
        }

        int min = plugin.getConfig().getInt("randomtp.min-radius", 200);
        int max = Math.max(min + 1, plugin.getConfig().getInt("randomtp.max-radius", 5000));
        double cx = plugin.getConfig().getDouble("randomtp.center-x", 0);
        double cz = plugin.getConfig().getDouble("randomtp.center-z", 0);

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        double angle = rnd.nextDouble(0, Math.PI * 2);
        double dist = Math.sqrt(rnd.nextDouble((double) min * min, (double) max * max)); // gleichmäßig über die Fläche
        int x = (int) Math.floor(cx + Math.cos(angle) * dist);
        int z = (int) Math.floor(cz + Math.sin(angle) * dist);

        if (!world.getWorldBorder().isInside(new Location(world, x, 64, z))) {
            attempt(player, world, attemptsLeft - 1);
            return;
        }

        world.getChunkAtAsync(x >> 4, z >> 4).thenAccept(chunk -> {
            Location safe = findSafe(world, x, z);
            if (safe == null) {
                attempt(player, world, attemptsLeft - 1);
                return;
            }
            safe.setYaw(player.getYaw());
            safe.setPitch(player.getPitch());

            player.teleportAsync(safe).thenAccept(success -> {
                searching.remove(uuid);
                if (success) {
                    cooldowns.put(uuid, System.currentTimeMillis());
                    player.sendMessage(Component.text("Viel Spaß in der Farmwelt!").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("Teleport fehlgeschlagen.").color(NamedTextColor.RED));
                }
            });
        }).exceptionally(ex -> {
            searching.remove(uuid);
            plugin.getLogger().warning("RandomTP Fehler: " + ex.getMessage());
            return null;
        });
    }

    private Location findSafe(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
        if (y <= world.getMinHeight() + 1 || y >= world.getMaxHeight() - 3) return null;

        Block ground = world.getBlockAt(x, y, z);
        Block feet = ground.getRelative(BlockFace.UP);
        Block head = feet.getRelative(BlockFace.UP);

        Material g = ground.getType();
        if (!g.isSolid() || UNSAFE.contains(g) || Tag.LEAVES.isTagged(g)) return null;
        if (!feet.isPassable() || feet.isLiquid() || UNSAFE.contains(feet.getType())) return null;
        if (!head.isPassable() || head.isLiquid() || UNSAFE.contains(head.getType())) return null;

        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    @Override
    public String permission() {
        return "eldoria.randomtp";
    }
}
