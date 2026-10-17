package me.chaos.EldoriaItems.Sets;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A set bonus that grants a potion effect (e.g. Nachtsicht) while active.
 * Uses an infinite-duration, non-particle effect; removed again on deactivate.
 */
public class PotionSetBonusEffect implements SetBonusEffect {

    private final PotionEffectType type;
    private final int level; // 1 = Nachtsicht I

    public PotionSetBonusEffect(PotionEffectType type, int level) {
        this.type = type;
        this.level = Math.max(1, level);
    }

    @Override
    public void onActivate(Player player) {
        player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, level - 1, true, false, true));
    }

    @Override
    public void onDeactivate(Player player) {
        player.removePotionEffect(type);
    }
}
