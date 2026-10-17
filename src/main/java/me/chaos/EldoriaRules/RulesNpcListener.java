package me.chaos.EldoriaRules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

public class RulesNpcListener implements Listener {

    private final Plugin plugin;
    private final RulesBook book;

    public RulesNpcListener(Plugin plugin, RulesBook book) {
        this.plugin = plugin;
        this.book = book;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!RulesNpc.isRulesNpc(plugin, event.getRightClicked())) return;

        // Immer abbrechen (sonst öffnet sich das Händlermenü), aber nur einmal reagieren (Haupthand).
        event.setCancelled(true);
        if (event.getHand() != EquipmentSlot.HAND) return;

        book.show(event.getPlayer());
    }
}
