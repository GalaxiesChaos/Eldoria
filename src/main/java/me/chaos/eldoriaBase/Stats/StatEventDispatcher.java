package me.chaos.eldoriaBase.Stats;

import me.chaos.eldoriaBase.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Registers every {@link Stat} in the {@link StatRegistry} as a listener for the event
 * classes it declared via {@link Stat#getListenedEvents()}. This uses Bukkit's dynamic
 * event registration ({@code PluginManager#registerEvent}) instead of {@code @EventHandler}
 * methods, since the set of (stat -> event class) pairs is only known at runtime.
 * <p>
 * Call {@link #registerAll()} once from {@code Main#onEnable()}, after {@code StatLoader}
 * has populated the {@link StatRegistry}.
 */
public class StatEventDispatcher implements Listener {

    private final Main main;

    public StatEventDispatcher(Main main) {
        this.main = main;
    }

    public void registerAll() {
        for (Stat stat : StatRegistry.getAll()) {
            for (Class<? extends Event> eventClass : stat.getListenedEvents()) {
                register(stat, eventClass);
            }
        }
    }

    private void register(Stat stat, Class<? extends Event> eventClass) {
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                this,
                EventPriority.NORMAL,
                (listener, event) -> {
                    if (eventClass.isInstance(event)) {
                        stat.onEvent(event);
                    }
                },
                main,
                true // ignoreCancelled: if the event is Cancellable and got canceled (e.g. a protection
                // plugin denying a block break), the stat effect is skipped entirely. This is a
                // sensible default for all current stats; switch to false here if a future stat
                // needs to run even on canceled events.
        );
    }
}