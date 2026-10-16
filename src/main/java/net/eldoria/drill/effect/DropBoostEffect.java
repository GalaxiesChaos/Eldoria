package net.eldoria.drill.effect;

import net.eldoria.drill.util.Text;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/** Module effect: with a chance, an extra item drops when a block with the given tag is broken. */
public final class DropBoostEffect implements DrillEffect {

    private final String tagId;
    private final ItemStack reward;
    private final double chance;

    public DropBoostEffect(String tagId, ItemStack reward, double chance) {
        this.tagId = tagId;
        this.reward = reward.clone();
        this.chance = chance;
    }

    @Override
    public void onBlockBroken(DrillBreakContext context) {
        if (!BlockTags.of(context.block()).contains(tagId)) {
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() < chance) {
            context.give(reward.clone());
        }
    }

    @Override
    public String describe() {
        String item = reward.getType().name().toLowerCase().replace('_', ' ');
        return Text.num(chance * 100) + "% Chance auf " + reward.getAmount() + "x " + item + " bei " + tagId + "-Blöcken";
    }
}
