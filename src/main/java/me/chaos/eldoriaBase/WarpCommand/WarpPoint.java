package me.chaos.eldoriaBase.WarpCommand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Utils.Codecs.CodecHolder;
import me.chaos.eldoriaBase.Utils.Codecs.LocationCodecHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WarpPoint {
    private final String Id;
    private final Location WarpLocation;
    private final World world;

    public World getWorld ( ) {
        return world;
    }

    public Location getWarpLocation ( ) {
        return WarpLocation;
    }

    public LocationCodecHelper getWarpLocationCODEC ( ) {
        return new LocationCodecHelper(WarpLocation);
    }

    public String getId ( ) {
        return Id;
    }

    public WarpPoint(Player player, String ID){
        this.world = player.getWorld ( );
        this.WarpLocation = player.getLocation ();
        this.Id = ID;
    }

    private WarpPoint(LocationCodecHelper loc, String ID){
        this.world = loc.getWorld ();
        this.Id = ID;
        this.WarpLocation = loc.toLocation ();
    }

    public static Codec<WarpPoint> CODEC = RecordCodecBuilder.create (instance -> instance.group (
            CodecHolder.getLocationCodec ().fieldOf ("Location").forGetter (WarpPoint::getWarpLocationCODEC),
            Codec.STRING.fieldOf ("ID").forGetter (WarpPoint::getId)
    ).apply (instance, WarpPoint::new));


}
