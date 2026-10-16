package me.chaos.EldoriaDrops;

import me.chaos.eldoriaBase.Stats.Mining.MiningCategories;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

/**
 * Registers the built-in tags and assigns them to all existing blocks.
 * New tags / new blocks: add them here - that's the whole registration step.
 */
public final class BlockTagLoader {

    private BlockTagLoader() {}

    public static void loadTags() {
        BlockTagRegistry.register(BlockTag.STONE);
        BlockTagRegistry.register(BlockTag.WOOD);
        BlockTagRegistry.register(BlockTag.METAL);
        BlockTagRegistry.register(BlockTag.GEM);
        BlockTagRegistry.register(BlockTag.EMERALD);
        BlockTagRegistry.register(BlockTag.LAPIS);
        BlockTagRegistry.register(BlockTag.REDSTONE);
        BlockTagRegistry.register(BlockTag.QUARTZ);
        BlockTagRegistry.register(BlockTag.COAL);
        BlockTagRegistry.register(BlockTag.ANCIENT_DEBRIS);
        BlockTagRegistry.register(BlockTag.MITHRIL);

        // STONE: exactly the raw stone variants the mining stats already use
        BlockTagRegistry.assign(BlockTag.STONE, MiningCategories.RAW_STONE);

        // WOOD: every log / stem / wood block incl. stripped variants (Bukkit's Tag.LOGS).
        // Planks are deliberately NOT included - add Tag.PLANKS.getValues() here if you want them.
        BlockTagRegistry.assign(BlockTag.WOOD, Tag.LOGS.getValues());

        // METAL
        BlockTagRegistry.assign(BlockTag.METAL,
                Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
                Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
                Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.NETHER_GOLD_ORE);

        // GEM = diamond
        BlockTagRegistry.assign(BlockTag.GEM,
                Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE);

        // EMERALD / LAPIS / REDSTONE / QUARTZ: one tag each
        BlockTagRegistry.assign(BlockTag.EMERALD, Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE);
        BlockTagRegistry.assign(BlockTag.LAPIS, Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE);
        BlockTagRegistry.assign(BlockTag.REDSTONE, Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE);
        BlockTagRegistry.assign(BlockTag.QUARTZ, Material.NETHER_QUARTZ_ORE);

        // COAL
        BlockTagRegistry.assign(BlockTag.COAL,
                Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE);

        // ANCIENT_DEBRIS
        BlockTagRegistry.assign(BlockTag.ANCIENT_DEBRIS, Material.ANCIENT_DEBRIS);

        // MITHRIL: intentionally no blocks yet (reserved for later)

        // Info only: ores nobody has tagged yet (they still go into the inventory, just without replacement drops)
        List<Material> untagged = MiningCategories.ORES.stream()
                .filter(ore -> !BlockTagRegistry.isTagged(ore))
                .toList();
        if (!untagged.isEmpty()) {
            Bukkit.getLogger().info("[EldoriaDrops] Ores without a block tag: " + untagged);
        }
    }
}
