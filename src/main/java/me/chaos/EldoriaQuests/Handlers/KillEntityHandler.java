package me.chaos.EldoriaQuests.Handlers;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.Quest.TaskHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEntityHandler implements TaskHandler<EntityDeathEvent> {

    @Override
    public int evaluate(EntityDeathEvent event, Player player, Quest task) {
        if (event.getEntity().getKiller() == null) return -1;
        Player killer = event.getEntity().getKiller();
        if (!player.equals(killer)) return -1;

        if (task != null && task.getMeta() != null) {
            String entityType = task.getMeta().get("entity");
            if (entityType != null && !entityType.isEmpty()) {
                if (!event.getEntity().getType().toString().equalsIgnoreCase(entityType)) {
                    return -1;
                }
            }
        }

        return 1;
    }

}
