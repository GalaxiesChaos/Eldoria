package me.chaos.eldoriaBase.PlayerData;

import com.mojang.serialization.Codec;

import java.util.*;
import java.util.function.Supplier;

public class PlayerDataRegistry {
    private static final Map<String, Codec<? extends PlayerDataHolder>> REGISTRY = new HashMap<> ( );
    private static final List<Supplier<? extends PlayerDataHolder>> DataList = new ArrayList<>();

    public static void register (String key , Codec<? extends PlayerDataHolder> codec) {
        REGISTRY.put (key , codec);
    }

    public static Codec<? extends PlayerDataHolder> get (String key) {
        return REGISTRY.get (key);
    }


    public static void register(Supplier<? extends PlayerDataHolder> o){
        DataList.add (o);
    }

    public static List<Supplier<? extends PlayerDataHolder>> getDataList() {
        return Collections.unmodifiableList(DataList);
    }
}
