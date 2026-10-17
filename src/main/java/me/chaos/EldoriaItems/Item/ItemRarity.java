package me.chaos.EldoriaItems.Item;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * Base rarity tiers for custom items.
 * <p>
 * Rarities are ordered from {@link #GEWOEHNLICH} (weakest) to {@link #MYTHISCH} (strongest).
 * An item's *base* rarity is fixed when its {@link CustomItemTemplate} is created, but the
 * *current* rarity of one specific item instance can be upgraded later at runtime - see
 * {@link CustomItemService#upgradeRarity(org.bukkit.inventory.ItemStack)}.
 */
public enum ItemRarity {
    GEWOEHNLICH("Gewöhnlich", NamedTextColor.GRAY),
    UNGEWOEHNLICH("Ungewöhnlich", NamedTextColor.GREEN),
    SELTEN("Selten", NamedTextColor.DARK_BLUE),
    EPISCH("Episch", NamedTextColor.DARK_PURPLE),
    LEGENDAER("Legendär", NamedTextColor.GOLD),
    // No vanilla NamedTextColor is an actual "pink", so we use a real hex pink here to keep
    // it visually distinct from Selten (dark blue) and Episch (dark purple).
    MYTHISCH("Mythisch", TextColor.color(0xFF69B4));

    private final String displayName;
    private final TextColor color;

    ItemRarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }

    /** @return the next higher rarity, or {@code this} if already at the highest tier (Mythisch). */
    public ItemRarity next() {
        int idx = this.ordinal();
        ItemRarity[] values = values();
        return idx + 1 < values.length ? values[idx + 1] : this;
    }

    public boolean isMax() {
        return this == MYTHISCH;
    }

    /**
     * Resolves a rarity from its enum name (used when reading persistent item data),
     * falling back to {@code fallback} if the key is blank/unknown.
     */
    public static ItemRarity byKeyOrDefault(String key, ItemRarity fallback) {
        if (key == null || key.isBlank()) return fallback;
        try {
            return ItemRarity.valueOf(key);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
}
