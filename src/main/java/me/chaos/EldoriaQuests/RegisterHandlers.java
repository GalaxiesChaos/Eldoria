package me.chaos.EldoriaQuests;

import me.chaos.EldoriaQuests.Handlers.CollectHandler;
import me.chaos.EldoriaQuests.Handlers.KillEntityHandler;
import me.chaos.EldoriaQuests.Quest.TaskType;
import me.chaos.EldoriaQuests.Quest.TaskTypeHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class RegisterHandlers {
    public final TaskTypeHandler handler = new TaskTypeHandler();

    public RegisterHandlers() {
        handler.register(TaskType.COLLECT, EntityPickupItemEvent.class, new CollectHandler());
        // FIXED: kill quests could never progress because no handler was registered for them
        handler.register(TaskType.KILL_ENTITY, EntityDeathEvent.class, new KillEntityHandler());
    }
}
