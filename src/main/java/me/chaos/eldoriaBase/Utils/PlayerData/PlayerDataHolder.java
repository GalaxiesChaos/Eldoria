package me.chaos.eldoriaBase.Utils.PlayerData;

import com.google.gson.JsonElement;
import me.chaos.eldoriaBase.Main;
import org.bukkit.entity.Player;

public interface PlayerDataHolder {

    JsonElement getData();

    String getSaveKey();

    default void append (Main main, Player player){
        main.getHandler ().getPlayerDataHandler ().getPlayerData (player).append (this);
    }

}

