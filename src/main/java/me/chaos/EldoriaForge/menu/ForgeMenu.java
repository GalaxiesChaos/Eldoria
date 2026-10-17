package me.chaos.EldoriaForge.menu;

import me.chaos.EldoriaAntiDupe.ProtectedGui;
import me.chaos.EldoriaForge.ForgeModule;
import me.chaos.EldoriaForge.data.ForgeSlot;
import me.chaos.EldoriaForge.data.PlayerForgeData;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The 6x9 forge menu. One station per column: furnace icon (row 1) + 3 progress
 * panes (rows 2-4) that turn from red to green (1/3, 2/3, done). First and last
 * row stay empty, except the page arrows (9 stations/page; 16 slots = 2 pages).
 * <pre>
 * row0: (empty)
 * row1: F F F F F F F F F  <- furnaces / items being forged
 * row2: P P P P P P P P P  <- panes
 * row3: P P P P P P P P P
 * row4: P P P P P P P P P
 * row5: (empty, page arrows at 45 / 53)
 * </pre>
 */
public final class ForgeMenu implements ProtectedGui {
    public static final int SIZE = 54;
    public static final int STATIONS_PER_PAGE = 9;
    public static final int FURNACE_ROW_START = 9;
    public static final int PREV_PAGE_SLOT = 45;
    public static final int NEXT_PAGE_SLOT = 53;
    private static final int[] PANE_ROWS = {18, 27, 36};

    private final PlayerForgeData data;
    private final int page;
    private final Inventory inventory;

    private ForgeMenu(PlayerForgeData data, int page) {
        this.data = data;
        this.page = Math.max(0, page);
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Schmiede", NamedTextColor.DARK_GRAY));
    }

    public static boolean open(Player player) {
        return open(player, 0);
    }

    public static boolean open(Player player, int page) {
        PlayerForgeData data = ForgeModule.forgeData(player);
        if (data == null) {
            player.sendMessage(Text.plain("Spielerdaten sind noch nicht geladen.", NamedTextColor.RED));
            return false;
        }
        ForgeMenu menu = new ForgeMenu(data, page);
        menu.render();
        player.openInventory(menu.inventory);
        return true;
    }

    public int page() {
        return page;
    }

    public PlayerForgeData data() {
        return data;
    }

    public static int pages(PlayerForgeData data) {
        return Math.max(1, (int) Math.ceil(data.totalSlots() / (double) STATIONS_PER_PAGE));
    }

    /** Rebuilds the whole menu. Called on open, on interaction and once per second by ForgeTask. */
    public void render() {
        inventory.clear();
        for (int col = 0; col < STATIONS_PER_PAGE; col++) {
            int index = page * STATIONS_PER_PAGE + col;
            int[] paneSlots = {PANE_ROWS[0] + col, PANE_ROWS[1] + col, PANE_ROWS[2] + col};
            fillStation(index, FURNACE_ROW_START + col, paneSlots);
        }
        if (page > 0) {
            inventory.setItem(PREV_PAGE_SLOT, named(Material.ARROW, "Vorherige Seite"));
        }
        if (page < pages(data) - 1) {
            inventory.setItem(NEXT_PAGE_SLOT, named(Material.ARROW, "Nächste Seite"));
        }
    }

    private void fillStation(int index, int furnaceSlot, int[] paneSlots) {
        if (index >= data.totalSlots()) {
            return; // no more slots configured -> leave column empty
        }
        if (!data.isUnlocked(index)) {
            inventory.setItem(furnaceSlot, named(Material.BARRIER, "Schmiedeplatz #" + (index + 1),
                    Text.gray("Gesperrt")));
            return;
        }

        ForgeSlot slot = data.slot(index);
        if (slot == null || !slot.isActive()) {
            inventory.setItem(furnaceSlot, named(Material.FURNACE, "Schmiedeplatz #" + (index + 1),
                    Text.gray("Klicke, um ein Rezept zu wählen")));
            return;
        }

        Optional<ForgeRecipe> recipe = slot.recipe();
        if (recipe.isEmpty()) {
            inventory.setItem(furnaceSlot, named(Material.BARRIER, "Fehler",
                    Text.gray("Unbekanntes Rezept: " + slot.recipeId())));
            return;
        }

        inventory.setItem(furnaceSlot, forgingItem(slot, recipe.get()));

        long elapsed = slot.elapsedSeconds();
        int time = recipe.get().timeSeconds();
        for (int i = 0; i < paneSlots.length; i++) {
            // pane 1 turns green after 1/3 of the time, pane 2 after 2/3, pane 3 when done
            boolean green = elapsed >= time * (i + 1) / 3.0;
            Material pane = green ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
            inventory.setItem(paneSlots[i], ItemStack.of(pane));
        }
    }

    /** Furnace slot icon while forging: the item being produced + progress lore. */
    private ItemStack forgingItem(ForgeSlot slot, ForgeRecipe recipe) {
        ItemStack item = recipe.outputItemStack();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Text.plain("Wird geschmiedet: " + recipe.name(), NamedTextColor.GOLD));
        if (slot.isDone(recipe.timeSeconds())) {
            lore.add(Text.plain("FERTIG! Klicke zum Abholen", NamedTextColor.GREEN));
        } else {
            lore.add(Text.plain("Verbleibend: " + Text.time(slot.remainingSeconds(recipe.timeSeconds())), NamedTextColor.YELLOW));
        }
        meta.lore(lore);
        if (slot.isDone(recipe.timeSeconds())) {
            meta.setEnchantmentGlintOverride(true);
        }
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack named(Material material, String name, Component... lore) {
        ItemStack item = ItemStack.of(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Text.plain(name, NamedTextColor.WHITE));
        if (lore.length > 0) {
            meta.lore(List.of(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
