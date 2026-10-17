package me.chaos.EldoriaItems.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomItemRegistry {
    private static final Map<String, CustomItemTemplate> REGISTRY = new LinkedHashMap<>();

    public static void register(CustomItemTemplate template) {
        REGISTRY.put(template.getId(), template);
    }

    public static CustomItemTemplate get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<CustomItemTemplate> getAll() {
        return REGISTRY.values();
    }
}
