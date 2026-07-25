package me.chaos.eldoriaBase;

import me.chaos.eldoriaBase.Listeners.PlayerJoinListener;
import me.chaos.eldoriaBase.Listeners.PlayerQuitListener;
import me.chaos.eldoriaBase.Utils.PlayerData.PlayerDataRegistry;
import me.chaos.eldoriaBase.WarpCommand.WarpCommand;
import me.chaos.eldoriaBase.WarpCommand.WarpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.mvplugins.multiverse.core.MultiverseCoreApi;

public final class Main extends JavaPlugin {
    private MultiverseCoreApi coreApi;
    public Handlers handler = new Handlers();


    @Override
    public void onEnable ( ) {
        coreApi = MultiverseCoreApi.get();

        registerPlayerData ();
        registerListeners ();
        registerCommand (
                "Warp", new WarpCommand (this)
        );
    }

    public void registerListeners(){
        Bukkit.getPluginManager ().registerEvents (new PlayerQuitListener (this),this);
        Bukkit.getPluginManager ().registerEvents (new PlayerJoinListener (this),this);
    }

    public void registerPlayerData(){
        PlayerDataRegistry.register (WarpPlayerManager.SAVE_KEY, WarpPlayerManager.CODEC);
    }

    @Override
    public void onDisable ( ) {
        // Plugin shutdown logic
    }

    public MultiverseCoreApi getCoreApi(){
        return coreApi;
    }
    public Handlers getHandler(){return handler;}
}
