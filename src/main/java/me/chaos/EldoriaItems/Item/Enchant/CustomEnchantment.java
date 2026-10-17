package me.chaos.EldoriaItems.Item.Enchant;

import net.kyori.adventure.text.Component;

/**
 * Minimal contract a "custom enchant" needs to fulfil so the item builder/service can render
 * it in an item's lore. This is intentionally small so it can be implemented by - or bridged
 * to - Eldoria's own custom enchant system once/if that lives elsewhere; you don't have to use
 * {@link SimpleCustomEnchantment} as-is, just implement this interface with your real one.
 */
public interface CustomEnchantment {
    String getId();

    int getMaxLevel();

    /**
     * Fully formatted, colored, non-italic lore line for the given level,
     * e.g. "Effizienz+ III".
     */
    Component getLoreLine(int level);
}
