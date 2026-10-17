package me.chaos.EldoriaItems.Item.Enchant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomEnchantmentRegistry {
    private static final Map<String, CustomEnchantment> REGISTRY = new LinkedHashMap<>();

    public static CustomEnchantment register(CustomEnchantment enchantment) {
        REGISTRY.put(enchantment.getId(), enchantment);
        return enchantment;
    }

    public static CustomEnchantment get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<CustomEnchantment> getAll() {
        return REGISTRY.values();
    }
}
