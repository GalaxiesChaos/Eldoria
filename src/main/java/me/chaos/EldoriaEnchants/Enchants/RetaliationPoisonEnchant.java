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

/**
 * "Inflict poison on an enemy when they hit you" - the example from the spec.
 * Lives on armor; EnchantEventListener calls this with {@code owner} = the wearer
 * (the one getting hit), so we just poison whoever the damager was.
 */
public class RetaliationPoisonEnchant extends EventEnchant<EntityDamageByEntityEvent> {

    public RetaliationPoisonEnchant() {
        super("retaliation", Component.text("Vergeltung").color(NamedTextColor.DARK_GREEN),
                3, 1, EnchantTarget.ARMOR, EntityDamageByEntityEvent.class);
    }

    @Override
    public void onTrigger(EntityDamageByEntityEvent event, Player owner, ItemStack item, int level, EnchantVariables variables) {
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;
        if (attacker.equals(owner)) return; // don't poison yourself off e.g. thorns-on-thorns weirdness

        int durationTicks = 40 + (level * 20); // 2s base, +1s per level
        int amplifier = level >= getMaxLevel() ? 1 : 0; // Poison II only at the enchant's absolute max level

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.POISON, durationTicks, amplifier));
    }
}
