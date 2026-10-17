package me.chaos.eldoriaBase.Stats.Mining;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Stats.Stat;
import me.chaos.eldoriaBase.Stats.StatApplicability;
import me.chaos.eldoriaBase.Stats.StatValueType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;

import java.util.List;

/**
 * CHANCE stat: multiplies the *amount* of every drop while mining ore or raw stone.
 * 100 value = double stack sizes, 200 = triple, remainder = chance for one more multiple
 * (same math as {@link Stat#rollChanceMultiplier(double)}). Stacks are grown in place rather
 * than spawning extra item entities (that's what {@link MiningDropchanceStat} does instead).
 */
public class MiningFortuneStat extends Stat {

    public static final String ID = "mining_fortune";

    private final Main main;

    public MiningFortuneStat(Main main) {
        super(
                ID,
                "Bergbaugl\u00fcck",
                StatValueType.CHANCE,
                100.0,
                StatApplicability.builder().blocks(MiningCategories.ORES_AND_RAW_STONE).build(),
                List.of(BlockDropItemEvent.class)
        );
        this.main = main;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof BlockDropItemEvent dropEvent)) return;
        if (dropEvent.isCancelled()) return;

        Material broken = dropEvent.getBlockState().getType();
        if (!getApplicability().appliesToBlock(broken)) return;

        Player player = dropEvent.getPlayer();
        double value = main.getHandler().getStatManager().getValue(player, this);
        if (value <= 0) return;

        int multiplier = rollChanceMultiplier(value);
        if (multiplier <= 1) return;

        for (Item drop : dropEvent.getItems()) {
            org.bukkit.inventory.ItemStack stack = drop.getItemStack();
            int max = Math.max(1, stack.getMaxStackSize());
            stack.setAmount(Math.min(max, stack.getAmount() * multiplier));
        }
    }
}
