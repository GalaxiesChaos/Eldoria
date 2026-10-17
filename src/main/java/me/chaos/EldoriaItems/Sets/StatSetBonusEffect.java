package me.chaos.EldoriaItems.Sets;

import me.chaos.EldoriaItems.Item.Stats.StatType;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * A set bonus that grants flat stat bonuses while active.
 * <p>
 * NOTE: actually applying these stats to gameplay (e.g. into a real attribute/stat service)
 * isn't implemented here since that system doesn't exist yet in the codebase -
 * {@link #onActivate} / {@link #onDeactivate} are exactly the two places to hook that in later.
 */
public class StatSetBonusEffect implements SetBonusEffect {
    private final Map<StatType, Double> bonuses;

    public StatSetBonusEffect(Map<StatType, Double> bonuses) {
        this.bonuses = bonuses;
    }

    public Map<StatType, Double> getBonuses() {
        return bonuses;
    }

    @Override
    public void onActivate(Player player) {
        // TODO: hook into Eldoria's stat/attribute system once it exists, e.g.:
        // bonuses.forEach((stat, value) -> statService.addModifier(player, stat, value));
    }

    @Override
    public void onDeactivate(Player player) {
        // TODO: remove the modifiers added in onActivate for this player.
    }
}
