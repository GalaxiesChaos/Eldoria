package me.chaos.EldoriaDrops;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The loot table of one {@link BlockTag}.
 * <p>
 * A block break does ONE roll over the whole table: the entries' chances are laid out
 * back to back (0.02 + 0.05 = 2% for entry A, 5% for entry B, 93% = normal drop), so every
 * entry has exactly the chance you gave it. That's why the chances of one table may not
 * add up to more than 1.0.
 */
public class DropTable {

    private static final float EPSILON = 1.0E-4f;

    private final List<DropEntry> entries = new ArrayList<>();
    private float totalChance = 0f;

    public DropTable add(DropEntry entry) {
        float newTotal = totalChance + entry.chance();
        if (newTotal > 1.0f + EPSILON) {
            throw new IllegalArgumentException(
                    "Chances of one drop table must not exceed 1.0 in total (would be " + newTotal + ")");
        }
        entries.add(entry);
        totalChance = newTotal;
        return this;
    }

    public List<DropEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public float getTotalChance() {
        return totalChance;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /** @return the replacement item, or null if the normal drop should stay. */
    public ItemStack roll() {
        if (entries.isEmpty()) return null;

        float roll = ThreadLocalRandom.current().nextFloat();
        float cumulative = 0f;
        for (DropEntry entry : entries) {
            cumulative += entry.chance();
            if (roll < cumulative) {
                return entry.create();
            }
        }
        return null;
    }
}
