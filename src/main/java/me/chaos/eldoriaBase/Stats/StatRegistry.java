package me.chaos.eldoriaBase.Stats;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds every registered {@link Stat}. Mirrors {@code PlayerDataRegistry} / {@code QuestRegistry}.
 */
public class StatRegistry {

    private static final Map<String, Stat> STATS = new LinkedHashMap<>();

    public static void register(Stat stat) {
        STATS.put(stat.getId(), stat);
    }

    public static Stat get(String id) {
        return STATS.get(id);
    }

    public static Collection<Stat> getAll() {
        return Collections.unmodifiableCollection(STATS.values());
    }
}