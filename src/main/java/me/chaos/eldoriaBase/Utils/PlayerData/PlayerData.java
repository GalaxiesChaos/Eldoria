package me.chaos.eldoriaBase.Utils.PlayerData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.chaos.eldoriaBase.Utils.DataHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerData implements DataHolder {
    List<PlayerDataHolder> PlayerDataList = new ArrayList<> (  );

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

}
