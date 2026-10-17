package me.chaos.EldoriaItems.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemTypeRegistry {
    private static final Map<String, ItemType> REGISTRY = new LinkedHashMap<>();

    public static ItemType register(ItemType type) {
        REGISTRY.put(type.getKey(), type);
        return type;
    }

    public static ItemType get(String key) {
        return REGISTRY.get(key.toLowerCase());
    }

    public static Collection<ItemType> getAll() {
        return REGISTRY.values();
    }
}
