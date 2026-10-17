package me.chaos.EldoriaItems.Item.Stats;

import me.chaos.eldoriaBase.Stats.StatItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bridge between the *display* stats of the EldoriaItems system (StatType, lore lines) and the
 * *functional* stat framework in eldoriaBase (Stat + StatItemUtil PDC keys + StatManager).
 * <p>
 * EldoriaItems stat keys that have a functional counterpart are written onto the item stack
 * whenever CustomItemService renders it, so a pickaxe with "+50 Abbaugeschwindigkeit" actually
 * mines faster and armor with "+10 Bergbaugl\u00fcck" actually multiplies drops. Display-only
 * stats (damage, strength, ...) are ignored here until a functional stat exists for them.
 */
public final class StatKeyBridge {

    // EldoriaItems StatType key -> eldoriaBase functional stat id (Stat.getId())
    private static final Map<String, String> FUNCTIONAL_IDS = Map.of(
            "mining_speed", "mining_speed",
            "dropchance", "mining_dropchance",
            "mining_fortune", "mining_fortune",
            "mining_spread", "mining_spread"
    );

    private StatKeyBridge() {
    }

    /** The functional stat id for a display stat key, or null when the stat is display-only. */
    public static String functionalStatId(String statTypeKey) {
        return statTypeKey == null ? null : FUNCTIONAL_IDS.get(statTypeKey.toLowerCase());
    }

    /**
     * Writes every functionally-supported stat of one item stack into its PDC
     * (template base stats + upgrade bonuses), replacing previous values.
     */
    public static void applyFunctionalStats(ItemStack stack, Map<StatType, Double> effectiveStats) {
        Map<String, Double> consolidated = new LinkedHashMap<>();
        for (Map.Entry<StatType, Double> entry : effectiveStats.entrySet()) {
            String functionalId = functionalStatId(entry.getKey().getKey());
            if (functionalId != null) {
                consolidated.merge(functionalId, entry.getValue(), Double::sum);
            }
        }
        for (Map.Entry<String, Double> entry : consolidated.entrySet()) {
            StatItemUtil.setStat(stack, entry.getKey(), entry.getValue());
        }
    }
}
