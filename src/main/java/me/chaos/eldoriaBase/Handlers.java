package me.chaos.eldoriaBase;

import me.chaos.EldoriaQuests.RegisterHandlers;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHandler;
import me.chaos.eldoriaBase.Stats.StatManager;
import me.chaos.eldoriaBase.WarpCommand.WarpHandler;

public class Handlers {
    WarpHandler WarpHandler;
    PlayerDataHandler PlayerHandler;
    RegisterHandlers registerHandlers;
    StatManager statManager;


    public Handlers(){
        WarpHandler = new WarpHandler ();
        PlayerHandler = new PlayerDataHandler ();
        registerHandlers  = new RegisterHandlers();
        statManager = new StatManager(this);
    }

    public WarpHandler getWarpHandler(){
        return WarpHandler;
    }

    public PlayerDataHandler getPlayerDataHandler(){
        return PlayerHandler;
    }

    public RegisterHandlers getRegisterHandlers() {
        return registerHandlers;
    }

    public StatManager getStatManager(){
        return statManager;
    }
}
