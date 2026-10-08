package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.Interface.EnchantAnvilInv;
import me.chaos.EldoriaEnchants.Interface.EnchantTableInv;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

/** These menus aren't real storage - whatever's sitting in their item/book slots goes back to the player on close. */
public class EnchantMenuCloseListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof EnchantTableInv tableInv) {
            returnItem(player, tableInv.getInventory().getItem(EnchantTableInv.ITEM_SLOT));
            tableInv.getInventory().setItem(EnchantTableInv.ITEM_SLOT, null);
        } else if (event.getInventory().getHolder() instanceof EnchantAnvilInv anvilInv) {
            returnItem(player, anvilInv.getInventory().getItem(EnchantAnvilInv.GEAR_SLOT));
            returnItem(player, anvilInv.getInventory().getItem(EnchantAnvilInv.BOOK_SLOT));
            anvilInv.getInventory().setItem(EnchantAnvilInv.GEAR_SLOT, null);
            anvilInv.getInventory().setItem(EnchantAnvilInv.BOOK_SLOT, null);
        }
    }

    private void returnItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        player.getInventory().addItem(item).values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }
}
