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
    private final String SAVE_KEY = "PlayerQuestManager";
    private final int MAX = 5;


    public PlayerQuestsManager(Main main, Player player){
        append(main, player);
    }

    private PlayerQuestsManager(List<Quest> quests){
        this.Quests = new ArrayList<>(quests);

    }

    public Codec<PlayerQuestsManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Quest.CODEC.listOf().fieldOf("Quests").forGetter(PlayerQuestsManager::getQuests)
    ).apply(instance,PlayerQuestsManager::new));


    public void add(Quest quest) throws DefaultQuestExeption {
        if (Quests.size() > MAX){
            throw new DefaultQuestExeption("Zu viele Quests vorhanden");
        }

        Quests.add(quest);
    }

    public Quest getQuest(String ID){
        for (Quest quest : Quests){
            if (quest.getId().equalsIgnoreCase(ID)){
                return quest;
            }
        }
        return null;
    }

    public Quest getByType(String Type){
        for (Quest quest : Quests){
            if (quest.getTaskType().equalsIgnoreCase(Type)){
                return quest;
            }
        }
        return null;

    }

    private List<Quest> getQuests(){
        return Quests;
    }

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE,this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
