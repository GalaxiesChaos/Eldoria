package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.CustomEnchant;
import me.chaos.EldoriaEnchants.EnchantData;
import me.chaos.EldoriaEnchants.EnchantVariables;
import me.chaos.EldoriaEnchants.EventEnchant;
import me.chaos.EldoriaEnchants.ItemEnchantHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Central dispatcher for event-based (triggered) enchants.
 * <p>
 * To wire up a new kind of trigger (e.g. block-break for a future mining enchant):
 * add one @EventHandler method for that Bukkit event below, and call
 * {@link #dispatch} with every item that could plausibly carry a relevant enchant
 * (mainhand, armor, whatever makes sense for that event). No changes are needed
 * anywhere else - any EventEnchant declaring that event class will automatically
 * be picked up.
 */
public class EnchantEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker) {
            dispatch(event, attacker, attacker.getInventory().getItemInMainHand());
        }
        if (event.getEntity() instanceof Player victim) {
            for (ItemStack armorPiece : victim.getInventory().getArmorContents()) {
                dispatch(event, victim, armorPiece);
            }
        }
    }

    private void dispatch(Event event, Player owner, ItemStack item) {
        if (item == null || item.getType().isAir()) return;

        for (Map.Entry<CustomEnchant, EnchantData> entry : ItemEnchantHandler.getEnchants(item).entrySet()) {
            if (entry.getKey() instanceof EventEnchant<?> eventEnchant && eventEnchant.getEventClass().isInstance(event)) {
                invoke(eventEnchant, event, owner, item, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> void invoke(EventEnchant<E> enchant, Event event, Player owner, ItemStack item, EnchantData data) {
        EnchantVariables variables = data.variables();
        enchant.onTrigger((E) event, owner, item, data.level(), variables);

        if (variables.isDirty()) {
            ItemEnchantHandler.setEnchant(item, enchant, data.level(), variables);
        }
    }
}
