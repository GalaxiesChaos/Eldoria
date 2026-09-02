package me.chaos.EldoriaQuests.Quest;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@FunctionalInterface
public interface TaskHandler<E extends Event> {
    /** Returns progress to add (-1 if this event doesn't apply to this task instance) */
    int evaluate(E event, Player player, Quest task);
}
