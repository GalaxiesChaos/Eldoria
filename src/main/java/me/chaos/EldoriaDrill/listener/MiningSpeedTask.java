package me.chaos.EldoriaDrill.listener;

import me.chaos.EldoriaDrill.data.DrillCalculator;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/** Applies the MINING_SPEED stat as a hidden Haste effect while a fuelled drill is held. */
public final class MiningSpeedTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            Optional<DrillData> data = DrillItems.read(hand);
            if (data.isEmpty() || data.get().fuel() <= 1e-6) {
                continue;
            }
            int level = (int) Math.floor(DrillCalculator.totalStats(data.get()).get(DrillStatRegistry.MINING_SPEED));
            if (level <= 0) {
                continue;
            }
            level = Math.min(level, 5);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 30, level - 1, true, false, false));
        }
    }
}
