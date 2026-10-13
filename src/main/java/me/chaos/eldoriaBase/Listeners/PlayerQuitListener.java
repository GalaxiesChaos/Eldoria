package me.chaos.eldoriaBase.Listeners;

import me.chaos.eldoriaBase.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    Main main;

    public PlayerQuitListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        savePlayerData(player);
        // FIXED: WarpHandler no longer leaks Player objects / stale managers
        main.getHandler().getWarpHandler().unregister(player);
    }

    public void savePlayerData(Player player) {
        try {
            main.getHandler().getPlayerDataHandler().saveData(player);
        } catch (Exception e) {
            main.getLogger().severe("Fehler beim Speichern von " + player.getName() + ": " + e.getMessage());
        }
    }
}
