package net.eldoria.drill.fuel;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Open registry of fuel types. */
public final class FuelTypeRegistry {

    private static final Map<String, FuelType> TYPES = new LinkedHashMap<>();

    private FuelTypeRegistry() {
    }

    public static FuelType register(FuelType type) {
        TYPES.put(type.id(), type);
        return type;
    }

    public static Optional<FuelType> get(String id) {
        return Optional.ofNullable(TYPES.get(id));
    }

    public static Collection<FuelType> all() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    public static Optional<FuelType> match(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return Optional.empty();
        }
        for (FuelType type : TYPES.values()) {
            if (type.matches(stack)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
