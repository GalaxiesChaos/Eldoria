package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.Interface.EnchantAnvilInv;
import me.chaos.EldoriaEnchants.Interface.EnchantTableInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Set;

/**
 * Right-clicking an enchanting table or any anvil variant now opens Eldoria's own
 * menu instead of vanilla's. This fully replaces vanilla enchanting/anvil UI for
 * these blocks.
 */
public class EnchantStationListener implements Listener {

    private static final Set<Material> ANVIL_TYPES = Set.of(Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return; // avoid firing twice (main hand + off hand)
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Material type = event.getClickedBlock().getType();

        if (type == Material.ENCHANTING_TABLE) {
            event.setCancelled(true);
            player.openInventory(new EnchantTableInv().getInventory());
        } else if (ANVIL_TYPES.contains(type)) {
            event.setCancelled(true);
            player.openInventory(new EnchantAnvilInv().getInventory());
        }
    }
}
