package me.chaos.eldoriaBase.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataHandler {
    Map<UUID, PlayerData> PlayerMap = new HashMap<>();

    public PlayerData getPlayerData(Player player) {
        return PlayerMap.get(player.getUniqueId());
    }

    public void addData(Player player, PlayerData data) {
        PlayerMap.put(player.getUniqueId(), data);
    }

    public void saveData(Player player) {
        // FIXED: previously logged "nicht gefunden" and then NPE'd on the null anyway
        PlayerData data = getPlayerData(player);
        if (data == null) {
            Bukkit.getLogger().warning("[EldoriaMC]: PlayerData nicht gefunden für " + player.getUniqueId());
            return;
        }
        data.saveDataToFile(player);
    }
}
