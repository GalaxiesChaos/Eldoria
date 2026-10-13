package me.chaos.EldoriaQuests;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.eldoriaBase.Handlers;
import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.List;

public class QuestListeners implements Listener {
    private final Handlers handlers;

    public QuestListeners(Main main) {
        handlers = main.getHandler();
    }

    @EventHandler
    public void onCollectEvent(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        // FIXED: previously crashed with NPE when player data / quest manager was
        // missing, and only ever evaluated the FIRST collect quest
        for (Quest quest : activeQuests(player, "COLLECT")) {
            int progress = handlers.getRegisterHandlers().handler.evaluate(e, player, quest);
            if (progress > 0) applyProgress(player, quest, progress);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        for (Quest quest : activeQuests(killer, "KILL_ENTITY")) {
            int progress = handlers.getRegisterHandlers().handler.evaluate(e, killer, quest);
            if (progress > 0) applyProgress(killer, quest, progress);
        }
    }

    private List<Quest> activeQuests(Player player, String type) {
        var data = handlers.getPlayerDataHandler().getPlayerData(player);
        if (data == null) return List.of();
        var manager = data.getPlayerQuestManager();
        if (manager == null) return List.of();
        return manager.getByType(type);
    }

    /** FIXED: the progress value returned by the task handlers was previously
     *  discarded entirely — quests could never advance, complete, or pay out. */
    private void applyProgress(Player player, Quest quest, int amount) {
        quest.addProgress(amount);
        if (quest.getProgress() < quest.getGoal()) return;

        var data = handlers.getPlayerDataHandler().getPlayerData(player);
        if (data != null) {
            var money = data.getMoney();
            if (money != null && quest.getCashReward() != null && quest.getCashReward() > 0) {
                money.addMoney(quest.getCashReward());
            }
            var manager = data.getPlayerQuestManager();
            if (manager != null) {
                manager.remove(quest);
            }
        }
        player.sendMessage(Component.text("Quest abgeschlossen: " + quest.getTitle(), NamedTextColor.GREEN));
    }
}
