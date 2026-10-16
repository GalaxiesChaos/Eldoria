package me.chaos.EldoriaDrops;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One {@link DropTable} per {@link BlockTag}. Mirrors the other registries (StatRegistry, QuestRegistry, ...).
 */
public final class DropRegistry {

    private static final Map<BlockTag, DropTable> TABLES = new LinkedHashMap<>();

    private DropRegistry() {}

    /** The table of a tag, created empty on first access. */
    public static DropTable getOrCreate(BlockTag tag) {
        if (BlockTagRegistry.get(tag.getId()) != tag) {
            throw new IllegalStateException("Block tag is not registered: " + tag.getId());
        }
        return TABLES.computeIfAbsent(tag, t -> new DropTable());
    }

    public static void register(BlockTag tag, DropEntry entry) {
        getOrCreate(tag).add(entry);
    }

    /** May be null if nothing was registered for the tag. */
    public static DropTable get(BlockTag tag) {
        return TABLES.get(tag);
    }

    public static Map<BlockTag, DropTable> getAll() {
        return Collections.unmodifiableMap(TABLES);
    }

    /**
     * Rolls the tables of every tag the block carries (in tag order); the first table that
     * produces a replacement wins.
     *
     * @return the replacement item, or null if the normal drop should stay.
     */
    public static ItemStack roll(Material block) {
        for (BlockTag tag : BlockTagRegistry.getTags(block)) {
            DropTable table = TABLES.get(tag);
            if (table == null) continue;

            ItemStack result = table.roll();
            if (result != null) return result;
        }
        return null;
    }
}
