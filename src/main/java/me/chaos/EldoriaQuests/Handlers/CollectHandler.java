package me.chaos.EldoriaQuests.Handlers;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.Quest.TaskHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class CollectHandler implements TaskHandler<EntityPickupItemEvent> {

    @Override
    public int evaluate(EntityPickupItemEvent event, Player player, Quest task) {
        if (event.getEntity() instanceof Player trigger){
            if (player.equals(trigger)) {
                return event.getItem().getItemStack().getAmount();
            }
        }
        return -1;
    }
}
