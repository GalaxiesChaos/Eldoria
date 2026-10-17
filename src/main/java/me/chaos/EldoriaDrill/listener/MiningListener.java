package me.chaos.EldoriaDrill.listener;

import me.chaos.EldoriaDrill.api.DrillBreakEvent;
import me.chaos.EldoriaDrill.data.DrillCalculator;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.effect.DrillBreakContext;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.mining.BlockMiningLevels;
import me.chaos.EldoriaDrill.mining.DrillLevels;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import me.chaos.EldoriaDrill.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/** Fuel consumption, mining level check and effects while mining with a drill. */
public final class MiningListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemStack hand = player.getInventory().getItemInMainHand();
        Optional<DrillData> loaded = DrillItems.read(hand);
        if (loaded.isEmpty()) {
            return;
        }
        DrillData data = loaded.get();

        if (DrillCalculator.base(data).isEmpty()) {
            deny(event, player, "Die Basis dieses Bohrers existiert nicht mehr.");
            return;
        }
        if (data.fuel() <= 1e-6) {
            deny(event, player, "Der Bohrer hat keinen Treibstoff mehr.");
            return;
        }
        int level = DrillCalculator.miningLevel(data);
        if (level < 0) {
            deny(event, player, "Baue zuerst einen Drillkopf ein.");
            return;
        }
        int required = BlockMiningLevels.required(event.getBlock());
        if (required > level) {
            deny(event, player, "Dein Bohrer ist zu schwach für diesen Block (benötigt: " + DrillLevels.name(required) + ").");
            return;
        }

        DrillStatSet stats = DrillCalculator.totalStats(data);
        data.setFuel(data.fuel() - DrillCalculator.fuelCost(stats));

        DrillBreakContext context = new DrillBreakContext(player, event.getBlock(), stats);
        for (DrillEffect effect : DrillCalculator.effects(data)) {
            effect.onBlockBroken(context);
        }
        Bukkit.getPluginManager().callEvent(
                new DrillBreakEvent(player, event.getBlock(), stats, context.lootOddsMultipliers()));

        applySpread(event, hand, data, stats);

        DrillItems.write(hand, data);
        player.getInventory().setItemInMainHand(hand);
        if (data.fuel() <= 1e-6) {
            player.sendActionBar(Text.plain("Dem Bohrer ist der Treibstoff ausgegangen!", NamedTextColor.RED));
        }
    }

    /** Fuel replaces durability: drills never take tool damage. */
    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        if (DrillItems.isDrill(event.getItem())) {
            event.setCancelled(true);
        }
    }

    /**
     * MINING_SPREAD: each point clears one additional block of the same type around the broken
     * block (breadth-first), each extra block consuming the same fuel as the main block.
     * breakNaturally does not re-fire BlockBreakEvent, so this cannot recurse or double-dip
     * effects. Protection hardening (e.g. PlotSquared checks) belongs here if needed later.
     */
    private static final int MAX_SPREAD_EXTRA = 16;

    private static void applySpread(BlockBreakEvent event, ItemStack hand, DrillData data, DrillStatSet stats) {
        double spread = stats.get(DrillStatRegistry.MINING_SPREAD);
        if (spread <= 0) {
            return;
        }
        double fuelCost = DrillCalculator.fuelCost(stats);
        int remaining = Math.min((int) Math.floor(spread), MAX_SPREAD_EXTRA);
        if (remaining <= 0) {
            return;
        }

        Block origin = event.getBlock();
        Material target = origin.getType();
        Set<Block> visited = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && remaining > 0) {
            if (data.fuel() <= fuelCost) {
                break; // out of fuel - stop clearing extra blocks
            }
            Block current = queue.poll();
            for (int dx = -1; dx <= 1 && remaining > 0; dx++) {
                for (int dy = -1; dy <= 1 && remaining > 0; dy++) {
                    for (int dz = -1; dz <= 1 && remaining > 0; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        Block relative = current.getRelative(dx, dy, dz);
                        if (relative.getType() != target || !visited.add(relative)) {
                            continue;
                        }
                        data.setFuel(data.fuel() - fuelCost);
                        relative.breakNaturally(hand);
                        queue.add(relative);
                        remaining--;
                    }
                }
            }
        }
    }

    private static void deny(BlockBreakEvent event, Player player, String message) {
        event.setCancelled(true);
        player.sendActionBar(Text.plain(message, NamedTextColor.RED));
    }
}
