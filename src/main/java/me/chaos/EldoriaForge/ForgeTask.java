package me.chaos.EldoriaForge;

import me.chaos.EldoriaForge.menu.ForgeMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/** Re-renders every open forge menu once per second (pane colors, remaining time). */
public final class ForgeTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof ForgeMenu menu) {
                menu.render();
            }
        }
    }
}
