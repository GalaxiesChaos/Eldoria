package me.chaos.eldoriaBase;

import me.chaos.eldoriaBase.Utils.PlayerData.PlayerDataHandler;
import me.chaos.eldoriaBase.WarpCommand.WarpHandler;

public class Handlers {
    WarpHandler WarpHandler;
    PlayerDataHandler PlayerHandler;

    public Handlers(){
        WarpHandler = new WarpHandler ();
        PlayerHandler = new PlayerDataHandler ();
    }

    public WarpHandler getWarpHandler(){
        return WarpHandler;
    }

    public PlayerDataHandler getPlayerDataHandler(){
        return PlayerHandler;
    }

}
