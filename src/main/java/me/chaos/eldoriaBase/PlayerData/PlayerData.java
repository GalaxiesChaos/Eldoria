package me.chaos.eldoriaBase.PlayerData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.chaos.EldoriaCash.PlayerBank;
import me.chaos.EldoriaCash.PlayerMoney;
import me.chaos.EldoriaQuests.PlayerQuestsManager;
import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.eldoriaBase.Utils.DataHolder;
import me.chaos.eldoriaBase.WarpCommand.WarpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PlayerData implements DataHolder {
    List<PlayerDataHolder> PlayerDataList = new ArrayList<> (  );


    public PlayerData(){
        for (Supplier<? extends PlayerDataHolder> dataHolder : PlayerDataRegistry.getDataList()){
            this.append(dataHolder.get());
        }
    }


    public PlayerData(JsonElement data){
        if (data == null || !data.isJsonObject ()) return;

        JsonObject object = data.getAsJsonObject ();

        for (String key : object.keySet ()) {
            Codec<? extends PlayerDataHolder> codec = PlayerDataRegistry.get (key);

            if (codec == null) {
                Bukkit.getLogger ().warning ("Unbekannter PlayerData-Key beim Laden: " + key);
                continue;
            }

            try {
                PlayerDataHolder holder = codec.parse (JsonOps.INSTANCE, object.get (key)).getOrThrow ();
                PlayerDataList.add (holder);
            } catch (Exception e) {
                Bukkit.getLogger ().warning ("Fehler beim Laden von PlayerData '" + key + "': " + e.getMessage ());
            }
        }

        for (Supplier<? extends PlayerDataHolder> factory : PlayerDataRegistry.getDataList ()) {
            PlayerDataHolder fresh = factory.get ();
            boolean alreadyPresent = false;

            for (PlayerDataHolder existing : PlayerDataList) {
                if (existing.getSaveKey ().equals (fresh.getSaveKey ())) {
                    alreadyPresent = true;
                    break;
                }
            }

            if (!alreadyPresent) {
                append(fresh);
            }
        }



    }

    public void append(PlayerDataHolder dataHolder){
        PlayerDataList.add (dataHolder);
    }

    public void saveDataToFile(Player player){
        WritePlayer (player,combineJson ());
    }



    private JsonElement combineJson(){
        JsonObject data = new JsonObject ();
        for (PlayerDataHolder holder : PlayerDataList){
            data.add (holder.getSaveKey ( ) , holder.getData ());
        }
        return data;
    }

    public PlayerMoney getMoney( ){
        for (PlayerDataHolder dataHolder : PlayerDataList){
            if (dataHolder instanceof PlayerMoney){
                return (PlayerMoney) dataHolder;
            }
        }
        return null;
    }

    public PlayerBank getBank( ){
        for (PlayerDataHolder dataHolder : PlayerDataList){
            if (dataHolder instanceof PlayerBank){
                return (PlayerBank) dataHolder;
            }
        }

        return null;
    }

    public WarpPlayerManager getWarpManager(){
        for (PlayerDataHolder dataHolder : PlayerDataList){
            if (dataHolder instanceof WarpPlayerManager manager){
                return manager;
            }
        }

        return null;
    }

    public PlayerQuestsManager getPlayerQuestManager(){
        for (PlayerDataHolder dataHolder : PlayerDataList){
            if (dataHolder instanceof PlayerQuestsManager manager){
                return manager;
            }
        }

        return null;
    }
}
