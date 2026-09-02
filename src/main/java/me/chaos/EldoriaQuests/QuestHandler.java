package me.chaos.EldoriaQuests;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestHandler {
    private static final int MAX_REROLLS = 3; // TODO: make configurable

    private final Map<UUID, Integer> usedRerolls = new HashMap<>();

    public boolean hasRerollsLeft(Player player) {
        return usedRerolls.getOrDefault(player.getUniqueId(), 0) < MAX_REROLLS;
    }

    public int getRerollsLeft(Player player) {
        return MAX_REROLLS - usedRerolls.getOrDefault(player.getUniqueId(), 0);
    }

    public void useReroll(Player player) {
        usedRerolls.merge(player.getUniqueId(), 1, Integer::sum);
    }
}
