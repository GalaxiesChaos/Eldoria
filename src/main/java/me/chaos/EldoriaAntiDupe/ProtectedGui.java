package me.chaos.EldoriaAntiDupe;

import org.bukkit.inventory.InventoryHolder;

/**
 * Marker for Eldoria menus that are NOT real storage. {@code GuiExploitListener} blocks every
 * way of moving items into/out of such a menu (shift-click, double-click collect, drag, number
 * keys, offhand swap) except on the slots returned as interactive here.
 * <p>
 * Usage: let the menu class implement this instead of plain InventoryHolder and override
 * {@link #isInteractiveSlot(int)} if players may place/take items in specific slots.
 */
public interface ProtectedGui extends InventoryHolder {

    /** Slots (raw slot index in the menu) where players may freely place/take items. */
    default boolean isInteractiveSlot(int slot) {
        return false;
    }
}
