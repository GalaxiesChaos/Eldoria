package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.CustomEnchant;
import me.chaos.EldoriaEnchants.EnchantBookFactory;
import me.chaos.EldoriaEnchants.EnchantRegistry;
import me.chaos.EldoriaEnchants.EnchantVariables;
import me.chaos.EldoriaEnchants.Interface.EnchantAnvilInv;
import me.chaos.EldoriaEnchants.Interface.EnchantTableInv;
import me.chaos.EldoriaEnchants.ItemEnchantHandler;
import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all clicks inside the two custom enchanting menus. Everything outside
 * the designated item/book/option slots is decorative and blocked.
 * <p>
 * Known limitation: this handles single clicks on the relevant slots. Shift-click
 * and drag operations from the player's own inventory aren't specifically
 * validated against the item/book slots, so treat this as a solid first pass
 * rather than a fully hardened GUI.
 */
public class EnchantMenuClickListener implements Listener {

    private final Main main;

    public EnchantMenuClickListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        if (event.getInventory().getHolder() instanceof EnchantTableInv tableInv) {
            handleTableClick(event, player, tableInv);
        } else if (event.getInventory().getHolder() instanceof EnchantAnvilInv anvilInv) {
            handleAnvilClick(event, player, anvilInv);
        }
    }

    private void handleTableClick(InventoryClickEvent event, Player player, EnchantTableInv tableInv) {
        boolean clickedInTop = event.getClickedInventory() == tableInv.getInventory();
        int slot = event.getSlot();

        if (clickedInTop && slot == EnchantTableInv.ITEM_SLOT) {
            // Let the click resolve first (the item is actually placed/removed after this
            // handler returns), then refresh the option slots on the next tick.
            Bukkit.getScheduler().runTask(main, tableInv::refresh);
            return;
        }

        if (!clickedInTop) return; // player's own inventory - leave it alone

        event.setCancelled(true);
        if (Arrays.stream(EnchantTableInv.OPTION_SLOTS).noneMatch(s -> s == slot)) return;

        ItemStack target = tableInv.getInventory().getItem(EnchantTableInv.ITEM_SLOT);
        if (target == null || target.getType().isAir()) return;

        List<CustomEnchant> applicable = EnchantRegistry.getApplicable(target);
        int index = indexOf(EnchantTableInv.OPTION_SLOTS, slot);
        if (index < 0 || index >= applicable.size()) return;

        CustomEnchant enchant = applicable.get(index);
        int currentLevel = ItemEnchantHandler.getLevel(target, enchant);

        if (currentLevel >= enchant.getMaxTableLevel()) {
            player.sendMessage(Component.text("Maximum am Tisch bereits erreicht.").color(NamedTextColor.RED));
            return;
        }

        int nextLevel = currentLevel + 1;
        int cost = enchant.getTableCost(nextLevel);

        if (player.getLevel() < cost) {
            player.sendMessage(Component.text("Nicht genug Erfahrungslevel (" + cost + " ben\u00f6tigt).").color(NamedTextColor.RED));
            return;
        }

        player.setLevel(player.getLevel() - cost);
        ItemEnchantHandler.addLevel(target, enchant, 1);
        tableInv.getInventory().setItem(EnchantTableInv.ITEM_SLOT, target);
        tableInv.refresh();
    }

    private void handleAnvilClick(InventoryClickEvent event, Player player, EnchantAnvilInv anvilInv) {
        boolean clickedInTop = event.getClickedInventory() == anvilInv.getInventory();
        int slot = event.getSlot();

        if (!clickedInTop) return;
        if (slot == EnchantAnvilInv.GEAR_SLOT || slot == EnchantAnvilInv.BOOK_SLOT) return; // freely place/remove gear + book

        event.setCancelled(true);
        if (slot != EnchantAnvilInv.CONFIRM_SLOT) return;

        ItemStack gear = anvilInv.getInventory().getItem(EnchantAnvilInv.GEAR_SLOT);
        ItemStack book = anvilInv.getInventory().getItem(EnchantAnvilInv.BOOK_SLOT);

        if (gear == null || gear.getType().isAir() || book == null || book.getType().isAir()) {
            player.sendMessage(Component.text("Lege Gegenstand und Verzauberungsbuch ein.").color(NamedTextColor.RED));
            return;
        }

        String enchantId = EnchantBookFactory.readEnchantId(book);
        Integer level = EnchantBookFactory.readLevel(book);
        if (enchantId == null || level == null) {
            player.sendMessage(Component.text("Das ist kein g\u00fcltiges Verzauberungsbuch.").color(NamedTextColor.RED));
            return;
        }

        CustomEnchant enchant = EnchantRegistry.get(enchantId);
        if (enchant == null || !enchant.canApplyTo(gear)) {
            player.sendMessage(Component.text("Diese Verzauberung passt nicht auf diesen Gegenstand.").color(NamedTextColor.RED));
            return;
        }

        int currentLevel = ItemEnchantHandler.getLevel(gear, enchant);
        if (level <= currentLevel) {
            player.sendMessage(Component.text("Das Buch ist nicht st\u00e4rker als die vorhandene Verzauberung.").color(NamedTextColor.RED));
            return;
        }

        ItemEnchantHandler.setEnchant(gear, enchant, level, new EnchantVariables());
        anvilInv.getInventory().setItem(EnchantAnvilInv.GEAR_SLOT, gear);

        book.setAmount(book.getAmount() - 1);
        anvilInv.getInventory().setItem(EnchantAnvilInv.BOOK_SLOT, book.getAmount() <= 0 ? null : book);

        player.sendMessage(Component.text("Verzauberung angewendet: ").color(NamedTextColor.GREEN)
                .append(enchant.getDisplayName()));
    }

    private static int indexOf(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) return i;
        }
        return -1;
    }
}
