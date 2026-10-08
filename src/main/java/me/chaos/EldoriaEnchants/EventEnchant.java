package me.chaos.EldoriaEnchants;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

/**
 * An enchant whose effect fires off a specific Bukkit event instead of granting a
 * passive stat - e.g. "10% chance per level to poison whatever you hit".
 * <p>
 * {@link me.chaos.EldoriaEnchants.Listeners.EnchantEventListener} is responsible
 * for finding items that hold an EventEnchant matching the fired event and calling
 * {@link #onTrigger}. Which player/item ends up "owning" the trigger (attacker vs.
 * defender) depends entirely on where the listener found the item equipped - the
 * enchant itself decides what to do with that using the event's own getters
 * (getDamager()/getEntity() etc.).
 */
public abstract class EventEnchant<E extends Event> extends CustomEnchant {

    private final Class<E> eventClass;

    protected EventEnchant(String id, Component displayName, int maxLevel, int maxTableLevel,
                            EnchantTarget target, Class<E> eventClass) {
        super(id, displayName, maxLevel, maxTableLevel, target);
        this.eventClass = eventClass;
    }

    public Class<E> getEventClass() {
        return eventClass;
    }

    /**
     * Called whenever a matching event fires while this enchant is present on an
     * item {@code owner} is holding/wearing.
     *
     * @param owner     the player equipping the item this enchant instance lives on
     * @param item      the actual ItemStack, in case you need to read/re-write more than the enchant data
     * @param level     the enchant's current level on this item
     * @param variables this enchant's own persistent scratch space on this specific item.
     *                  Mutate it freely (e.g. to store a cooldown) - anything written here
     *                  is saved back to the item's PDC automatically after this call returns.
     */
    public abstract void onTrigger(E event, Player owner, ItemStack item, int level, EnchantVariables variables);
}
