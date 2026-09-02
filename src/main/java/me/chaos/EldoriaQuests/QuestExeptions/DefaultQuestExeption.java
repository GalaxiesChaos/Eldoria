package me.chaos.EldoriaQuests.QuestExeptions;

import org.bukkit.entity.Player;

public class DefaultQuestExeption extends Exception {
    public DefaultQuestExeption(String msg){
        super("[QuestERROR]" + msg);
    }

    public DefaultQuestExeption(){
        super("[QuestERROR]");
    }

    public DefaultQuestExeption(String msg, Player player){
        super("[QuestERROR]" + msg);
        player.sendMessage("[QuestERROR]" + msg);
    }


}
