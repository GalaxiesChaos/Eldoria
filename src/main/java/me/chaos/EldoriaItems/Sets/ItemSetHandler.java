package me.chaos.EldoriaItems.Sets;

import me.chaos.EldoriaItems.Item.CustomItemService;
import me.chaos.EldoriaItems.Item.CustomItemTemplate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks, per player, which set bonuses are currently active based on equipped armor + held
 * items, and fires {@link SetBonusEffect#onActivate}/{@link SetBonusEffect#onDeactivate}
 * whenever that changes.
 * <p>
 * Equipment is re-checked on a short repeating timer (every second) rather than hooked into
 * every possible inventory/armor event - there's no single Bukkit event that reliably covers
 * every way a held/equipped item can change (armor swap, hotbar scroll, offhand swap, etc.),
 * and a 1-second delay is imperceptible for passive set bonuses. Join/quit are still handled
 * instantly for cleanliness.
 */
public class ItemSetHandler implements Listener {

    // player -> (setId -> currently active SetBonus)
    private final Map<UUID, Map<String, SetBonus>> activeBonuses = new HashMap<>();

    public void startPeriodicCheck(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                recalculate(player);
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        recalculate(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        activeBonuses.remove(event.getPlayer().getUniqueId());
    }

    public void recalculate(Player player) {
        EntityEquipment equipment = player.getEquipment();
        if (equipment == null) return;

        List<ItemStack> equipped = List.of(
                equipment.getHelmet(), equipment.getChestplate(),
                equipment.getLeggings(), equipment.getBoots(),
                equipment.getItemInMainHand(), equipment.getItemInOffHand()
        );

        Map<String, Integer> pieceCountBySet = new HashMap<>();
        for (ItemStack stack : equipped) {
            if (stack == null) continue;
            CustomItemTemplate template = CustomItemService.getTemplate(stack);
            if (template == null || template.getSetId() == null) continue;
            pieceCountBySet.merge(template.getSetId(), 1, Integer::sum);
        }

        Map<String, SetBonus> newActive = new HashMap<>();
        for (Map.Entry<String, Integer> entry : pieceCountBySet.entrySet()) {
            ItemSet set = ItemSetRegistry.get(entry.getKey());
            if (set == null) continue;
            SetBonus bonus = set.getHighestActiveBonus(entry.getValue());
            if (bonus != null) {
                newActive.put(entry.getKey(), bonus);
            }
        }

        Map<String, SetBonus> previous = activeBonuses.getOrDefault(player.getUniqueId(), Map.of());

        // Deactivate bonuses that are no longer active, or that changed tier
        for (Map.Entry<String, SetBonus> old : previous.entrySet()) {
            SetBonus stillActive = newActive.get(old.getKey());
            if (!old.getValue().equals(stillActive)) {
                old.getValue().effects().forEach(effect -> effect.onDeactivate(player));
            }
        }

        // Activate bonuses that are new, or that changed tier
        for (Map.Entry<String, SetBonus> now : newActive.entrySet()) {
            SetBonus wasActive = previous.get(now.getKey());
            if (!now.getValue().equals(wasActive)) {
                now.getValue().effects().forEach(effect -> effect.onActivate(player));
            }
        }

        activeBonuses.put(player.getUniqueId(), newActive);
    }

    public Map<String, SetBonus> getActiveBonuses(Player player) {
        return activeBonuses.getOrDefault(player.getUniqueId(), Map.of());
    }

    /** Forwards a generic Bukkit event to every currently active set bonus effect for this player. */
    public void dispatch(Event event, Player player) {
        for (SetBonus bonus : getActiveBonuses(player).values()) {
            for (SetBonusEffect effect : bonus.effects()) {
                effect.onGenericEvent(event, player);
            }
        }
    }
}
