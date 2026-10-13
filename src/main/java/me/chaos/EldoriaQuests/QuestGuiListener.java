package me.chaos.EldoriaQuests;

import me.chaos.EldoriaQuests.Interface.QuestConfirmInv;
import me.chaos.EldoriaQuests.Interface.QuestSelectionInv;
import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class QuestGuiListener implements Listener {

    private final QuestHandler questHandler;
    private final PlayerDataHandler playerDataHandler;

    public QuestGuiListener(QuestHandler questHandler, PlayerDataHandler playerDataHandler) {
        this.questHandler = questHandler;
        this.playerDataHandler = playerDataHandler;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory().getHolder() instanceof QuestSelectionInv selectionInv) {
            event.setCancelled(true);
            handleSelectionClick(player, selectionInv, event.getSlot());
        } else if (event.getClickedInventory().getHolder() instanceof QuestConfirmInv confirmInv) {
            event.setCancelled(true);
            handleConfirmClick(player, confirmInv, event.getSlot());
        }
    }

    private void handleSelectionClick(Player player, QuestSelectionInv selectionInv, int slot) {
        if (Arrays.stream(QuestSelectionInv.QUEST_SLOTS).noneMatch(s -> s == slot)) return;

        Quest quest = selectionInv.getQuest(slot);
        if (quest == null) return; // already accepted, nothing to do

        QuestConfirmInv confirmInv = new QuestConfirmInv(quest, selectionInv, slot);
        player.openInventory(confirmInv.getInventory());
    }

    private void handleConfirmClick(Player player, QuestConfirmInv confirmInv, int slot) {
        try {
            if (slot == QuestConfirmInv.ACCEPT_SLOT) {
                acceptQuest(player, confirmInv);
            } else if (slot == QuestConfirmInv.REROLL_SLOT) {
                rerollQuest(player, confirmInv);
            } else {
                return;
            }
        } catch (DefaultQuestExeption e) {
            player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
        }

        player.openInventory(confirmInv.getOrigin().getInventory());
    }

    private void acceptQuest(Player player, QuestConfirmInv confirmInv) throws DefaultQuestExeption {
        Quest quest = confirmInv.getQuest();
        if (quest == null) {
            throw new DefaultQuestExeption("Quest konnte nicht angenommen werden.");
        }

        // FIXED: accepted quests are now persisted on the player instead of only
        // changing the GUI item (old TODO). Progress is tracked on these instances.
        var data = playerDataHandler.getPlayerData(player);
        if (data == null || data.getPlayerQuestManager() == null) {
            throw new DefaultQuestExeption("Spielerdaten sind noch nicht geladen.");
        }
        data.getPlayerQuestManager().add(quest);

        confirmInv.getOrigin().markAccepted(confirmInv.getOriginSlot(), quest);
        player.sendMessage(Component.text("Quest angenommen: " + quest.getTitle()).color(NamedTextColor.GREEN));
    }

    private void rerollQuest(Player player, QuestConfirmInv confirmInv) throws DefaultQuestExeption {
        if (!questHandler.hasRerollsLeft(player)) {
            player.sendMessage(Component.text("Keine Rerolls mehr übrig.").color(NamedTextColor.RED));
            return;
        }

        QuestSelectionInv origin = confirmInv.getOrigin();
        Quest newQuest = QuestRegistry.getRandomQuest(origin.getCurrentQuestIds());

        origin.placeQuest(confirmInv.getOriginSlot(), newQuest);
        questHandler.useReroll(player);

        player.sendMessage(Component.text("Neue Quest: " + newQuest.getTitle()).color(NamedTextColor.GOLD));
    }
}
