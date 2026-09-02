package me.chaos.EldoriaQuests.Quest;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.EnumMap;
import java.util.Map;

/**
 * Maps each {@link TaskType} to the {@link TaskHandler} responsible for it,
 * and dispatches fired Bukkit events to the right handler for a given quest.
 */
public class TaskTypeHandler {

    /**
     * Pairs a handler with the exact event class it expects, so we can check
     * the event type safely instead of relying on an unchecked cast.
     */
    private record Registration<E extends Event>(Class<E> eventClass, TaskHandler<E> handler) {
        int evaluate(Event event, Player player, Quest quest) {
            if (!eventClass.isInstance(event)) {
                return -1;
            }
            return handler.evaluate(eventClass.cast(event), player, quest);
        }
    }

    private final Map<TaskType, Registration<?>> handlers = new EnumMap<>(TaskType.class);

    /**
     * Registers the handler for a task type, along with the Bukkit event
     * class it listens to. Registering again for the same type replaces the
     * previous handler.
     */
    public <E extends Event> void register(TaskType type, Class<E> eventClass, TaskHandler<E> handler) {
        handlers.put(type, new Registration<>(eventClass, handler));
    }

    public boolean hasHandler(TaskType type) {
        return handlers.containsKey(type);
    }

    /**
     * Runs the handler registered for the quest's task type against the
     * fired event and returns the progress to add.
     *
     * @return -1 if no handler is registered for the quest's task type, or if
     *         the event isn't the type that handler expects.
     */
    public int evaluate(Event event, Player player, Quest quest) {
        Registration<?> registration = handlers.get(quest.getType());
        if (registration == null) {
            return -1;
        }
        return registration.evaluate(event, player, quest);
    }
}

