package me.chaos.eldoriaBase.Utils.GuiBuilder;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface GuiInterface {
    static void fillInv(Inventory inv){
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
        }
    }
}
