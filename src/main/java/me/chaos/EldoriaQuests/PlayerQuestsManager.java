package me.chaos.EldoriaQuests;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerQuestsManager implements PlayerDataHolder {
    private List<Quest> Quests = new ArrayList<>();
    // FIXED: static now — needed for PlayerDataRegistry registration and codec access
    public static final String SAVE_KEY = "PlayerQuestManager";
    private static final int MAX = 5;

    public PlayerQuestsManager(Main main, Player player) {
        append(main, player);
    }

    private PlayerQuestsManager(List<Quest> quests) {
        this.Quests = new ArrayList<>(quests);
    }

    // FIXED: static final, consistent with every other codec in the project
    public static final Codec<PlayerQuestsManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Quest.CODEC.listOf().fieldOf("Quests").forGetter(PlayerQuestsManager::getQuests)
    ).apply(instance, PlayerQuestsManager::new));

    public void add(Quest quest) throws DefaultQuestExeption {
        // FIXED: off-by-one — `>` allowed MAX+1 quests
        if (Quests.size() >= MAX) {
            throw new DefaultQuestExeption("Zu viele Quests vorhanden");
        }
        Quests.add(quest);
    }

    /** Removes a completed (or abandoned) quest from the active list. */
    public void remove(Quest quest) {
        Quests.remove(quest);
    }

    public Quest getQuest(String ID) {
        for (Quest quest : Quests) {
            if (quest.getId().equalsIgnoreCase(ID)) {
                return quest;
            }
        }
        return null;
    }

    /** FIXED: returns ALL active quests of a type (previously only the first), since
     *  a player can hold several quests that share a task type. */
    public List<Quest> getByType(String Type) {
        return Quests.stream()
                .filter(q -> q.getTaskType().equalsIgnoreCase(Type))
                .toList();
    }

    private List<Quest> getQuests() {
        return Quests;
    }

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
