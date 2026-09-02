package me.chaos.EldoriaQuests.Handlers;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.Quest.TaskHandler;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class CollectHandler implements TaskHandler<EntityPickupItemEvent> {

    @Override
    public int evaluate(EntityPickupItemEvent event, Player player, Quest task) {
        if (event.getEntity() instanceof Player trigger){
            if (player.equals(trigger)) {
                Item item = event.getItem();
                if (task != null && task.getMeta() != null) {
                    String Material = task.getMeta().get("item");
                    if (item.getItemStack().getType().toString().equals(Material)){
                        return item.getItemStack().getAmount();
                    }
                }
            }
        }
        return -1;
    }
}

