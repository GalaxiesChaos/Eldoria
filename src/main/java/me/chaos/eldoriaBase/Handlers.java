package me.chaos.eldoriaBase;

import me.chaos.EldoriaQuests.RegisterHandlers;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHandler;
import me.chaos.eldoriaBase.WarpCommand.WarpHandler;

public class Handlers {
    WarpHandler WarpHandler;
    PlayerDataHandler PlayerHandler;
    RegisterHandlers registerHandlers;



    public Handlers(){
        WarpHandler = new WarpHandler ();
        PlayerHandler = new PlayerDataHandler ();
        registerHandlers  = new RegisterHandlers();
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
}
