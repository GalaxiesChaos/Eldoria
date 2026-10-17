package me.chaos.EldoriaItems.Item.Ability;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Scaffold for active abilities / special item modifiers.
 * <p>
 * The design spec asks for "space for an active ability or special modifier that will be
 * implemented later" - that's exactly what this is: it defines how an ability is *displayed*
 * in the lore right now, plus a hook ({@link #activate}) to wire up real behaviour later
 * without having to touch the item/lore rendering code again.
 */
public interface ItemAbility {
    String getId();

    Component getDisplayName();

    /** Extra lore lines describing what the ability does (already colored/formatted). */
    List<Component> getDescriptionLines();

    /** Ticks between uses; 0 for passive abilities / abilities without a cooldown. */
    default int getCooldownTicks() {
        return 0;
    }

    /**
     * Called when the player triggers the ability (e.g. right-click while holding the item).
     * No-op by default - implement once the ability/trigger system exists.
     */
    default void activate(Player player, ItemStack stack) {
    }
}
