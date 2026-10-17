package me.chaos.EldoriaForge.listener;

import me.chaos.EldoriaForge.ForgeModule;
import me.chaos.EldoriaForge.data.ForgeSlot;
import me.chaos.EldoriaForge.data.PlayerForgeData;
import me.chaos.EldoriaForge.menu.ForgeMenu;
import me.chaos.EldoriaForge.menu.ForgeRecipeMenu;
import me.chaos.EldoriaForge.recipe.ForgeIngredient;
import me.chaos.EldoriaForge.recipe.ForgeItems;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/** All forge menu interactions. Item movement itself is blocked by GuiExploitListener (ProtectedGui). */
public final class ForgeMenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory().getHolder() instanceof ForgeMenu menu) {
            event.setCancelled(true);
            handleForgeClick(player, menu, event.getSlot());
        } else if (event.getClickedInventory().getHolder() instanceof ForgeRecipeMenu menu) {
            event.setCancelled(true);
            handleRecipeClick(player, menu, event.getSlot());
        }
    }

    private void handleForgeClick(Player player, ForgeMenu menu, int slot) {
        PlayerForgeData data = ForgeModule.forgeData(player);
        if (data == null) {
            return;
        }

        if (slot == ForgeMenu.PREV_PAGE_SLOT || slot == ForgeMenu.NEXT_PAGE_SLOT) {
            if (menu.getInventory().getItem(slot) == null) {
                return; // no arrow on this page
            }
            int newPage = slot == ForgeMenu.PREV_PAGE_SLOT ? menu.page() - 1 : menu.page() + 1;
            ForgeMenu.open(player, newPage);
            return;
        }

        if (slot < ForgeMenu.FURNACE_ROW_START || slot >= ForgeMenu.FURNACE_ROW_START + ForgeMenu.STATIONS_PER_PAGE) {
            return; // only the furnace row is interactive; panes/background are display-only
        }

        int slotIndex = menu.page() * ForgeMenu.STATIONS_PER_PAGE + (slot - ForgeMenu.FURNACE_ROW_START);
        ForgeSlot forgeSlot = data.slot(slotIndex);
        if (forgeSlot == null) {
            return;
        }

        if (forgeSlot.isActive() && forgeSlot.recipe().isEmpty()) {
            // recipe was removed from the registry — clean up instead of soft-locking the slot
            data.collect(slotIndex, player);
            player.sendMessage(Text.plain("Das Rezept existiert nicht mehr, der Platz wurde geleert.", NamedTextColor.RED));
            menu.render();
            return;
        }

        if (!forgeSlot.isActive()) {
            ForgeRecipeMenu.open(player, slotIndex);
        } else if (forgeSlot.isDone(forgeSlot.recipe().get().timeSeconds())) {
            collect(player, data, slotIndex, menu);
        } else {
            player.sendMessage(Text.plain("Noch nicht fertig! Verbleibend: "
                    + Text.time(forgeSlot.remainingSeconds(forgeSlot.recipe().get().timeSeconds())), NamedTextColor.YELLOW));
        }
    }

    private void collect(Player player, PlayerForgeData data, int slotIndex, ForgeMenu menu) {
        ItemStack result = data.collect(slotIndex, player);
        if (result != null) {
            var leftovers = player.getInventory().addItem(result);
            for (ItemStack leftover : leftovers.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
            player.sendMessage(Text.plain("Geschmiedeter Gegenstand erhalten!", NamedTextColor.GREEN));
        }
        menu.render();
    }

    private void handleRecipeClick(Player player, ForgeRecipeMenu menu, int slot) {
        PlayerForgeData data = ForgeModule.forgeData(player);
        if (data == null) {
            return;
        }

        if (slot == ForgeRecipeMenu.BACK_SLOT) {
            ForgeMenu.open(player, pageForSlot(menu.originSlot()));
            return;
        }

        ForgeRecipe recipe = menu.recipe(slot);
        if (recipe == null) {
            return;
        }

        if (data.startForging(menu.originSlot(), recipe, player)) {
            player.sendMessage(Text.plain("Schmieden gestartet: " + recipe.name()
                    + " (" + Text.time(recipe.timeSeconds()) + ")", NamedTextColor.GREEN));
            ForgeMenu.open(player, pageForSlot(menu.originSlot()));
        } else {
            for (ForgeIngredient ingredient : recipe.missing(player)) {
                int have = ingredient.countIn(player.getInventory());
                player.sendMessage(Text.plain("Fehlt: " + ingredient.amount() + "x ", NamedTextColor.RED)
                        .append(ForgeItems.displayName(ingredient.item()))
                        .append(Text.gray(" (du hast " + have + ")")));
            }
        }
    }

    private int pageForSlot(int slotIndex) {
        return slotIndex / ForgeMenu.STATIONS_PER_PAGE;
    }
}
