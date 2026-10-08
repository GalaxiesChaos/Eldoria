package me.chaos.EldoriaEnchants;

import me.chaos.eldoriaBase.Utils.RomanNumeral;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Builds standalone "Eldoria Enchant Book" items. These are meant to come from
 * custom crafting recipes or rare mob drops, so that pushing an enchant past its
 * enchanting-table cap (see CustomEnchant#getMaxTableLevel) stays gated behind
 * content instead of pure XP grinding. Applied to gear via the reworked anvil
 * (see EnchantAnvilInv).
 * <p>
 * Wiring up the actual recipes/drop tables is intentionally left out of this
 * rework - hook them up wherever you generate loot/recipes and just call
 * {@link #create} for the result item.
 */
public class EnchantBookFactory {

    public static final NamespacedKey BOOK_ENCHANT_KEY = new NamespacedKey("eldoriaenchants", "book_enchant");
    public static final NamespacedKey BOOK_LEVEL_KEY = new NamespacedKey("eldoriaenchants", "book_level");

    private EnchantBookFactory() {}

    public static ItemStack create(CustomEnchant enchant, int level) {
        int clamped = Math.max(1, Math.min(level, enchant.getMaxLevel()));

        ItemStack book = ItemStack.of(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();

        meta.displayName(enchant.getDisplayName()
                .append(Component.text(" " + RomanNumeral.toRoman(clamped)))
                .color(NamedTextColor.LIGHT_PURPLE));
        meta.lore(List.of(Component.text("Am Amboss anwendbar").color(NamedTextColor.GRAY)));

        meta.getPersistentDataContainer().set(BOOK_ENCHANT_KEY, PersistentDataType.STRING, enchant.getId());
        meta.getPersistentDataContainer().set(BOOK_LEVEL_KEY, PersistentDataType.INTEGER, clamped);

        book.setItemMeta(meta);
        return book;
    }

    public static String readEnchantId(ItemStack book) {
        if (book == null || !book.hasItemMeta()) return null;
        return book.getItemMeta().getPersistentDataContainer().get(BOOK_ENCHANT_KEY, PersistentDataType.STRING);
    }

    public static Integer readLevel(ItemStack book) {
        if (book == null || !book.hasItemMeta()) return null;
        return book.getItemMeta().getPersistentDataContainer().get(BOOK_LEVEL_KEY, PersistentDataType.INTEGER);
    }
}
