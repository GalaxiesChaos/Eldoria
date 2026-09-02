package me.chaos.eldoriaBase;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.Quest.TaskType;
import me.chaos.EldoriaQuests.QuestRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public class QuestLoader {
    public static void loadQuests(){
        QuestRegistry.register(new Quest(
                10,
                Map.of("entity", EntityType.ZOMBIE.toString()),
                TaskType.KILL_ENTITY,
                List.of("Töte 10 zombies", "Bekomme 50 Coins"),
                0, 0, 50,
                "quest_kill_zombies_10",
                "Zombie jagd"
        ));

        QuestRegistry.register(new Quest(
                10,
                Map.of("item", Material.IRON_INGOT.toString()),
                TaskType.COLLECT,
                List.of("Sammle 10 Eisenbarren, bekomme 30 Coins"),
                0,0,30,
                "quest_collect_iron_10",
                "Eisensammler"
        ));

        QuestRegistry.register(new Quest(
                5,
                Map.of("item",Material.DIAMOND.toString()),
                TaskType.COLLECT,
                List.of("Liefere dem Händler 3 Diamanten(WIP)"),
                0,0,75,
                "quest_deliver_diamonds_5",
                "Diamanten sind gefragt"
        ));

    }
}
