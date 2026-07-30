package me.chaos.eldoriaBase.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SignInputListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, Consumer<Integer>> pendingInputs = new HashMap<>();
    private final Map<UUID, BlockState> previousStates = new HashMap<>();
    private final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();

    public SignInputListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void promptForInteger(Player player, List<String> lines, Consumer<Integer> callback) {
        UUID uuid = player.getUniqueId();

        // Give each player their own spot so concurrent uses don't collide,
        // and pick somewhere players won't normally see (e.g. deep below the world).
        Location loc = player.getLocation().clone();
        loc.setY(-64);
        Block block = loc.getBlock();

        previousStates.put(uuid, block.getState()); // remember what was there before

        block.setType(Material.OAK_SIGN);
        Sign sign = (Sign) block.getState();
        SignSide front = sign.getSide(Side.FRONT);

        for (int i = 0; i < lines.size() && i < 4; i++) {
            front.line(i, Component.text(lines.get(i)));
        }
        sign.update();

        pendingInputs.put(uuid, callback);
        player.openSign(sign, Side.FRONT);

        // Safety net: pressing Escape instead of submitting never fires SignChangeEvent,
        // so clean up after a delay to avoid leaving the fake sign behind.
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pendingInputs.remove(uuid) != null) {
                restoreBlock(uuid, block);
            }
        }, 20L * 30); // 30 second timeout
        timeoutTasks.put(uuid, task);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Consumer<Integer> callback = pendingInputs.remove(uuid);
        if (callback == null || event.getSide() != Side.FRONT) return;

        BukkitTask timeout = timeoutTasks.remove(uuid);
        if (timeout != null) timeout.cancel();

        String input = PlainTextComponentSerializer.plainText().serialize(event.line(0));
        restoreBlock(uuid, event.getBlock());

        try {
            callback.accept(Integer.parseInt(input.trim()));
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("That wasn't a valid number.")
                    .color(NamedTextColor.RED));
        }
    }

    private void restoreBlock(UUID uuid, Block block) {
        BlockState previous = previousStates.remove(uuid);
        if (previous != null) {
            previous.update(true, false);
        } else {
            block.setType(Material.AIR);
        }
    }
}
