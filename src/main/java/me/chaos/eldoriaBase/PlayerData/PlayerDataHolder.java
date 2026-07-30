package me.chaos.eldoriaBase.PlayerData;

import com.google.gson.JsonElement;
import me.chaos.eldoriaBase.Main;
import org.bukkit.entity.Player;

import java.util.List;

public interface PlayerDataHolder {

    JsonElement getData();

    String getSaveKey();

    default void append (Main main, Player player){
        main.getHandler ().getPlayerDataHandler ().getPlayerData (player).append (this);
    }

}