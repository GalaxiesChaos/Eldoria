package me.chaos.EldoriaEnchants;

import me.chaos.eldoriaBase.Utils.GuiBuilder.GuiInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Reworked anvil menu. {@link #GEAR_SLOT} holds the item to upgrade,
 * {@link #BOOK_SLOT} holds an Eldoria enchant book (see EnchantBookFactory)
 * obtained from recipes or rare drops. Clicking {@link #CONFIRM_SLOT} applies the
 * book's enchant/level onto the gear - this is the only route past the enchanting
 * table's level cap.
 * <p>
 * Note: this replaces vanilla anvil functionality (repair/rename/combine)
 * entirely while this menu is open. If you still want those, they'd need their
 * own slots added here.
 */
public class EnchantAnvilInv implements InventoryHolder {

    public static final int GEAR_SLOT = 11;
    public static final int BOOK_SLOT = 15;
    public static final int CONFIRM_SLOT = 22;

    private final Inventory inventory;

    public EnchantAnvilInv() {
        this.inventory = Bukkit.createInventory(this, 27, Component.text("Amboss"));
        GuiInterface.fillInv(inventory);
        inventory.setItem(GEAR_SLOT, null);
        inventory.setItem(BOOK_SLOT, null);
        refresh();
    }

    public void refresh() {
        ItemStack confirm = ItemStack.of(Material.ANVIL);
        ItemMeta meta = confirm.getItemMeta();
        meta.displayName(Component.text("Anwenden").color(NamedTextColor.GREEN));
        confirm.setItemMeta(meta);
        inventory.setItem(CONFIRM_SLOT, confirm);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
