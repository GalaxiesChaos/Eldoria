package me.chaos.EldoriaItems.Item.Obtain;

import org.bukkit.Material;

/**
 * Describes a chance to get a custom item instead of (or alongside) the vanilla drop when a
 * specific block is mined - e.g. "mining Iron Ore has a 2% chance to also drop Steel".
 *
 * @param replaceVanillaDrop if true, the vanilla drop is cancelled when this triggers;
 *                           if false, the custom item drops in addition to the vanilla drop.
 */
public record OreReplacementEntry(Material sourceBlock, String resultItemTemplateId, double chancePercent,
                                   boolean replaceVanillaDrop) {
}
