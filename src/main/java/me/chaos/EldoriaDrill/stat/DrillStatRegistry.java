package me.chaos.EldoriaDrill.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Open registry of drill stats (registration order = display order). */
public final class DrillStatRegistry {

    private static final Map<String, DrillStat> STATS = new LinkedHashMap<>();

    /** Tank size. */
    public static final DrillStat MAX_FUEL = register(new DrillStat("max_fuel", "Max. Treibstoff", false));
    /** Percent less fuel per block (negative = more fuel per block). Capped at 90. */
    public static final DrillStat FUEL_EFFICIENCY = register(new DrillStat("fuel_efficiency", "Treibstoffeffizienz", true));
    /** Haste level applied while holding the drill (0 = none). */
    public static final DrillStat MINING_SPEED = register(new DrillStat("mining_speed", "Abbaugeschwindigkeit", false));
    /** Mining drop chance, exposed to the drop system via DrillBreakEvent (not applied here). */
    public static final DrillStat DROP_CHANCE = register(new DrillStat("drop_chance", "Dropchance", true));
    /** Multiplies drop stack sizes; consumed by DrillDropEnhancements (100 = double drops). */
    public static final DrillStat MINING_FORTUNE = register(new DrillStat("mining_fortune", "Bergbaugl\u00fcck", true));
    /** Breaks additional adjacent blocks of the same type while drilling; consumed by MiningListener. */
    public static final DrillStat MINING_SPREAD = register(new DrillStat("mining_spread", "Abbaufl\u00e4che", false));
    /** Reserved for the plates. No built-in behaviour yet. */
    public static final DrillStat REINFORCEMENT = register(new DrillStat("reinforcement", "Verstärkung", false));

    private DrillStatRegistry() {
    }

    public static DrillStat register(DrillStat stat) {
        STATS.put(stat.id(), stat);
        return stat;
    }

    public static Optional<DrillStat> get(String id) {
        return Optional.ofNullable(STATS.get(id));
    }

    public static Collection<DrillStat> all() {
        return Collections.unmodifiableCollection(STATS.values());
    }
}
