package me.chaos.EldoriaContent.drops;

import me.chaos.EldoriaDrops.BlockTag;
import me.chaos.EldoriaDrops.BlockTagRegistry;
import me.chaos.EldoriaItems.Item.Obtain.OreReplacementEntry;
import me.chaos.EldoriaItems.Item.Obtain.OreReplacementRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;

/**
 * Bonus drops while mining (added on top of the vanilla drop, never replacing it):
 * <ul>
 *   <li>Hartholz - 10% from any log</li>
 *   <li>Bronze 10% / Silber 5% / Stahl 1% / Verst. Stahl 0,1% / Titan 0,1% - from every metal-tagged block</li>
 *   <li>Gl\u00fccks-Klee - 0,5% from every gem-tagged block (as drill module item)</li>
 * </ul>
 * Uses OreReplacementEntry with replaceVanillaDrop=false so the normal ore drop always stays.
 * Tag membership comes from the EldoriaDrops BlockTagLoader, so config changes apply automatically.
 */
public final class MiningDrops {

    private MiningDrops() {
    }

    public static void register() {
        // Hartholz from logs
        for (Material log : Tag.LOGS.getValues()) {
            OreReplacementRegistry.register(
                    new OreReplacementEntry(log, "hardwood", 10.0, false));
        }

        // metals
        for (Material block : BlockTagRegistry.getBlocks(BlockTag.METAL)) {
            OreReplacementRegistry.register(new OreReplacementEntry(block, "bronze", 10.0, false));
            OreReplacementRegistry.register(new OreReplacementEntry(block, "silver", 5.0, false));
            OreReplacementRegistry.register(new OreReplacementEntry(block, "steel", 1.0, false));
            OreReplacementRegistry.register(new OreReplacementEntry(block, "reinforced_steel", 0.1, false));
            OreReplacementRegistry.register(new OreReplacementEntry(block, "titanium", 0.1, false));
        }

        // gems: lucky clover (drill module item, resolved via ForgeItems)
        for (Material block : BlockTagRegistry.getBlocks(BlockTag.GEM)) {
            OreReplacementRegistry.register(new OreReplacementEntry(block, "drill:lucky_metal_clover", 0.5, false));
        }
    }
}
