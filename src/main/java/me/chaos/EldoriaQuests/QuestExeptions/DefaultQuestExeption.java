package me.chaos.EldoriaQuests.QuestExeptions;

public class DefaultQuestExeption extends Exception {
    public DefaultQuestExeption(String msg){
        super("[QuestERROR]" + msg);
    }

    public DefaultQuestExeption(){
        super("[QuestERROR]");
    }


}
