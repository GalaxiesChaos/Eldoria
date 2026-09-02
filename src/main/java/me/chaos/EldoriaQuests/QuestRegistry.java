package me.chaos.EldoriaQuests;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;

import java.util.*;

public class QuestRegistry {
    private static final List<Quest> ALL_QUESTS = new ArrayList<>();

    public static void register(Quest quest) {
        ALL_QUESTS.add(quest);
    }

    public static List<Quest> getAll() {
        return Collections.unmodifiableList(ALL_QUESTS);
    }

    /**
     * Picks a random quest whose ID is not present in excludeIds.
     * Throws DefaultQuestExeption if no eligible quest is left.
     */
    public static Quest getRandomQuest(Collection<String> excludeIds) throws DefaultQuestExeption {
        List<Quest> pool = ALL_QUESTS.stream()
                .filter(q -> !excludeIds.contains(q.getId()))
                .toList();

        if (pool.isEmpty()) {
            throw new DefaultQuestExeption("Nicht genug Quests vorhanden.");
        }

        return pool.get(new Random().nextInt(pool.size()));
    }

}
