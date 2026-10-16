package net.eldoria.drill.menu;

import me.chaos.EldoriaAntiDupe.ProtectedGui;
import net.eldoria.drill.base.DrillBase;
import net.eldoria.drill.data.DrillCalculator;
import net.eldoria.drill.data.DrillData;
import net.eldoria.drill.data.DrillItems;
import net.eldoria.drill.data.DrillSlot;
import net.eldoria.drill.stat.DrillStatSet;
import net.eldoria.drill.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The 5x9 drill menu. State is written through to the item on every change.
 * <pre>
 *  col 0        col 3-5            col 8
 *  fuel   row0: . . . . BASE . . . .
 *  bar    row1: free row            module 1
 *         row2:     HEAD ENGINE TANK    module 2
 *         row3:     PLATE MOD1 MOD2     module 3
 *         row4:           refuel button
 * </pre>
 */
public final class DrillMenu implements ProtectedGui {

    public static final int SIZE = 45;
    public static final int BASE_SLOT = 4;
    public static final int REFUEL_BUTTON = 40;
    /** Bottom to top. */
    private static final int[] FUEL_SLOTS = {36, 27, 18, 9, 0};

    private final DrillHandle handle;
    private final DrillData data;
    private final DrillBase base;
    private final Inventory inventory;

    private DrillMenu(DrillHandle handle, DrillData data, DrillBase base) {
        this.handle = handle;
        this.data = data;
        this.base = base;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text(base.displayName(), NamedTextColor.DARK_GRAY));
    }

    /** Opens the menu for the drill in the player's main hand. */
    public static boolean open(Player player) {
        Optional<DrillHandle> handle = DrillHandle.ofMainHand(player);
        return handle.isPresent() && open(handle.get());
    }

    public static boolean open(DrillHandle handle) {
        Optional<DrillData> data = handle.load();
        if (data.isEmpty()) {
            return false;
        }
        Optional<DrillBase> base = DrillCalculator.base(data.get());
        if (base.isEmpty()) {
            handle.player().sendActionBar(Text.plain("Die Basis dieses Bohrers existiert nicht mehr.", NamedTextColor.RED));
            return false;
        }
        DrillMenu menu = new DrillMenu(handle, data.get(), base.get());
        menu.render();
        handle.player().openInventory(menu.inventory);
        return true;
    }

    public static boolean isFuelSlot(int slot) {
        for (int fuelSlot : FUEL_SLOTS) {
            if (fuelSlot == slot) {
                return true;
            }
        }
        return false;
    }

    public DrillHandle handle() {
        return handle;
    }

    public DrillData data() {
        return data;
    }

    public DrillBase base() {
        return base;
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return DrillSlot.byMenuSlot(slot) != null; // the nine install slots; fuel bar + buttons stay display-only
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /** Clamps fuel to the (possibly changed) tank size, saves to the item and redraws. */
    public void persist() {
        double max = DrillCalculator.maxFuel(DrillCalculator.totalStats(data));
        data.setFuel(Math.min(data.fuel(), max));
        handle.save(data);
        render();
    }

    public void render() {
        ItemStack filler = named(Material.GRAY_STAINED_GLASS_PANE, " ", NamedTextColor.GRAY);
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }
        renderFuelBar();
        renderBase();
        inventory.setItem(REFUEL_BUTTON, named(Material.COAL, "Auftanken", NamedTextColor.GOLD, "Klicken, um das Tankmenü zu öffnen"));
        for (DrillSlot slot : DrillSlot.values()) {
            renderSlot(slot);
        }
    }

    private void renderFuelBar() {
        DrillStatSet stats = DrillCalculator.totalStats(data);
        double max = DrillCalculator.maxFuel(stats);
        double ratio = Math.min(1.0, data.fuel() / max);
        String amount = Text.num(data.fuel()) + " / " + Text.num(max) + " (" + Math.round(ratio * 100) + "%)";
        for (int i = 0; i < FUEL_SLOTS.length; i++) {
            boolean lit = ratio > i / 5.0 + 1e-9;
            inventory.setItem(FUEL_SLOTS[i], lit
                    ? named(Material.LIME_STAINED_GLASS_PANE, "Treibstoff", NamedTextColor.GREEN, amount, "Klicken zum Auftanken")
                    : named(Material.RED_STAINED_GLASS_PANE, "Treibstoff", NamedTextColor.RED, amount, "Klicken zum Auftanken"));
        }
    }

    private void renderBase() {
        ItemStack display = new ItemStack(base.icon());
        List<Component> lore = DrillItems.buildLore(data);
        display.editMeta(meta -> {
            meta.displayName(Text.plain(base.displayName(), NamedTextColor.GOLD));
            meta.lore(lore);
        });
        inventory.setItem(BASE_SLOT, display);
    }

    private void renderSlot(DrillSlot slot) {
        if (slot.isModule() && base.isModuleLocked(slot.moduleIndex())) {
            inventory.setItem(slot.menuSlot(), named(Material.BARRIER, "Gesperrt", NamedTextColor.DARK_RED,
                    "Dieser Modulslot ist für diese Bohrerbasis gesperrt."));
            return;
        }
        ItemStack installed = data.installed(slot);
        if (installed != null) {
            inventory.setItem(slot.menuSlot(), installed.clone());
            return;
        }
        Material placeholder = slot.isModule() ? Material.PURPLE_STAINED_GLASS_PANE : Material.LIGHT_BLUE_STAINED_GLASS_PANE;
        inventory.setItem(slot.menuSlot(), named(placeholder, slot.type().displayName(), NamedTextColor.AQUA, "Leerer Slot"));
    }

    private static ItemStack named(Material material, String name, NamedTextColor color, String... lore) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            meta.displayName(Text.plain(name, color));
            if (lore.length > 0) {
                List<Component> lines = new ArrayList<>();
                for (String line : lore) {
                    lines.add(Text.gray(line));
                }
                meta.lore(lines);
            }
        });
        return item;
    }
}
