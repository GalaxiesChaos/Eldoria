package me.chaos.eldoriaBase.PlayerData;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerDataRegistry {
    private static final Map<String, Codec<? extends PlayerDataHolder>> REGISTRY = new HashMap<> ( );
    private static final List<PlayerDataHolder> DataList = new ArrayList<>();

    public static void register (String key , Codec<? extends PlayerDataHolder> codec) {
        REGISTRY.put (key , codec);
    }

    public static Codec<? extends PlayerDataHolder> get (String key) {
        return REGISTRY.get (key);
    }


    public static void register(PlayerDataHolder o){
        DataList.add(o);
    }

    public static List<PlayerDataHolder> getDataList() {
        return DataList;
    }
}
