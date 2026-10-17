package me.chaos.EldoriaDrill.upgrade;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Open registry of upgrades and modules. */
public final class DrillUpgradeRegistry {

    private static final Map<String, DrillUpgrade> UPGRADES = new LinkedHashMap<>();

    private DrillUpgradeRegistry() {
    }

    public static DrillUpgrade register(DrillUpgrade upgrade) {
        UPGRADES.put(upgrade.id(), upgrade);
        return upgrade;
    }

    public static Optional<DrillUpgrade> get(String id) {
        return Optional.ofNullable(UPGRADES.get(id));
    }

    public static Collection<DrillUpgrade> all() {
        return Collections.unmodifiableCollection(UPGRADES.values());
    }

    public static Set<String> ids() {
        return Collections.unmodifiableSet(UPGRADES.keySet());
    }
}
