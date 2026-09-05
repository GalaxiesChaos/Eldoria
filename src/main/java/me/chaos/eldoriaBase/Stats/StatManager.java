package me.chaos.eldoriaBase.Stats;

import me.chaos.eldoriaBase.Handlers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Computes the fully aggregated value of a stat for a player, adding up every source
 * mentioned in the requirements:
 * <ul>
 *     <li>armor pieces currently worn</li>
 *     <li>the item currently held in the main hand</li>
 *     <li>bonuses granted through a (future) stat-allocation menu, persisted via {@link PlayerStatData}</li>
 * </ul>
 */
public class StatManager {

    private final Handlers handlers;

    public StatManager(Handlers handlers) {
        this.handlers = handlers;
    }

    public double getValue(Player player, Stat stat) {
        double total = getMenuBonus(player, stat);

        PlayerInventory inventory = player.getInventory();
        for (ItemStack armorPiece : inventory.getArmorContents()) {
            total += StatItemUtil.getStat(armorPiece, stat);
        }

        total += StatItemUtil.getStat(inventory.getItemInMainHand(), stat);

        return total;
    }

    private double getMenuBonus(Player player, Stat stat) {
        PlayerStatData data = handlers.getPlayerDataHandler().getPlayerData(player).getStatData();
        return data == null ? 0.0 : data.getBonus(stat);
    }
}