package me.chaos.eldoriaBase.Stats.Mining;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

/**
 * Curated set of "natural" mineable blocks: ores and their raw stone counterparts.
 * Deliberately excludes crafted/processed variants (stone bricks, polished stone,
 * slabs, stairs, walls, ...). Add more raw block types here if needed - both mining
 * stats read from this one shared set.
 */
public final class MiningCategories {

    private static final Set<Material> ORES = EnumSet.of(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.NETHER_GOLD_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.ANCIENT_DEBRIS
    );

    // "Uncrafted" stone - raw variants only. NOT stone bricks, polished stone/andesite/etc,
    // smooth stone, walls, stairs, slabs and the like.
    private static final Set<Material> RAW_STONE = EnumSet.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.GRANITE,
            Material.DIORITE,
            Material.ANDESITE,
            Material.TUFF,
            Material.NETHERRACK,
            Material.BLACKSTONE
    );

    public static final Set<Material> ORES_AND_RAW_STONE;

    static {
        EnumSet<Material> combined = EnumSet.copyOf(ORES);
        combined.addAll(RAW_STONE);
        ORES_AND_RAW_STONE = EnumSet.copyOf(combined);
    }

    private MiningCategories() {
    }
}