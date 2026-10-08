package me.chaos.EldoriaEnchants;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnchantRegistry {
    private static final Map<String, CustomEnchant> REGISTRY = new LinkedHashMap<>();

    private EnchantRegistry() {}

    public static void register(CustomEnchant enchant) {
        if (REGISTRY.containsKey(enchant.getId())) {
            throw new IllegalStateException("Duplicate enchant id: " + enchant.getId());
        }
        REGISTRY.put(enchant.getId(), enchant);
    }

    public static CustomEnchant get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<CustomEnchant> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    /** Every registered enchant whose target matches the given item. */
    public static List<CustomEnchant> getApplicable(ItemStack item) {
        return REGISTRY.values().stream().filter(e -> e.canApplyTo(item)).toList();
    }
}
