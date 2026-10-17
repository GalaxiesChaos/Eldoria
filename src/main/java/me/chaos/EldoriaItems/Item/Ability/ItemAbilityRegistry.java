package me.chaos.EldoriaItems.Item.Ability;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemAbilityRegistry {
    private static final Map<String, ItemAbility> REGISTRY = new LinkedHashMap<>();

    public static ItemAbility register(ItemAbility ability) {
        REGISTRY.put(ability.getId(), ability);
        return ability;
    }

    public static ItemAbility get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<ItemAbility> getAll() {
        return REGISTRY.values();
    }
}
