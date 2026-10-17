package me.chaos.EldoriaDrill.listener;

import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.data.DrillSlot;
import me.chaos.EldoriaDrill.menu.DrillHandle;
import me.chaos.EldoriaDrill.menu.DrillMenu;
import me.chaos.EldoriaDrill.menu.RefuelMenu;
import me.chaos.EldoriaDrill.upgrade.DrillUpgrade;
import me.chaos.EldoriaDrill.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Click handling for both menus. The drill menu is fully manual (every click is cancelled and
 * re-implemented), the refuel menu only lets the input slot behave normally.
 * The held drill is protected from being moved while a menu is open.
 */
public final class MenuListener implements Listener {

    private final JavaPlugin plugin;

    public MenuListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof DrillMenu drillMenu) {
            onDrillMenuClick(event, player, drillMenu);
        } else if (holder instanceof RefuelMenu refuelMenu) {
            onRefuelMenuClick(event, player, refuelMenu);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        int topSize = top.getSize();
        InventoryHolder holder = top.getHolder();
        if (holder instanceof DrillMenu) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < topSize)) {
                event.setCancelled(true);
            }
        } else if (holder instanceof RefuelMenu refuelMenu) {
            boolean forbidden = event.getRawSlots().stream().anyMatch(slot -> slot < topSize && slot != RefuelMenu.INPUT);
            if (forbidden) {
                event.setCancelled(true);
            } else if (event.getRawSlots().contains(RefuelMenu.INPUT)) {
                scheduleRefuel(refuelMenu);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof RefuelMenu refuelMenu) {
            refuelMenu.returnInput();
        }
    }

    // ------------------------------------------------------------------ drill menu

    private void onDrillMenuClick(InventoryClickEvent event, Player player, DrillMenu menu) {
        if (!menu.handle().valid()) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return; // click outside the window
        }
        if (clicked.equals(event.getView().getTopInventory())) {
            event.setCancelled(true);
            onTopClick(event, player, menu);
            return;
        }
        // player inventory
        ItemStack current = event.getCurrentItem();
        if (isThisDrill(current, menu.handle()) || swapsDrill(event, menu.handle())) {
            event.setCancelled(true);
            return;
        }
        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            if (current != null && !current.getType().isAir()) {
                insertUpgrade(event, player, menu, current);
            }
        }
    }

    private void onTopClick(InventoryClickEvent event, Player player, DrillMenu menu) {
        int raw = event.getRawSlot();
        DrillSlot slot = DrillSlot.byMenuSlot(raw);
        if (slot != null) {
            onUpgradeSlotClick(event, player, menu, slot);
            return;
        }
        if (raw == DrillMenu.REFUEL_BUTTON || DrillMenu.isFuelSlot(raw)) {
            Bukkit.getScheduler().runTask(plugin, () -> RefuelMenu.open(menu.handle()));
        }
    }

    private void onUpgradeSlotClick(InventoryClickEvent event, Player player, DrillMenu menu, DrillSlot slot) {
        DrillData data = menu.data();
        DrillBase base = menu.base();
        if (slot.isModule() && base.isModuleLocked(slot.moduleIndex())) {
            player.sendActionBar(Text.plain("Dieser Modulslot ist für diese Bohrerbasis gesperrt.", NamedTextColor.RED));
            return;
        }
        ItemStack installed = data.installed(slot);
        ItemStack cursor = event.getCursor();
        boolean changed = false;

        switch (event.getClick()) {
            case LEFT, RIGHT -> {
                if (isEmpty(cursor)) {
                    if (installed != null) {
                        player.setItemOnCursor(installed);
                        data.setInstalled(slot, null);
                        changed = true;
                    }
                } else if (accepts(player, base, slot, cursor)) {
                    ItemStack one = cursor.clone();
                    one.setAmount(1);
                    if (installed == null) {
                        data.setInstalled(slot, one);
                        ItemStack rest = cursor.clone();
                        rest.setAmount(cursor.getAmount() - 1);
                        player.setItemOnCursor(rest.getAmount() <= 0 ? null : rest);
                        changed = true;
                    } else if (cursor.getAmount() == 1) {
                        data.setInstalled(slot, one);
                        player.setItemOnCursor(installed);
                        changed = true;
                    }
                }
            }
            case SHIFT_LEFT, SHIFT_RIGHT -> {
                if (installed != null && player.getInventory().addItem(installed.clone()).isEmpty()) {
                    data.setInstalled(slot, null);
                    changed = true;
                }
            }
            default -> {
            }
        }

        if (changed) {
            menu.persist();
            player.updateInventory();
        }
    }

    /** Shift-click from the player inventory: put the upgrade into the first free fitting slot. */
    private void insertUpgrade(InventoryClickEvent event, Player player, DrillMenu menu, ItemStack stack) {
        Optional<DrillUpgrade> upgrade = DrillItems.upgradeOf(stack);
        if (upgrade.isEmpty()) {
            player.sendActionBar(Text.plain("Das ist kein Bohrer-Upgrade.", NamedTextColor.RED));
            return;
        }
        DrillData data = menu.data();
        DrillBase base = menu.base();
        if (!upgrade.get().allowedOn(base)) {
            player.sendActionBar(Text.plain("Dieses Upgrade passt nicht zu dieser Bohrerbasis.", NamedTextColor.RED));
            return;
        }
        for (DrillSlot slot : DrillSlot.values()) {
            if (slot.type() != upgrade.get().slotType()
                    || data.installed(slot) != null
                    || (slot.isModule() && base.isModuleLocked(slot.moduleIndex()))) {
                continue;
            }
            ItemStack one = stack.clone();
            one.setAmount(1);
            data.setInstalled(slot, one);
            ItemStack rest = stack.clone();
            rest.setAmount(stack.getAmount() - 1);
            event.setCurrentItem(rest.getAmount() <= 0 ? null : rest);
            menu.persist();
            player.updateInventory();
            return;
        }
        player.sendActionBar(Text.plain("Kein freier Slot für: " + upgrade.get().slotType().displayName() + ".", NamedTextColor.RED));
    }

    private boolean accepts(Player player, DrillBase base, DrillSlot slot, ItemStack stack) {
        Optional<DrillUpgrade> upgrade = DrillItems.upgradeOf(stack);
        if (upgrade.isEmpty()) {
            player.sendActionBar(Text.plain("Das ist kein Bohrer-Upgrade.", NamedTextColor.RED));
            return false;
        }
        if (upgrade.get().slotType() != slot.type()) {
            player.sendActionBar(Text.plain("Das gehört in einen Slot vom Typ: " + upgrade.get().slotType().displayName() + ".", NamedTextColor.RED));
            return false;
        }
        if (!upgrade.get().allowedOn(base)) {
            player.sendActionBar(Text.plain("Dieses Upgrade passt nicht zu dieser Bohrerbasis.", NamedTextColor.RED));
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ refuel menu

    private void onRefuelMenuClick(InventoryClickEvent event, Player player, RefuelMenu menu) {
        if (!menu.handle().valid()) {
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return;
        }
        if (clicked.equals(event.getView().getTopInventory())) {
            int raw = event.getRawSlot();
            if (raw == RefuelMenu.BACK) {
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(plugin, () -> DrillMenu.open(menu.handle()));
                return;
            }
            if (raw != RefuelMenu.INPUT || swapsDrill(event, menu.handle())) {
                event.setCancelled(true);
                return;
            }
            scheduleRefuel(menu);
        } else {
            if (isThisDrill(event.getCurrentItem(), menu.handle()) || swapsDrill(event, menu.handle())) {
                event.setCancelled(true);
                return;
            }
            if (event.getClick().isShiftClick()) {
                // Moved here: the anti-dupe GUI guard cancels the vanilla shift-click move at LOW priority,
                // so we move the stack into the input slot manually (same idea as insertUpgrade in the drill menu).
                event.setCancelled(true);
                ItemStack shiftStack = event.getCurrentItem();
                if (!isEmpty(shiftStack) && isEmpty(menu.getInventory().getItem(RefuelMenu.INPUT))) {
                    menu.getInventory().setItem(RefuelMenu.INPUT, shiftStack.clone());
                    event.setCurrentItem(null);
                }
                scheduleRefuel(menu);
            }
        }
    }

    private void scheduleRefuel(RefuelMenu menu) {
        Bukkit.getScheduler().runTask(plugin, menu::processInput);
    }

    // ------------------------------------------------------------------ helpers

    private static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType().isAir() || stack.getAmount() <= 0;
    }

    private static boolean isThisDrill(ItemStack stack, DrillHandle handle) {
        return handle.id().equals(DrillItems.idOf(stack));
    }

    /** Number-key swap that would move the protected drill out of its hotbar slot. */
    private static boolean swapsDrill(InventoryClickEvent event, DrillHandle handle) {
        return event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() == handle.slot();
    }
}
