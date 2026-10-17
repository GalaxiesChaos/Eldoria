package me.chaos.EldoriaItems.Item;

import java.util.Objects;

/**
 * Represents a "type" of item, shown as the bottom line of an item's lore
 * (e.g. "SPITZHACKE", "BOHRER", "KATANA" - or even a pure crafting material like "ERZ").
 * <p>
 * Types are open-ended by design: vanilla-like types (pickaxe, axe, sword, ...) and fully
 * custom ones (drill, shortbow, katana, raw materials, ...) are registered the same way via
 * {@link ItemTypeRegistry#register(ItemType)}. See {@link DefaultItemTypes} for ready-made ones.
 */
public final class ItemType {
    private final String key;
    private final String displayName;
    private final ItemCategory category;

    public ItemType(String key, String displayName, ItemCategory category) {
        this.key = key.toLowerCase();
        this.displayName = displayName;
        this.category = category;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemCategory getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemType itemType)) return false;
        return key.equals(itemType.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "ItemType{" + key + "}";
    }
}
