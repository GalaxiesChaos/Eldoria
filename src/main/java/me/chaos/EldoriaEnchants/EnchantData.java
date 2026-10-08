package me.chaos.EldoriaEnchants;

/**
 * A snapshot of one enchant's state on a specific item: which enchant, what level,
 * and any per-item scratch variables its event logic has stored (empty for
 * stat-only enchants).
 */
public record EnchantData(CustomEnchant enchant, int level, EnchantVariables variables) {
}
