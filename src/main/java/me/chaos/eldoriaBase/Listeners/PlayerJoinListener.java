package me.chaos.eldoriaBase.Listeners;

import com.google.gson.JsonElement;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Utils.DataHolder;
import me.chaos.eldoriaBase.PlayerData.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    Main main;


    public PlayerJoinListener(Main main){
        this.main = main;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer ( );
        loadPlayerData (player);

    }

    public void loadPlayerData(Player player){
        JsonElement json = DataHolder.ReadPlayer(player);
        PlayerData data;


        if (json.getAsJsonObject().isEmpty()){
            data = new PlayerData(player, main);

        } else data = new PlayerData (json);

        main.getHandler ().getPlayerDataHandler ().addData (player,data);
    }
}
