package me.chaos.EldoriaDrill.effect;

import me.chaos.EldoriaDrill.stat.DrillStatSet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/** Data passed to {@link DrillEffect}s while a block is broken. */
public final class DrillBreakContext {

    private final Player player;
    private final Block block;
    private final DrillStatSet stats;
    private final Map<String, Double> lootOddsMultipliers = new HashMap<>();

    public DrillBreakContext(Player player, Block block, DrillStatSet stats) {
        this.player = player;
        this.block = block;
        this.stats = stats;
    }

    public Player player() {
        return player;
    }

    public Block block() {
        return block;
    }

    public DrillStatSet stats() {
        return stats;
    }

    /** Multipliers per block tag id; read by the drop system through DrillBreakEvent. */
    public Map<String, Double> lootOddsMultipliers() {
        return lootOddsMultipliers;
    }

    public void boostLootOdds(String tagId, double multiplier) {
        lootOddsMultipliers.merge(tagId, multiplier, (a, b) -> a * b);
    }

    /** Gives the item to the player directly, dropping what does not fit at the block. */
    public void give(ItemStack stack) {
        Map<Integer, ItemStack> left = player.getInventory().addItem(stack);
        Location at = block.getLocation().add(0.5, 0.5, 0.5);
        left.values().forEach(rest -> block.getWorld().dropItemNaturally(at, rest));
    }
}
