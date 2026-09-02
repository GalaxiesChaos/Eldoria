package me.chaos.EldoriaQuests;

import me.chaos.EldoriaQuests.Handlers.CollectHandler;
import me.chaos.EldoriaQuests.Quest.TaskType;
import me.chaos.EldoriaQuests.Quest.TaskTypeHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class RegisterHandlers {
    TaskTypeHandler handler = new TaskTypeHandler();

    public RegisterHandlers(){
        handler.register(TaskType.COLLECT, EntityPickupItemEvent.class, new CollectHandler());
    }

}

