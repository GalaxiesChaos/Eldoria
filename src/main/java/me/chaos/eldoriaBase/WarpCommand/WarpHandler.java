package me.chaos.eldoriaBase.WarpCommand;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpHandler {
    private Map<Player, WarpPlayerManager> WarpMap = new HashMap<> ();

    public void registerPlayer(Player player, List<WarpPoint> warps, Main main){
        //Füge spieler eigene Warps Hinzu
        WarpPlayerManager manager = new WarpPlayerManager (player, main);
        for (WarpPoint warp : warps){
            try {
                manager.addWarp (warp);
            } catch (NotEnoughWarpsExeption e) {
                player.sendMessage (e.getMessage ());
            }
        }

        //Add Public Warps zum Spieler
        loadPublic (player);


        WarpMap.put (player,manager);
    }

    public WarpPlayerManager getWarpmanager(Player player){
        return WarpMap.get (player);
    }

    public boolean containsWarp(String ID, Player player){
        return WarpMap.get (player).hasWarp (ID);
    }

    public void loadPublic(Player player){
        WarpPlayerManager manager = WarpMap.get (player);
        PublicWarp publicWarp = new PublicWarp ();
        for (WarpPoint warp : publicWarp.getPublicWarps ()){
            manager.addPublicWarp (warp);

    }
}

    public void ReloadPublic(){
        Set<Player> keySet = WarpMap.keySet ();
        List<WarpPoint> PublicWarps = new PublicWarp ().getPublicWarps ();

        for (Player player : keySet){
            WarpPlayerManager manager = WarpMap.get (player);
            List<WarpPoint> newWarps = PublicWarps.stream ().filter (x -> !manager.hasWarp (x.getId ())).toList ();

            newWarps.forEach (manager::addPublicWarp);

            WarpMap.replace (player,manager);
        }
    }

    public List<String> getIds(Player player){
        return WarpMap.get (player).getIds ();
    }




}



