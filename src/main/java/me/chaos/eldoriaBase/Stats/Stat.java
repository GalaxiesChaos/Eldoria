package me.chaos.eldoriaBase.Stats;

import org.bukkit.event.Event;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Base class for every stat in the game (mining stats, combat stats, ...).
 * <p>
 * A Stat bundles three things, exactly as requested:
 * <ol>
 *     <li>What it applies to - {@link #getApplicability()} (blocks / items / entities).</li>
 *     <li>What it does - {@link #onEvent(Event)}, fired for every event class in {@link #getListenedEvents()}.
 *     Registration happens dynamically via {@link StatEventDispatcher}, so a Stat behaves like its own
 *     tiny custom listener without needing an {@code @EventHandler} method or its own Listener class.</li>
 *     <li>How its value is interpreted - {@link #getValueType()} (per unit, or chance-out-of-max).</li>
 * </ol>
 * To add a new stat: extend this class, implement {@link #onEvent(Event)}, then register an instance
 * with {@link StatRegistry#register(Stat)} (see {@link me.chaos.eldoriaBase.StatLoader}).
 */
public abstract class Stat {

    private final String id;
    private final String displayName;
    private final StatValueType valueType;
    private final double maxValue;
    private final StatApplicability applicability;
    private final List<Class<? extends Event>> listenedEvents;

    /**
     * @param id             unique, stable identifier (also used as the PersistentDataContainer / save key,
     *                       so avoid renaming an id once players own items with it)
     * @param displayName    human-readable name, for later use in stat menus / lore
     * @param valueType      {@link StatValueType#PER_UNIT} or {@link StatValueType#CHANCE}
     * @param maxValue       only relevant for {@link StatValueType#CHANCE} stats - how much value equals
     *                       one "guaranteed unit" (e.g. 100 for a percentage-style dropchance). Ignored for
     *                       {@link StatValueType#PER_UNIT} stats, pass 0 there.
     * @param applicability  what this stat is allowed to affect
     * @param listenedEvents which Bukkit events this stat needs to react to
     */
    protected Stat(String id,
                   String displayName,
                   StatValueType valueType,
                   double maxValue,
                   StatApplicability applicability,
                   List<Class<? extends Event>> listenedEvents) {
        this.id = id;
        this.displayName = displayName;
        this.valueType = valueType;
        this.maxValue = maxValue;
        this.applicability = applicability;
        this.listenedEvents = List.copyOf(listenedEvents);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public StatValueType getValueType() {
        return valueType;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public StatApplicability getApplicability() {
        return applicability;
    }

    public List<Class<? extends Event>> getListenedEvents() {
        return listenedEvents;
    }

    /**
     * Called by the {@link StatEventDispatcher} for every fired event whose class is in
     * {@link #getListenedEvents()}. Implementations are responsible for figuring out the
     * relevant player, checking {@link #getApplicability()} themselves, looking up the
     * player's aggregated value (via {@code main.getHandler().getStatManager()}) and
     * applying the effect.
     */
    public abstract void onEvent(Event event);

    /**
     * Helper for {@link StatValueType#CHANCE} stats: turns an aggregated stat value into a
     * drop/roll multiplier. With {@code maxValue = 100}: a value of 50 -> 50% chance of an
     * extra unit (multiplier 1 or 2), 150 -> guaranteed +1 and a 50% chance of a second +1
     * (multiplier 2 or 3), and so on.
     */
    protected final int rollChanceMultiplier(double value) {
        if (valueType != StatValueType.CHANCE) {
            throw new IllegalStateException("rollChanceMultiplier() can only be used on CHANCE-type stats");
        }
        if (value <= 0 || maxValue <= 0) {
            return 1;
        }

        int guaranteed = (int) Math.floor(value / maxValue);
        double remainderChance = (value % maxValue) / maxValue;

        int extra = guaranteed;
        if (ThreadLocalRandom.current().nextDouble() < remainderChance) {
            extra++;
        }
        return 1 + extra;
    }
}