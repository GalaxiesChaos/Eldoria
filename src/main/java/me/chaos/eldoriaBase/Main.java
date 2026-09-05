package me.chaos.eldoriaBase;

import me.chaos.EldoriaCash.Interface.BankCommand;
import me.chaos.EldoriaCash.OnClickEvent;
import me.chaos.EldoriaCash.PlayerBank;
import me.chaos.EldoriaCash.PlayerMoney;
import me.chaos.EldoriaQuests.QuestCommand;
import me.chaos.EldoriaQuests.RegisterHandlers;
import me.chaos.eldoriaBase.Listeners.PlayerJoinListener;
import me.chaos.eldoriaBase.Listeners.PlayerQuitListener;
import me.chaos.eldoriaBase.PlayerData.PlayerDataRegistry;
import me.chaos.eldoriaBase.Stats.PlayerStatData;
import me.chaos.eldoriaBase.Stats.StatEventDispatcher;
import me.chaos.eldoriaBase.Stats.StatItemUtil;
import me.chaos.eldoriaBase.Utils.SignInputListener;
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
        registerCommands();
        registerPlayerDataCodec();
        QuestLoader.loadQuests();

        StatItemUtil.init(this);
        StatLoader.loadStats(this);
        new StatEventDispatcher(this).registerAll();



    }

    public void registerListeners(){
        Bukkit.getPluginManager ().registerEvents (new PlayerQuitListener (this),this);
        Bukkit.getPluginManager ().registerEvents (new PlayerJoinListener (this),this);
        Bukkit.getPluginManager ().registerEvents (new OnClickEvent(this),this);
        Bukkit.getPluginManager ().registerEvents (new SignInputListener(this), this);
    }

    public void registerPlayerDataCodec(){
        PlayerDataRegistry.register (WarpPlayerManager.SAVE_KEY, WarpPlayerManager.CODEC);
        PlayerDataRegistry.register(PlayerMoney.SAVE_KEY, PlayerMoney.CODEC);
        PlayerDataRegistry.register(PlayerBank.SAVE_KEY, PlayerBank.CODEC);
        PlayerDataRegistry.register(PlayerStatData.SAVE_KEY, PlayerStatData.CODEC);

    }

    private void registerPlayerData(){
        PlayerDataRegistry.register(WarpPlayerManager::new);
        PlayerDataRegistry.register(PlayerBank::new);
        PlayerDataRegistry.register(PlayerMoney::new);
        PlayerDataRegistry.register(PlayerStatData::new);

    }

    private void registerCommands(){
        registerCommand ("warp", new WarpCommand (this));
        registerCommand("bank", new BankCommand());
        registerCommand("quests",new QuestCommand());
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
