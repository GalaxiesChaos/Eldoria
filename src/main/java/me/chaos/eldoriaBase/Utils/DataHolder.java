package me.chaos.eldoriaBase.Utils;

import com.google.gson.JsonElement;
import org.bukkit.entity.Player;

public interface DataHolder {
    String CONFIG_PATH = "./config/Eldoria";
    String PLAYER_PATH = "./config/Eldoria/Players";


    default void WriteConfig (JsonElement data){
        ReadWriteHandler.Write (data,
                CONFIG_PATH + "WarpConfig"
        );
    }
    default void WritePlayer(Player player, JsonElement data){
        ReadWriteHandler.Write (data,
                PLAYER_PATH + player.getUniqueId ());

    }

    static JsonElement ReadConfig ( ){
        return ReadWriteHandler.Read (CONFIG_PATH + "WarpConfig");
    }

    static JsonElement ReadPlayer(Player player){
        return ReadWriteHandler.Read (PLAYER_PATH + player.getUniqueId ());
    }
}
