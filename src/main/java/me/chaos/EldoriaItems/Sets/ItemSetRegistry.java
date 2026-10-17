package me.chaos.EldoriaItems.Sets;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemSetRegistry {
    private static final Map<String, ItemSet> REGISTRY = new LinkedHashMap<>();

    public static void register(ItemSet set) {
        REGISTRY.put(set.id(), set);
    }

    public static ItemSet get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<ItemSet> getAll() {
        return REGISTRY.values();
    }
}
