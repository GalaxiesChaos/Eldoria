package me.chaos.eldoriaBase.WarpCommand;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.WarpNotFoundExeption;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;

public class WarpPlayerManager implements PlayerDataHolder {
    public static final String SAVE_KEY = "warps";
    private int current_size = 0;
    private List<WarpPoint> WarpList = new ArrayList<> (  );

    public WarpPlayerManager() {

    }

    public WarpPoint getWarp(String ID) throws NullPointerException {
       for (WarpPoint point : WarpList){
           if (point.getId ().equalsIgnoreCase (ID)){
               return point;
           }
       }
       return null;
   }

   public void addWarp(WarpPoint add) throws NotEnoughWarpsExeption {
       int size = 3;
       if (current_size++ < size){
           WarpList.add (add);
       } else {
           throw new NotEnoughWarpsExeption ("Nicht genug Warps vorhanden");
       }
   }

   public void removeWarp(String ID) throws WarpNotFoundExeption {
       boolean removed = WarpList.removeIf (point -> point.getId ().equalsIgnoreCase (ID));

       if (removed) {
           current_size--;
       } else {
           throw new WarpNotFoundExeption ("Warp wurde nicht gefunden");
       }

   }

   public void addPublicWarp(WarpPoint point){
       WarpList.add (point);
   }

   public boolean hasWarp(String ID){
        return WarpList.stream ().anyMatch (x -> x.getId ().equalsIgnoreCase (ID));
   }

   public List<String> getIds(){
        return WarpList.stream ().map (WarpPoint::getId).toList ();
   }

   private List<WarpPoint> getWarps(){
       return WarpList;
   }

   public WarpPlayerManager(Player player, Main main){
       append (main,player);
   }

   private WarpPlayerManager (List<WarpPoint> warps){
       WarpList = warps;
   }


   public static final Codec< WarpPlayerManager> CODEC  = RecordCodecBuilder.create (instance -> instance.group (
           WarpPoint.CODEC.listOf ( ).fieldOf ("Warps").forGetter (WarpPlayerManager::getWarps)
   ).apply (instance, WarpPlayerManager::new));


    @Override
    public JsonElement getData ( ) {
        return CODEC.encodeStart (JsonOps.INSTANCE,this).getOrThrow ();
    }

    @Override
    public String getSaveKey ( ) {
        return SAVE_KEY;
    }


    public Codec<WarpPlayerManager> getCodec ( ) {
        return CODEC;
    }
}
