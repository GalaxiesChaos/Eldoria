package me.chaos.EldoriaEnchants;

import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.function.IntToDoubleFunction;

/**
 * One stat a StatEnchant grants, plus the formula for how much of it you get at a
 * given level. {@code perLevel} can be anything (linear, diminishing returns, a
 * lookup table, whatever) - {@link #linear} just covers the common case.
 */
public record StatDefinition(EnchantStat stat, Operation operation, IntToDoubleFunction perLevel) {

    public double valueAtLevel(int level) {
        return perLevel.applyAsDouble(level);
    }

    /** Straight line: value = level * amountPerLevel. */
    public static StatDefinition linear(EnchantStat stat, Operation operation, double amountPerLevel) {
        return new StatDefinition(stat, operation, level -> level * amountPerLevel);
    }
}
