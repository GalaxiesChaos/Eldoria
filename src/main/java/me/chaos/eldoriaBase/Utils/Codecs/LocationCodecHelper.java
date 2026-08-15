package me.chaos.eldoriaBase.Utils.Codecs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationCodecHelper extends Location {

    public LocationCodecHelper(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public LocationCodecHelper(Location loc){
        super(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());
    }

    public String getWorldName() {
        return getWorld().getName();

    }

    public LocationCodecHelper(String world, double x, double y, double z) {
        super(Bukkit.getWorld(world), x + 0.5, y + 0.1, z + 0.5);
    }

    public Location toLocation() {
      return new Location(getWorld(),getX(),getY(),getZ());
    }


}
