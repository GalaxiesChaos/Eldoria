package me.chaos.eldoriaBase.Utils.Codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.bukkit.Location;
import org.bukkit.World;

public class CodecHolder {
    private static Codec<LocationCodecHelper> LOCCODEC = RecordCodecBuilder.create (instance -> {
                return instance.group (
                        Codec.STRING.fieldOf ("World").forGetter (LocationCodecHelper::getWorldName),
                        Codec.INT.fieldOf ("X").forGetter (Location::getBlockX) ,
                        Codec.INT.fieldOf ("Y").forGetter (Location::getBlockY) ,
                        Codec.INT.fieldOf ("Z").forGetter (Location::getBlockZ)

                ).apply (instance , LocationCodecHelper::new);
            }
    );

    public static Codec<LocationCodecHelper> getLocationCodec(){
        return LOCCODEC;
    }





}
