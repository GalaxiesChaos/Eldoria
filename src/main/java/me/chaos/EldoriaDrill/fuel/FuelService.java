package me.chaos.EldoriaDrill.fuel;

import me.chaos.EldoriaDrill.data.DrillData;
import org.bukkit.inventory.ItemStack;

public final class FuelService {

    private FuelService() {
    }

    /**
     * Burns as many items of the stack as needed to fill the tank (the last item may be partly wasted).
     *
     * @return number of items consumed; the caller removes them from the stack
     */
    public static int refuel(DrillData data, double maxFuel, FuelType type, ItemStack stack) {
        double room = maxFuel - data.fuel();
        if (room <= 1e-9 || stack.getAmount() <= 0 || type.fuelValue() <= 0) {
            return 0;
        }
        int needed = (int) Math.ceil(room / type.fuelValue() - 1e-9);
        int used = Math.min(stack.getAmount(), Math.max(1, needed));
        data.setFuel(Math.min(maxFuel, data.fuel() + used * type.fuelValue()));
        return used;
    }
}
