package me.chaos.eldoriaBase.Utils.PlayerData;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataHandler {
    Map<Player, PlayerData> PlayerMap = new HashMap<>();

    public PlayerData getPlayerData(Player player){
        return PlayerMap.get (player);
    }

    public void addData(Player player , PlayerData data){
        PlayerMap.put (player,data);
    }

    public void saveData(Player player){
        getPlayerData (player).saveDataToFile (player);
    }
}
