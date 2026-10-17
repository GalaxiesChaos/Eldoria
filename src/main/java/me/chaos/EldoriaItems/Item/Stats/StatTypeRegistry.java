package me.chaos.EldoriaItems.Item.Stats;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatTypeRegistry {
    private static final Map<String, StatType> REGISTRY = new LinkedHashMap<>();

    public static StatType register(StatType stat) {
        REGISTRY.put(stat.getKey(), stat);
        return stat;
    }

    public static StatType get(String key) {
        return REGISTRY.get(key.toLowerCase());
    }

    public static Collection<StatType> getAll() {
        return REGISTRY.values();
    }
}
