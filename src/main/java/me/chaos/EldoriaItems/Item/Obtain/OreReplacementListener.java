package me.chaos.EldoriaItems.Item.Obtain;

import me.chaos.EldoriaForge.recipe.ForgeItems;
import me.chaos.EldoriaItems.Item.CustomItemService;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the "Item drop replacement" obtain method, e.g. a small chance to get Steel
 * instead of / alongside Iron when mining Iron Ore. Register entries via
 * {@link OreReplacementRegistry#register}.
 */
public class OreReplacementListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        List<OreReplacementEntry> entries = OreReplacementRegistry.getForBlock(event.getBlock().getType());
        if (entries.isEmpty()) return;

        for (OreReplacementEntry entry : entries) {
            if (ThreadLocalRandom.current().nextDouble(100.0) <= entry.chancePercent()) {
                // EldoriaItems template ids first; "drill:<id>", "drillbase:<id>" and "nexo:<id>"
                // are resolved through the shared ForgeItems resolver.
                ItemStack customDrop = CustomItemService.createItemStack(entry.resultItemTemplateId());
                if (customDrop == null) {
                    customDrop = ForgeItems.resolve(entry.resultItemTemplateId());
                    if (customDrop.getType() == Material.BARRIER) continue; // unresolvable -> no drop
                }

                if (entry.replaceVanillaDrop()) {
                    event.setDropItems(false);
                }
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), customDrop);
                break; // only ever roll one replacement per block break
            }
        }
    }
}
