package me.chaos.EldoriaQuests;

import java.util.EnumMap;
import java.util.Map;

public class QuestTypeHandler {
    private Map<TaskType, TaskHandler<?>> handler = new EnumMap<>(TaskType.class);

}
