package me.chaos.EldoriaNPC;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

/**
 * Right-click interaction with NPCs (dialogue / GUI / teleport) plus protection from damage and mob targeting.
 */
public final class NPCListener implements Listener {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final long LINE_DELAY_TICKS = 25;

    private final Plugin plugin;
    private final NPCManager manager;

    public NPCListener(Plugin plugin, NPCManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();

        NPCDefinition definition = manager.getDefinitionOf(entity).orElse(null);
        if (definition == null) return;

        event.setCancelled(true);

        switch (definition.interaction()) {
            case GUI -> {
                if (!GuiOpenerRegistry.open(player, definition.guiId())) {
                    player.sendMessage(Component.text("Dieser NPC hat kein gültiges Menü (gui: " + definition.guiId() + ").")
                            .color(NamedTextColor.RED));
                }
            }
            case DIALOGUE -> sendDialogue(player, definition);
            case TELEPORT -> NPCTeleporter.teleport(player, definition);
        }
    }

    private void sendDialogue(Player player, NPCDefinition definition) {
        if (definition.lines().isEmpty()) {
            player.sendMessage(Component.text("...").color(NamedTextColor.GRAY));
            return;
        }
        Component name = LEGACY.deserialize(definition.displayName());
        for (int i = 0; i < definition.lines().size(); i++) {
            long delay = LINE_DELAY_TICKS * i;
            String line = definition.lines().get(i).replace("%player%", player.getName());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) return;
                player.sendMessage(Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(name)
                        .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                        .append(LEGACY.deserialize(line)));
            }, delay);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (manager.getDefinitionOf(event.getEntity()).isPresent()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() != null && manager.getDefinitionOf(event.getTarget()).isPresent()) {
            event.setCancelled(true);
        }
    }
}
