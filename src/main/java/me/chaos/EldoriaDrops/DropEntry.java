package me.chaos.EldoriaDrops;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * One line of a {@link DropTable}: an item and the chance (0.0 - 1.0, where 1.0 = 100%)
 * that it REPLACES the block's normal drop.
 * <p>
 * The item is a {@link Supplier} so it is created fresh on every drop and can point at
 * custom items that are registered later (e.g. from the EldoriaItems registry):
 * <pre>DropEntry.of(() -> myCustomItem(), 0.05f)</pre>
 */
public record DropEntry(Supplier<ItemStack> item, float chance) {

    public DropEntry {
        Objects.requireNonNull(item, "item");
        if (Float.isNaN(chance) || chance < 0f || chance > 1f) {
            throw new IllegalArgumentException("chance must be between 0.0 and 1.0 but was " + chance);
        }
    }

    public static DropEntry of(Material material, float chance) {
        if (!material.isItem() || material.isAir()) {
            throw new IllegalArgumentException(material + " is not a valid drop item");
        }
        return new DropEntry(() -> ItemStack.of(material), chance);
    }

    public static DropEntry of(ItemStack template, float chance) {
        ItemStack copy = template.clone();
        return new DropEntry(copy::clone, chance);
    }

    public static DropEntry of(Supplier<ItemStack> supplier, float chance) {
        return new DropEntry(supplier, chance);
    }

    /** A fresh copy of the drop item, or null if the supplier produced nothing usable. */
    public ItemStack create() {
        ItemStack stack = item.get();
        if (stack == null || stack.getType().isAir()) return null;
        return stack.clone();
    }
}
