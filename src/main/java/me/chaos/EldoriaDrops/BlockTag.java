package me.chaos.EldoriaDrops;

import java.util.Locale;
import java.util.Objects;

/**
 * A category a block can belong to (stone, wood, metal, ...).
 * <p>
 * Deliberately a plain class instead of an enum: other modules can create and
 * register their own tags at runtime via {@link BlockTagRegistry#register(BlockTag)}
 * without touching this file (same open-registry idea as the rest of Eldoria).
 */
public final class BlockTag {

    public static final BlockTag STONE = new BlockTag("stone", "Stein");
    public static final BlockTag WOOD = new BlockTag("wood", "Holz");
    public static final BlockTag METAL = new BlockTag("metal", "Metall");
    public static final BlockTag GEM = new BlockTag("gem", "Edelstein"); // diamond only
    public static final BlockTag EMERALD = new BlockTag("emerald", "Smaragd");
    public static final BlockTag LAPIS = new BlockTag("lapis", "Lapislazuli");
    public static final BlockTag REDSTONE = new BlockTag("redstone", "Redstone");
    public static final BlockTag QUARTZ = new BlockTag("quartz", "Quarz");
    public static final BlockTag COAL = new BlockTag("coal", "Kohle");
    public static final BlockTag ANCIENT_DEBRIS = new BlockTag("ancient_debris", "Antiker Schutt");
    /** Reserved for later - registered, but no block carries it yet. */
    public static final BlockTag MITHRIL = new BlockTag("mithril", "Mithril");

    private final String id;
    private final String displayName;

    public BlockTag(String id, String displayName) {
        this.id = Objects.requireNonNull(id, "id").toLowerCase(Locale.ROOT);
        this.displayName = Objects.requireNonNull(displayName, "displayName");
    }

    /** Stable, lowercase identifier (also what quests reference in their "tag" meta). */
    public String getId() {
        return id;
    }

    /** Human readable name for menus / lore. */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlockTag other && other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "BlockTag[" + id + "]";
    }
}
