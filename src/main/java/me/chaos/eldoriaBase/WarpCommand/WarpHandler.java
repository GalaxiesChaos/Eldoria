package me.chaos.eldoriaBase.WarpCommand;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import org.bukkit.entity.Player;

import java.util.*;

public class WarpHandler {
    private Map<Player, WarpPlayerManager> WarpMap = new HashMap<> ();

    public void registerPlayer(Player player, List<WarpPoint> warps, Main main){

        WarpPlayerManager manager = new WarpPlayerManager (player, main);
        for (WarpPoint warp : warps){
            try {
                manager.addWarp (warp);
            } catch (NotEnoughWarpsExeption e) {
                player.sendMessage (e.getMessage ());
            }
        }


        WarpMap.put (player,manager);

        //Add Public Warps zum Spieler
        loadPublic (player);



    }

    public void registerPlayer(Player player, WarpPlayerManager manager){
        WarpMap.put(player, manager);
        loadPublic(player);
    }


    public WarpPlayerManager getWarpmanager(Player player){
        return WarpMap.get (player);
    }

    public boolean isEmpty(Player player){
        return WarpMap.get(player).isEmpty();
    }

    public boolean containsWarp(String ID, Player player){
        if (!(WarpMap.containsKey(player))){
            return false;
        }

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



