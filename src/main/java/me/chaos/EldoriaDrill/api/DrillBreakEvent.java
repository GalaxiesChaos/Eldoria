package me.chaos.EldoriaDrill.api;

import me.chaos.EldoriaDrill.stat.DrillStatSet;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Fired after a drill successfully started breaking a block (fuel was consumed, effects ran).
 * The custom drop system can listen to this to read the drill stats (e.g. DROP_CHANCE) and the
 * module loot odds multipliers per block tag.
 */
public final class DrillBreakEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Block block;
    private final DrillStatSet stats;
    private final Map<String, Double> lootOddsMultipliers;

    public DrillBreakEvent(Player player, Block block, DrillStatSet stats, Map<String, Double> lootOddsMultipliers) {
        this.player = player;
        this.block = block;
        this.stats = stats;
        this.lootOddsMultipliers = Collections.unmodifiableMap(lootOddsMultipliers);
    }

    public Player getPlayer() {
        return player;
    }

    public Block getBlock() {
        return block;
    }

    /** Combined stats of base + all installed upgrades/modules. */
    public DrillStatSet getStats() {
        return stats;
    }

    /** Multipliers per block tag id from modules. */
    public Map<String, Double> getLootOddsMultipliers() {
        return lootOddsMultipliers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
