package net.eldoria.drill.base;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Open registry of drill bases. Register your own bases at startup. */
public final class DrillBaseRegistry {

    private static final Map<String, DrillBase> BASES = new LinkedHashMap<>();

    private DrillBaseRegistry() {
    }

    public static DrillBase register(DrillBase base) {
        BASES.put(base.id(), base);
        return base;
    }

    public static Optional<DrillBase> get(String id) {
        return Optional.ofNullable(BASES.get(id));
    }

    public static Collection<DrillBase> all() {
        return Collections.unmodifiableCollection(BASES.values());
    }

    public static Set<String> ids() {
        return Collections.unmodifiableSet(BASES.keySet());
    }
}
