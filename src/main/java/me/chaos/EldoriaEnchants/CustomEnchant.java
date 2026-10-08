package me.chaos.EldoriaEnchants;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

/**
 * Base type every Eldoria enchant extends. Doesn't do anything by itself - see
 * {@link StatEnchant} for passive attribute boosts and {@link EventEnchant} for
 * triggered effects (e.g. "poison the attacker when they hit you").
 * <p>
 * Two level numbers matter here:
 * - maxLevel: the absolute ceiling, only reachable via enchant books (see
 *   {@link EnchantBookFactory}) handed out through recipes or rare drops.
 * - maxTableLevel: how far the enchanting table (see EnchantTableInv) is allowed
 *   to take an item on its own. Must be <= maxLevel.
 */
public abstract class CustomEnchant {

    private final String id;
    private final Component displayName;
    private final int maxLevel;
    private final int maxTableLevel;
    private final EnchantTarget target;

    protected CustomEnchant(String id, Component displayName, int maxLevel, int maxTableLevel, EnchantTarget target) {
        if (maxTableLevel > maxLevel) {
            throw new IllegalArgumentException(
                    "maxTableLevel (" + maxTableLevel + ") cannot exceed maxLevel (" + maxLevel + ") for enchant " + id);
        }

        this.id = id;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.maxTableLevel = maxTableLevel;
        this.target = target;
    }

    /** Stable identifier used for PDC storage and registry lookups. Never change this once items exist with it saved. */
    public String getId() {
        return id;
    }

    /** Name shown on the item's lore and in enchanting menus. */
    public Component getDisplayName() {
        return displayName;
    }

    /** Absolute maximum level, reachable only through enchant books (drops/recipes). */
    public int getMaxLevel() {
        return maxLevel;
    }

    /** Maximum level obtainable directly from the enchanting table. */
    public int getMaxTableLevel() {
        return maxTableLevel;
    }

    public EnchantTarget getTarget() {
        return target;
    }

    public boolean canApplyTo(ItemStack item) {
        return target.matches(item);
    }

    /**
     * XP-level cost to buy the given level at the enchanting table (i.e. the cost to
     * go from level-1 to level). Default is a flat linear curve - override for a
     * steeper/cheaper curve on a specific enchant.
     */
    public int getTableCost(int level) {
        return level * 3;
    }
}
