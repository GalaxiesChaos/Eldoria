package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.EnchantTarget;
import me.chaos.EldoriaEnchants.EnchantVariables;
import me.chaos.EldoriaEnchants.EventEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Weapon-side counterpart to Retaliation: chance per level to poison whatever you
 * hit. Also demonstrates {@link EnchantVariables} by storing a short per-item
 * cooldown so it can't proc twice in the same tick from multi-hit weapons.
 */
public class VenomousStrikeEnchant extends EventEnchant<EntityDamageByEntityEvent> {

    private static final String VAR_COOLDOWN_UNTIL = "cooldown_until";

    public VenomousStrikeEnchant() {
        super("venomous_strike", Component.text("Giftiger Hieb").color(NamedTextColor.DARK_PURPLE),
                3, 1, EnchantTarget.SWORD, EntityDamageByEntityEvent.class);
    }

    @Override
    public void onTrigger(EntityDamageByEntityEvent event, Player owner, ItemStack item, int level, EnchantVariables variables) {
        if (!event.getDamager().equals(owner)) return; // only the wielder's own hits count
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        long now = owner.getWorld().getFullTime();
        if (now < variables.getLong(VAR_COOLDOWN_UNTIL, 0L)) return;

        double chance = 0.10 * level; // 10% per level
        if (ThreadLocalRandom.current().nextDouble() > chance) return;

        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
        variables.setLong(VAR_COOLDOWN_UNTIL, now + 20L); // 1s internal cooldown, stored on the item itself
    }
}
