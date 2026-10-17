package me.chaos.EldoriaContent.drill;

import me.chaos.EldoriaDrill.effect.DrillBreakContext;
import me.chaos.EldoriaDrill.effect.DrillEffect;

/**
 * Marker effect of the "Auto-Schmelzerei" drill module. The actual smelting happens in
 * {@link DrillDropEnhancements}, which listens on BlockDropItemEvent: every ore drop is
 * converted into its furnace product unless the drill carries Silk Touch.
 */
public final class AutoSmeltEffect implements DrillEffect {

    public static final AutoSmeltEffect INSTANCE = new AutoSmeltEffect();

    private AutoSmeltEffect() {
    }

    @Override
    public void onBlockBroken(DrillBreakContext context) {
        // no-op: the effect is consumed by DrillDropEnhancements via DrillCalculator.effects(...)
    }

    @Override
    public String describe() {
        return "Schmilzt abgebaute Erze automatisch (wirkungslos bei Silk Touch).";
    }
}
