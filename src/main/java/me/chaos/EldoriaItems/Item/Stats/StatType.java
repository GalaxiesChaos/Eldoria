package me.chaos.EldoriaItems.Item.Stats;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

/**
 * Defines one kind of stat (e.g. "Dropchance", "Mining Speed").
 * Stats are open-ended: register your own via {@link StatTypeRegistry#register(StatType)}.
 */
public final class StatType {
    private final String key;
    private final String displayName;
    private final String suffix; // e.g. "%" or ""
    private final NamedTextColor valueColor;

    public StatType(String key, String displayName, String suffix, NamedTextColor valueColor) {
        this.key = key.toLowerCase();
        this.displayName = displayName;
        this.suffix = suffix;
        this.valueColor = valueColor;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Builds the fully formatted, non-italic lore line for a given value, e.g.
     * "Dropchance: 10%". Whole numbers are shown without decimals.
     */
    public Component formatLine(double value) {
        String number = (value == Math.floor(value)) ? String.valueOf((long) value) : String.valueOf(value);
        return Component.text(displayName + ": ")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(number + suffix).color(valueColor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatType statType)) return false;
        return key.equals(statType.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
