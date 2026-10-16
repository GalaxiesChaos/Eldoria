package net.eldoria.drill.effect;

import net.eldoria.drill.util.Text;

/**
 * Module effect: multiplies the loot odds of a block tag. The multiplier is delivered to the
 * drop system through DrillBreakEvent#getLootOddsMultipliers().
 */
public final class LootOddsBoostEffect implements DrillEffect {

    private final String tagId;
    private final double multiplier;

    public LootOddsBoostEffect(String tagId, double multiplier) {
        this.tagId = tagId;
        this.multiplier = multiplier;
    }

    @Override
    public void onBlockBroken(DrillBreakContext context) {
        if (BlockTags.of(context.block()).contains(tagId)) {
            context.boostLootOdds(tagId, multiplier);
        }
    }

    @Override
    public String describe() {
        return "x" + Text.num(multiplier) + " Loot-Chance auf " + tagId + "-Blöcke";
    }
}
