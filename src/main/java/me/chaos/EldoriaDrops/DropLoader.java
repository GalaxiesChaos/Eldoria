package me.chaos.EldoriaDrops;

import org.bukkit.Material;

/**
 * Registers the loot table of every tag. Chance 0.05f = 5% to replace the normal drop
 * (the replacement is exactly ONE item; Abbau-Dropchance may still add bonus copies).
 * <p>
 * The items below are VANILLA PLACEHOLDERS so the system can be tested end to end.
 * Swap them for your custom items, e.g.
 * <pre>DropEntry.of(() -> CustomItemRegistry.get("ruby").createItemStack(), 0.02f)</pre>
 * (adapt to the real EldoriaItems API - the Supplier makes load order irrelevant).
 */
public final class DropLoader {

    private DropLoader() {}

    public static void loadDrops() {
        // STONE
        DropRegistry.register(BlockTag.STONE, DropEntry.of(Material.FLINT, 0.03f));
        DropRegistry.register(BlockTag.STONE, DropEntry.of(Material.CLAY_BALL, 0.01f));

        // WOOD
        DropRegistry.register(BlockTag.WOOD, DropEntry.of(Material.APPLE, 0.03f));
        DropRegistry.register(BlockTag.WOOD, DropEntry.of(Material.HONEYCOMB, 0.01f));

        // METAL
        DropRegistry.register(BlockTag.METAL, DropEntry.of(Material.GOLD_NUGGET, 0.04f));

        // GEM (diamond)
        DropRegistry.register(BlockTag.GEM, DropEntry.of(Material.AMETHYST_SHARD, 0.03f));

        // EMERALD
        DropRegistry.register(BlockTag.EMERALD, DropEntry.of(Material.PRISMARINE_CRYSTALS, 0.03f));

        // LAPIS
        DropRegistry.register(BlockTag.LAPIS, DropEntry.of(Material.GLOW_INK_SAC, 0.03f));

        // REDSTONE
        DropRegistry.register(BlockTag.REDSTONE, DropEntry.of(Material.GLOWSTONE_DUST, 0.03f));

        // QUARTZ
        DropRegistry.register(BlockTag.QUARTZ, DropEntry.of(Material.PRISMARINE_SHARD, 0.03f));

        // COAL
        DropRegistry.register(BlockTag.COAL, DropEntry.of(Material.GUNPOWDER, 0.03f));

        // ANCIENT_DEBRIS
        DropRegistry.register(BlockTag.ANCIENT_DEBRIS, DropEntry.of(Material.ECHO_SHARD, 0.05f));

        // MITHRIL: no blocks carry this tag yet - add entries once they do
    }
}
