package me.chaos.eldoriaBase.Utils.Codecs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationCodecHelper extends Location {
    World world;

    public LocationCodecHelper(String World, int x, int y, int z){
        super(Bukkit.getWorld (World),x,y,z);
        world = Bukkit.getWorld (World);
    }

   public String getWorldName(){
        return world.getName ();
   }

    public Location getLocation(){
        return this.toLocation (world);
    }

}
