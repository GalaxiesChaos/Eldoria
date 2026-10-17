package me.chaos.EldoriaItems.Item.Enchant;

import me.chaos.EldoriaItems.Item.LoreFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Convenience implementation of {@link CustomEnchantment} for the common
 * "Name + Roman numeral level" style (e.g. "Schärfe V"). Swap this out once your real
 * custom-enchant system exists - only the {@link CustomEnchantment} interface matters
 * to the rest of the item system.
 */
public class SimpleCustomEnchantment implements CustomEnchantment {
    private final String id;
    private final String displayName;
    private final int maxLevel;
    private final NamedTextColor color;

    public SimpleCustomEnchantment(String id, String displayName, int maxLevel, NamedTextColor color) {
        this.id = id;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.color = color;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public Component getLoreLine(int level) {
        return Component.text(displayName + " " + LoreFormat.toRoman(level))
                .color(color)
                .decoration(TextDecoration.ITALIC, false);
    }
}
