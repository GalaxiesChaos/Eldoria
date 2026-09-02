package me.chaos.eldoriaBase;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.Quest.TaskType;
import me.chaos.EldoriaQuests.QuestRegistry;

import java.util.List;
import java.util.Map;

public class QuestLoader {
    public static void loadQuests(){
        QuestRegistry.register(new Quest(
                10,
                Map.of(),
                TaskType.KILL_ENTITY,
                List.of("Töte 10 zombies", "Bekomme 50 Coins"),
                0, 0, 50,
                "quest_kill_zombies_10",
                "Zombie jagd"
        ));

        QuestRegistry.register(new Quest(
                10,
                Map.of(),
                TaskType.COLLECT,
                List.of("Sammle 10 Eisenbarren, bekomme 30 Coins"),
                0,0,30,
                "quest_collect_iron_10",
                "Eisensammler"
        ));

        QuestRegistry.register(new Quest(
                5,
                Map.of(),
                TaskType.DELIVER,
                List.of("Liefere dem Händler 3 Diamanten"),
                0,0,75,
                "quest_deliver_diamonds_5",
                "Diamanten sind gefragt"
        ));

    }
}
