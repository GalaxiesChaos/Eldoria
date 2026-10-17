package me.chaos.EldoriaRules;

import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Fallback-NPC fürs Regelwerk: ein unbewegliches, unverwundbares Dorfbewohner-Entity mit PDC-Tag.
 * Jedes Entity mit diesem Tag öffnet beim Rechtsklick das Regelwerk - ein NPC aus eurer eigenen
 * NPC-Logik kann also einfach denselben Tag setzen (siehe {@link #tag(Plugin, Entity)}).
 */
public final class RulesNpc {

    private RulesNpc() {}

    private static NamespacedKey key(Plugin plugin) {
        return new NamespacedKey(plugin, "rules_npc");
    }

    public static void tag(Plugin plugin, Entity entity) {
        entity.getPersistentDataContainer().set(key(plugin), PersistentDataType.BYTE, (byte) 1);
    }

    public static boolean isRulesNpc(Plugin plugin, Entity entity) {
        return entity.getPersistentDataContainer().has(key(plugin), PersistentDataType.BYTE);
    }

    public static Villager spawn(Main plugin, Location location) {
        return location.getWorld().spawn(location, Villager.class, villager -> {
            villager.setAI(false);
            villager.setInvulnerable(true);
            villager.setSilent(true);
            villager.setPersistent(true);
            villager.setRemoveWhenFarAway(false);
            villager.setCollidable(false);
            villager.customName(MiniMessage.miniMessage()
                    .deserialize(plugin.getConfig().getString("rules.npc-name", "<gold>Regelwerk")));
            villager.setCustomNameVisible(true);
            tag(plugin, villager);
        });
    }

    /** Entfernt den nächsten Regelwerk-NPC im Umkreis. @return true, wenn einer entfernt wurde. */
    public static boolean removeNearest(Plugin plugin, Location from, double radius) {
        Entity nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity entity : from.getWorld().getNearbyEntities(from, radius, radius, radius)) {
            if (!isRulesNpc(plugin, entity)) continue;
            double distance = entity.getLocation().distanceSquared(from);
            if (distance < best) {
                best = distance;
                nearest = entity;
            }
        }
        if (nearest == null) return false;
        nearest.remove();
        return true;
    }
}
