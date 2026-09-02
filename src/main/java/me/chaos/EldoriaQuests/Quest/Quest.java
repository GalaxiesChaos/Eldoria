package me.chaos.EldoriaQuests.Quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quest {
    private String Title;
    private final String id;
    Integer CashReward;
    Integer XpReward;
    Integer difficulty;
    List<String> description;

    private final TaskType type;
    private final Map<String,String> meta;
    private final int goal;
    private int progress;


    public Quest(int goal, Map<String, String> meta, TaskType type, List<String> description, Integer difficulty, Integer XPReward, Integer cashReward, String id, String title) {
        this.goal = goal;
        this.meta = meta;
        this.type = type;
        this.description = description;
        this.difficulty = difficulty;
        this.XpReward = XPReward;
        CashReward = cashReward;
        this.id = id;
        Title = title;
    }

    public int getProgress() {
        return progress;
    }

    public int getGoal() {
        return goal;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public String getTaskType() {
        return type.toString();
    }

    public List<String> getDescription() {
        return description;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public Integer getXpReward() {
        return XpReward;
    }

    public Integer getCashReward() {
        return CashReward;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title").forGetter(Quest::getTitle),
            Codec.STRING.fieldOf("id").forGetter(Quest::getId),
            Codec.INT.fieldOf("CashReward").forGetter(Quest::getCashReward),
            Codec.INT.fieldOf("XpReward").forGetter(Quest::getXpReward),
            Codec.INT.fieldOf("difficulty").forGetter(Quest::getDifficulty),
            Codec.STRING.listOf().fieldOf("description").forGetter(Quest::getDescription),
            Codec.STRING.fieldOf("task").forGetter(Quest::getTaskType),
            Codec.INT.fieldOf("goal").forGetter(Quest::getGoal),
            Codec.INT.fieldOf("progress").forGetter(Quest::getProgress),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("meta").forGetter(Quest::getMeta)
    ).apply(instance,Quest::new));


    private Quest(String title, String id, Integer cashReward, Integer XpReward, Integer difficulty,
                 List<String> description, String task, Integer goal, Integer progress, Map<String, String> meta) {
        this.goal = goal;
        this.meta = meta;
        this.type = getTypeFromString(task);
        this.description = new ArrayList<>(description);
        this.difficulty = difficulty;
        this.XpReward = XpReward;
        CashReward = cashReward;
        this.id = id;
        Title = title;
        this.progress = progress;
    }


    private TaskType getTypeFromString(String input){
        return TaskType.valueOf(input);
    }

    public TaskType getType(){
        return type;
    }

    public void addProgress(int amount){
        this.progress += amount;
    }

}
