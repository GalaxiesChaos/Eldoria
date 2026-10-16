package me.chaos.EldoriaDrops;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Holds every {@link BlockTag} and knows which {@link Material} carries which tags.
 * A block may carry more than one tag.
 */
public final class BlockTagRegistry {

    private static final Map<String, BlockTag> TAGS = new LinkedHashMap<>();
    private static final Map<Material, Set<BlockTag>> BLOCK_TAGS = new EnumMap<>(Material.class);

    private BlockTagRegistry() {}

    public static BlockTag register(BlockTag tag) {
        if (TAGS.containsKey(tag.getId())) {
            throw new IllegalStateException("Duplicate block tag id: " + tag.getId());
        }
        TAGS.put(tag.getId(), tag);
        return tag;
    }

    public static BlockTag get(String id) {
        return id == null ? null : TAGS.get(id.toLowerCase(Locale.ROOT));
    }

    public static Collection<BlockTag> getAll() {
        return Collections.unmodifiableCollection(TAGS.values());
    }

    public static void assign(BlockTag tag, Material... materials) {
        assign(tag, Arrays.asList(materials));
    }

    public static void assign(BlockTag tag, Collection<Material> materials) {
        if (TAGS.get(tag.getId()) != tag) {
            throw new IllegalStateException("Block tag is not registered: " + tag.getId());
        }
        for (Material material : materials) {
            if (!material.isBlock()) {
                throw new IllegalArgumentException(material + " is not a block");
            }
            BLOCK_TAGS.computeIfAbsent(material, m -> new LinkedHashSet<>()).add(tag);
        }
    }

    /** All tags of a block - empty if it has none. */
    public static Set<BlockTag> getTags(Material material) {
        Set<BlockTag> tags = BLOCK_TAGS.get(material);
        return tags == null ? Set.of() : Collections.unmodifiableSet(tags);
    }

    public static boolean isTagged(Material material) {
        return BLOCK_TAGS.containsKey(material);
    }

    public static boolean hasTag(Material material, BlockTag tag) {
        return getTags(material).contains(tag);
    }

    /** Every block that carries the given tag (handy for debugging / menus). */
    public static Set<Material> getBlocks(BlockTag tag) {
        Set<Material> result = EnumSet.noneOf(Material.class);
        BLOCK_TAGS.forEach((material, tags) -> {
            if (tags.contains(tag)) result.add(material);
        });
        return result;
    }
}
