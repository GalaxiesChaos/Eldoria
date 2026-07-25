package me.chaos.eldoriaBase.Utils.Timers;

import me.chaos.eldoriaBase.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

public class Timers<T extends TimedEvent> implements Listener {
    T t ;
    
    
    BukkitScheduler scheduler;
    public Timers (Main main){
        scheduler = Bukkit.getServer ().getScheduler ();
        scheduler.runTaskLater (main, t ,Long.valueOf (20));
    }
    
    
}
