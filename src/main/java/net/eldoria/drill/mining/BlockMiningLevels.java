package net.eldoria.drill.mining;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.EnumMap;
import java.util.Map;

/** Which drill level a block needs. Vanilla tool tiers by default, with per-material overrides. */
public final class BlockMiningLevels {

    private static final Map<Material, Integer> OVERRIDES = new EnumMap<>(Material.class);

    private BlockMiningLevels() {
    }

    /** E.g. {@code override(Material.ANCIENT_DEBRIS, DrillLevels.NETHERITE)}. */
    public static void override(Material material, int level) {
        OVERRIDES.put(material, level);
    }

    public static int required(Block block) {
        Material type = block.getType();
        Integer override = OVERRIDES.get(type);
        if (override != null) {
            return override;
        }
        if (Tag.NEEDS_DIAMOND_TOOL.isTagged(type)) {
            return DrillLevels.DIAMOND;
        }
        if (Tag.NEEDS_IRON_TOOL.isTagged(type)) {
            return DrillLevels.IRON;
        }
        if (Tag.NEEDS_STONE_TOOL.isTagged(type)) {
            return DrillLevels.STONE;
        }
        return DrillLevels.WOOD;
    }
}
