package net.eldoria.drill.menu;

import net.eldoria.drill.data.DrillData;
import net.eldoria.drill.data.DrillItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

/** Points at one specific drill item in a player's hotbar (slot + instance id). */
public final class DrillHandle {

    private final Player player;
    private final int slot;
    private final UUID id;

    private DrillHandle(Player player, int slot, UUID id) {
        this.player = player;
        this.slot = slot;
        this.id = id;
    }

    /** The drill currently held in the main hand, if any. */
    public static Optional<DrillHandle> ofMainHand(Player player) {
        int slot = player.getInventory().getHeldItemSlot();
        UUID id = DrillItems.idOf(player.getInventory().getItem(slot));
        return id == null ? Optional.empty() : Optional.of(new DrillHandle(player, slot, id));
    }

    public Player player() {
        return player;
    }

    public int slot() {
        return slot;
    }

    public UUID id() {
        return id;
    }

    /** True while the same drill still sits in the same slot. */
    public boolean valid() {
        return player.isOnline() && id.equals(DrillItems.idOf(player.getInventory().getItem(slot)));
    }

    public Optional<DrillData> load() {
        return valid() ? DrillItems.read(player.getInventory().getItem(slot)) : Optional.empty();
    }

    public void save(DrillData data) {
        if (!valid()) {
            return;
        }
        ItemStack item = player.getInventory().getItem(slot);
        DrillItems.write(item, data);
        player.getInventory().setItem(slot, item);
    }
}
