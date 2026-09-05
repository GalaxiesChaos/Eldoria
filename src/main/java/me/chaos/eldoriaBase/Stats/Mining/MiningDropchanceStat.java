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

import java.util.ArrayList;
import java.util.List;

/**
 * CHANCE stat: 100 value = guaranteed 2x drops, 200 = guaranteed 3x drops, and so on,
 * with the remainder acting as a percentage chance for one more (see
 * {@link Stat#rollChanceMultiplier(double)}). Only applies while mining ore or
 * uncrafted/raw stone (see {@link MiningCategories}).
 */
public class MiningDropchanceStat extends Stat {

    public static final String ID = "mining_dropchance";

    private final Main main;

    public MiningDropchanceStat(Main main) {
        super(
                ID,
                "Abbau-Dropchance",
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

        Material broken = dropEvent.getBlockState().getType();
        if (!getApplicability().appliesToBlock(broken)) return;

        Player player = dropEvent.getPlayer();
        double value = main.getHandler().getStatManager().getValue(player, this);
        if (value <= 0) return;

        int multiplier = rollChanceMultiplier(value);
        if (multiplier <= 1) return;

        // Snapshot first: we're about to add more Item entities to the world and don't
        // want to iterate over those newly-added ones too.
        List<Item> originalDrops = new ArrayList<>(dropEvent.getItems());
        for (Item original : originalDrops) {
            for (int i = 1; i < multiplier; i++) {
                original.getWorld().dropItemNaturally(original.getLocation(), original.getItemStack().clone());
            }
        }
    }
}