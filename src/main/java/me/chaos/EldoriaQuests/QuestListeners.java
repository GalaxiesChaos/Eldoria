package me.chaos.EldoriaQuests;

import me.chaos.eldoriaBase.Handlers;
import me.chaos.eldoriaBase.Main;
import net.minecraft.network.chat.OutgoingChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class QuestListeners implements Listener {
    Handlers handlers;

    public QuestListeners(Main main){
        handlers = main.getHandler();
    }

    @EventHandler
    public void onCollectEvent(EntityPickupItemEvent e){
        if (e.getEntity() instanceof Player player) {

            handlers.getRegisterHandlers().handler.evaluate(e, player,
                    handlers.getPlayerDataHandler().getPlayerData(player).getPlayerQuestManager().getByType("COLLECT"));
        }


    }



}
