package me.chaos.eldoriaBase.Listeners;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Utils.DataHolder;
import me.chaos.eldoriaBase.Utils.PlayerData.PlayerData;
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
        PlayerData data = new PlayerData (DataHolder.ReadPlayer (player));
        main.getHandler ().getPlayerDataHandler ().addData (player,data);
    }
}
