package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.CustomEnchant;
import me.chaos.EldoriaEnchants.EnchantRegistry;
import me.chaos.EldoriaEnchants.ItemEnchantHandler;
import me.chaos.eldoriaBase.Utils.GuiBuilder.GuiInterface;
import me.chaos.eldoriaBase.Utils.RomanNumeral;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Reworked enchanting table menu. {@link #ITEM_SLOT} holds the gear being
 * enchanted; {@link #OPTION_SLOTS} list every enchant applicable to that item as
 * a book, showing its current level and the XP-level cost to buy the next one.
 * <p>
 * Levels bought here are capped per-enchant at {@link CustomEnchant#getMaxTableLevel()}
 * - anything above that only comes from enchant books (drops/recipes) applied at
 * the anvil (see {@link EnchantAnvilInv}).
 */
public class EnchantTableInv implements InventoryHolder {

    public static final int ITEM_SLOT = 4;
    public static final int[] OPTION_SLOTS = {19, 20, 21, 22, 23, 24, 25};

    private final Inventory inventory;

    public EnchantTableInv() {
        this.inventory = Bukkit.createInventory(this, 36, Component.text("Verzauberungstisch"));
        GuiInterface.fillInv(inventory);
        inventory.setItem(ITEM_SLOT, null);
        refresh();
    }

    /** Rebuilds the option slots from whatever is currently sitting in ITEM_SLOT. Call after the item slot changes. */
    public void refresh() {
        for (int slot : OPTION_SLOTS) {
            inventory.setItem(slot, ItemStack.of(Material.GRAY_STAINED_GLASS_PANE));
        }

        ItemStack target = inventory.getItem(ITEM_SLOT);
        if (target == null || target.getType().isAir()) return;

        List<CustomEnchant> applicable = EnchantRegistry.getApplicable(target);
        for (int i = 0; i < applicable.size() && i < OPTION_SLOTS.length; i++) {
            inventory.setItem(OPTION_SLOTS[i], buildOptionItem(applicable.get(i), target));
        }
    }

    private ItemStack buildOptionItem(CustomEnchant enchant, ItemStack target) {
        int currentLevel = ItemEnchantHandler.getLevel(target, enchant);
        int cap = enchant.getMaxTableLevel();

        ItemStack book = ItemStack.of(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.displayName(enchant.getDisplayName()
                .append(Component.text(" " + RomanNumeral.toRoman(Math.max(currentLevel + 1, 1))))
                .color(NamedTextColor.AQUA));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Aktuelles Level: " + currentLevel + "/" + enchant.getMaxLevel()).color(NamedTextColor.GRAY));

        if (currentLevel >= cap) {
            lore.add(Component.text("Maximum am Tisch erreicht (" + cap + ")").color(NamedTextColor.RED));
            lore.add(Component.text("H\u00f6here Level nur \u00fcber seltene Drops/Rezepte").color(NamedTextColor.DARK_GRAY));
        } else {
            int cost = enchant.getTableCost(currentLevel + 1);
            lore.add(Component.text("Kosten: " + cost + " Erfahrungslevel").color(NamedTextColor.GREEN));
            lore.add(Component.text("Klicken zum Aufwerten").color(NamedTextColor.YELLOW));
        }

        meta.lore(lore);
        book.setItemMeta(meta);
        return book;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
