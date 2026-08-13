package me.chaos.eldoriaBase.PlayerData;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataHandler {
    Map<UUID, PlayerData> PlayerMap = new HashMap<>();

    public PlayerData getPlayerData(Player player){
        return PlayerMap.get (player.getUniqueId());
    }

    public void addData(Player player , PlayerData data){
        PlayerMap.put (player.getUniqueId(),data);
    }

    public void saveData(Player player){
        if (getPlayerData(player) == null) {
            System.out.println("[EldoriaMC]: SpielerData nicht gefunden");
        }
        getPlayerData (player).saveDataToFile (player);
    }
}
