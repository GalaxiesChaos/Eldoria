package net.eldoria.drill.menu;

import me.chaos.EldoriaAntiDupe.ProtectedGui;
import net.eldoria.drill.base.DrillBase;
import net.eldoria.drill.data.DrillCalculator;
import net.eldoria.drill.data.DrillData;
import net.eldoria.drill.data.DrillItems;
import net.eldoria.drill.fuel.FuelService;
import net.eldoria.drill.fuel.FuelType;
import net.eldoria.drill.fuel.FuelTypeRegistry;
import net.eldoria.drill.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Separate refuel menu: put fuel into the middle slot, it is burned automatically. */
public final class RefuelMenu implements ProtectedGui {

    public static final int SIZE = 27;
    public static final int INFO = 4;
    public static final int INPUT = 13;
    public static final int BACK = 22;

    private final DrillHandle handle;
    private final Inventory inventory;

    private RefuelMenu(DrillHandle handle) {
        this.handle = handle;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Bohrer auftanken", NamedTextColor.DARK_GRAY));
    }

    public static boolean open(Player player) {
        Optional<DrillHandle> handle = DrillHandle.ofMainHand(player);
        return handle.isPresent() && open(handle.get());
    }

    public static boolean open(DrillHandle handle) {
        Optional<DrillData> data = handle.load();
        if (data.isEmpty()) {
            return false;
        }
        RefuelMenu menu = new RefuelMenu(handle);
        menu.render(data.get());
        handle.player().openInventory(menu.inventory);
        return true;
    }

    public DrillHandle handle() {
        return handle;
    }

    @Override
    public boolean isInteractiveSlot(int slot) {
        return slot == INPUT; // only the fuel input slot takes items
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private void render(DrillData data) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        filler.editMeta(meta -> meta.displayName(Text.gray(" ")));
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }
        inventory.setItem(INPUT, null);

        DrillBase base = DrillCalculator.base(data).orElse(null);
        double max = DrillCalculator.maxFuel(DrillCalculator.totalStats(data));
        List<Component> lore = new ArrayList<>();
        lore.add(DrillItems.fuelLine(data.fuel(), max));
        lore.add(Component.empty());
        lore.add(Text.gray("Akzeptierte Treibstoffe:"));
        for (FuelType type : FuelTypeRegistry.all()) {
            if (base == null || base.acceptsFuel(type)) {
                lore.add(Text.plain("- " + type.displayName() + ": +" + Text.num(type.fuelValue()), NamedTextColor.YELLOW));
            }
        }
        ItemStack info = new ItemStack(Material.COAL);
        info.editMeta(meta -> {
            meta.displayName(Text.plain("Bohrer-Treibstoff", NamedTextColor.GOLD));
            meta.lore(lore);
        });
        inventory.setItem(INFO, info);

        ItemStack back = new ItemStack(Material.ARROW);
        back.editMeta(meta -> meta.displayName(Text.plain("Zurück", NamedTextColor.YELLOW)));
        inventory.setItem(BACK, back);
    }

    /** Burns the fuel currently lying in the input slot (call one tick after a click/drag). */
    public void processInput() {
        Player player = handle.player();
        ItemStack input = inventory.getItem(INPUT);
        if (input == null || input.getType().isAir()) {
            return;
        }
        Optional<DrillData> loaded = handle.load();
        if (loaded.isEmpty()) {
            return;
        }
        DrillData data = loaded.get();
        Optional<FuelType> type = FuelTypeRegistry.match(input);
        if (type.isEmpty()) {
            player.sendActionBar(Text.plain("Dieser Gegenstand ist kein Treibstoff.", NamedTextColor.RED));
            return;
        }
        DrillBase base = DrillCalculator.base(data).orElse(null);
        if (base == null || !base.acceptsFuel(type.get())) {
            player.sendActionBar(Text.plain("Diese Bohrerbasis akzeptiert diesen Treibstoff nicht: " + type.get().displayName() + ".", NamedTextColor.RED));
            return;
        }
        double max = DrillCalculator.maxFuel(DrillCalculator.totalStats(data));
        int used = FuelService.refuel(data, max, type.get(), input);
        if (used <= 0) {
            player.sendActionBar(Text.plain("Der Tank ist bereits voll.", NamedTextColor.YELLOW));
            return;
        }
        input.setAmount(input.getAmount() - used);
        inventory.setItem(INPUT, input.getAmount() <= 0 ? null : input);
        handle.save(data);
        render(data, inventory.getItem(INPUT));
        player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 0.6f, 1.2f);
    }

    /** Re-renders the info panel but keeps whatever lies in the input slot. */
    private void render(DrillData data, ItemStack keep) {
        render(data);
        inventory.setItem(INPUT, keep);
    }

    /** Gives back whatever is left in the input slot (menu closed). */
    public void returnInput() {
        ItemStack leftover = inventory.getItem(INPUT);
        inventory.setItem(INPUT, null);
        if (leftover == null || leftover.getType().isAir()) {
            return;
        }
        Player player = handle.player();
        Map<Integer, ItemStack> rest = player.getInventory().addItem(leftover);
        rest.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }
}
