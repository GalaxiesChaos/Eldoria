package me.chaos.eldoriaBase.Listeners;

import me.chaos.eldoriaBase.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    Main main;
    public PlayerQuitListener(Main main){
        this.main = main;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer ( );

        savePlayerData (player);
    }

    public void savePlayerData(Player player){
        main.getHandler ().getPlayerDataHandler ().saveData (player);

    }

}
