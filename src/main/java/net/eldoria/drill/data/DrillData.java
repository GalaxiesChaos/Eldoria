package net.eldoria.drill.data;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/** Mutable per-item state of a drill. Persisted into the item's PDC by {@link DrillItems}. */
public final class DrillData {

    private final UUID id;
    private final String baseId;
    private double fuel;
    private final Map<DrillSlot, ItemStack> installed = new EnumMap<>(DrillSlot.class);

    public DrillData(UUID id, String baseId) {
        this.id = id;
        this.baseId = baseId;
    }

    public UUID id() {
        return id;
    }

    public String baseId() {
        return baseId;
    }

    public double fuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = Math.max(0, fuel);
    }

    /** The installed upgrade item (a 1-item stack) or null. */
    public ItemStack installed(DrillSlot slot) {
        return installed.get(slot);
    }

    public void setInstalled(DrillSlot slot, ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            installed.remove(slot);
        } else {
            installed.put(slot, stack);
        }
    }

    public Map<DrillSlot, ItemStack> installedView() {
        return Collections.unmodifiableMap(installed);
    }
}
