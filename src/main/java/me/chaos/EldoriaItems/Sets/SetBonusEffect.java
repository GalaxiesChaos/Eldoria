package me.chaos.EldoriaItems.Sets;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * One effect of a {@link SetBonus}. Every method is a no-op by default so this interface stays
 * usable even before a full effect/trigger system exists - implement only what you need.
 */
public interface SetBonusEffect {
    /** Fired once when the player reaches the required amount of pieces for this bonus. */
    default void onActivate(Player player) {
    }

    /** Fired once when the player drops below the required amount of pieces again. */
    default void onDeactivate(Player player) {
    }

    /**
     * Fired for every Bukkit event forwarded via {@link ItemSetHandler#dispatch} while this
     * bonus is active - the hook for custom triggers, e.g. "chance to get a bonus drop while
     * mining with the full set on". Wire up specific events per-effect as needed.
     */
    default void onGenericEvent(Event event, Player player) {
    }
}
