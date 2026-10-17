package me.chaos.eldoriaBase.Stats.Mining;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Stats.Stat;
import me.chaos.eldoriaBase.Stats.StatApplicability;
import me.chaos.eldoriaBase.Stats.StatValueType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PER_UNIT stat: every point breaks one additional block of the same type around the block the
 * player just mined (breadth-first, 26-neighbour search). "Verbreiterung" for ore veins / raw
 * stone patches. Extra blocks are broken with the held tool via {@link Block#breakNaturally},
 * which does not re-fire BlockBreakEvent, so this cannot recurse.
 * <p>
 * Sources are the usual aggregated ones (StatManager): armor pieces + held item.
 */
public class MiningSpreadStat extends Stat {

    public static final String ID = "mining_spread";

    /** Hard cap so one break can never clear more than this many extra blocks. */
    private static final int MAX_EXTRA_BLOCKS = 32;

    private final Main main;

    public MiningSpreadStat(Main main) {
        super(
                ID,
                "Abbaufl\u00e4che",
                StatValueType.PER_UNIT,
                0.0,
                StatApplicability.builder().blocks(MiningCategories.ORES_AND_RAW_STONE).build(),
                List.of(BlockBreakEvent.class)
        );
        this.main = main;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof BlockBreakEvent breakEvent)) return;
        if (breakEvent.isCancelled()) return;

        Player player = breakEvent.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;

        Material target = breakEvent.getBlock().getType();
        if (!getApplicability().appliesToBlock(target)) return;

        double value = main.getHandler().getStatManager().getValue(player, this);
        int remaining = (int) Math.min(Math.floor(value), MAX_EXTRA_BLOCKS);
        if (remaining <= 0) return;

        ItemStack tool = player.getInventory().getItemInMainHand();
        Block origin = breakEvent.getBlock();

        Set<Block> visited = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && remaining > 0) {
            Block current = queue.poll();
            for (int dx = -1; dx <= 1 && remaining > 0; dx++) {
                for (int dy = -1; dy <= 1 && remaining > 0; dy++) {
                    for (int dz = -1; dz <= 1 && remaining > 0; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        Block relative = current.getRelative(dx, dy, dz);
                        if (relative.getType() != target || !visited.add(relative)) continue;
                        // No synthetic BlockBreakEvent on purpose: it would re-enter the stat
                        // and the drill listener. Protection hardening (PlotSquared checks)
                        // belongs here later if drills ever need it.
                        relative.breakNaturally(tool);
                        queue.add(relative);
                        remaining--;
                    }
                }
            }
        }
    }
}
