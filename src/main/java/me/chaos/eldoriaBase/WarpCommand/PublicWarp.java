package me.chaos.eldoriaBase.WarpCommand;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Utils.DataHolder;
import me.chaos.eldoriaBase.Utils.ReadWriteHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicWarp implements DataHolder {
    private Set<WarpPoint> PublicWarps = new HashSet<> (  );

    public List<WarpPoint> getPublicWarps(){
        return PublicWarps.stream ( ).toList ( );
    }

    public void add(WarpPoint point){
        PublicWarps.add (point);
    }

    public void SaveToFile(){
        WriteConfig (CODEC.encodeStart (JsonOps.INSTANCE , this).getOrThrow ());
    }

    public static void loadFromFile(){
        CODEC.parse (JsonOps.INSTANCE, DataHolder.ReadConfig ());
    }


    public PublicWarp(){
        loadFromFile ();
    }

    private PublicWarp(List<WarpPoint> warps) {
        PublicWarps = new HashSet<> (warps);
    }

    public static Codec<PublicWarp> CODEC = RecordCodecBuilder.create (instance -> instance.group (
            WarpPoint.CODEC.listOf ().fieldOf ("Warps").forGetter (PublicWarp::getPublicWarps)
            ).apply(instance, PublicWarp::new));
}
