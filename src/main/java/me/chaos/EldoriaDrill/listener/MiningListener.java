package me.chaos.EldoriaDrill.listener;

import me.chaos.EldoriaDrill.api.DrillBreakEvent;
import me.chaos.EldoriaDrill.data.DrillCalculator;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.effect.DrillBreakContext;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.mining.BlockMiningLevels;
import me.chaos.EldoriaDrill.mining.DrillLevels;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import me.chaos.EldoriaDrill.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

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

    private static void deny(BlockBreakEvent event, Player player, String message) {
        event.setCancelled(true);
        player.sendActionBar(Text.plain(message, NamedTextColor.RED));
    }
}
