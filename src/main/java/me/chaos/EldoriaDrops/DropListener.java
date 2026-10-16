package me.chaos.EldoriaDrops;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Block drops go through three steps, ordered by event priority:
 * <ol>
 *     <li>LOW    - tagged block? Roll its {@link DropTable}. On success the whole normal drop is replaced
 *                  by exactly one item.</li>
 *     <li>NORMAL - the stats run (MiningDropchanceStat multiplies the stacks, so a replaced item gets the
 *                  Abbau-Dropchance bonus too - but only on blocks that stat applies to, e.g. not on wood).</li>
 *     <li>HIGH   - everything is put straight into the player's inventory; what doesn't fit is dropped at the block.</li>
 * </ol>
 * Mob drops (killed by a player) also go straight into the killer's inventory.
 */
public class DropListener implements Listener {

    // ------------------------------------------------------------------ blocks

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockDropReplace(BlockDropItemEvent event) {
        List<Item> items = event.getItems();
        if (items.isEmpty()) return; // wrong tool / nothing dropped -> nothing to replace

        Material broken = event.getBlockState().getType();
        if (!BlockTagRegistry.isTagged(broken)) return;

        // Not with Silk Touch, otherwise ores/stone could be "farmed" as blocks for rare items
        if (usesSilkTouch(event.getPlayer())) return;

        ItemStack replacement = DropRegistry.roll(broken);
        if (replacement == null) return;

        // Re-use the first drop entity for the replacement, remove all others
        items.get(0).setItemStack(replacement);
        for (int i = items.size() - 1; i >= 1; i--) {
            items.remove(i);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockDropCollect(BlockDropItemEvent event) {
        List<Item> items = event.getItems();
        if (items.isEmpty()) return;

        List<ItemStack> drops = new ArrayList<>(items.size());
        for (Item item : items) {
            drops.add(item.getItemStack());
        }
        items.clear(); // the vanilla item entities are removed, we hand the items out ourselves

        Location dropAt = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
        for (ItemStack stack : drops) {
            giveOrDrop(event.getPlayer(), stack, dropAt);
        }
    }

    // ------------------------------------------------------------------ mobs

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return; // a dying player's inventory is not loot

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty()) return;

        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack stack : drops) {
            if (stack == null || stack.getType().isAir()) continue;
            leftovers.addAll(killer.getInventory().addItem(stack.clone()).values());
        }

        // whatever didn't fit simply drops where the mob died
        drops.clear();
        drops.addAll(leftovers);
    }

    // ------------------------------------------------------------------ helpers

    private boolean usesSilkTouch(Player player) {
        return player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH);
    }

    private void giveOrDrop(Player player, ItemStack stack, Location fallback) {
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack.clone());
        for (ItemStack rest : leftover.values()) {
            dropSplit(fallback, rest);
        }
    }

    /** Drops a (possibly oversized) stack as several normal-sized item entities. */
    private void dropSplit(Location at, ItemStack stack) {
        int max = Math.max(1, stack.getMaxStackSize());
        int remaining = stack.getAmount();
        while (remaining > 0) {
            ItemStack part = stack.clone();
            part.setAmount(Math.min(max, remaining));
            at.getWorld().dropItemNaturally(at, part);
            remaining -= part.getAmount();
        }
    }
}
