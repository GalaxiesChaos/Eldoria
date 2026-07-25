package me.chaos.eldoriaBase.Utils.PlayerData;

import com.mojang.serialization.Codec;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataRegistry {
    private static final Map<String, Codec<? extends PlayerDataHolder>> REGISTRY = new HashMap<> ( );

    public static void register (String key , Codec<? extends PlayerDataHolder> codec) {
        REGISTRY.put (key , codec);
    }

    public static Codec<? extends PlayerDataHolder> get (String key) {
        return REGISTRY.get (key);
    }
}
