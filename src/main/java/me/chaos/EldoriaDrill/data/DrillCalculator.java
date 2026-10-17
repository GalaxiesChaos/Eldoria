package me.chaos.EldoriaDrill.data;

import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.base.DrillBaseRegistry;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import me.chaos.EldoriaDrill.upgrade.DrillUpgrade;
import me.chaos.EldoriaDrill.upgrade.UpgradeSlotType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Derived values: combined stats, mining level, fuel cost, effects. */
public final class DrillCalculator {

    private DrillCalculator() {
    }

    public static Optional<DrillBase> base(DrillData data) {
        return DrillBaseRegistry.get(data.baseId());
    }

    /** Installed upgrades that are valid for their slot and for this base (locked module slots are ignored). */
    public static List<DrillUpgrade> activeUpgrades(DrillData data) {
        DrillBase base = base(data).orElse(null);
        List<DrillUpgrade> result = new ArrayList<>();
        for (Map.Entry<DrillSlot, ItemStack> entry : data.installedView().entrySet()) {
            DrillSlot slot = entry.getKey();
            if (base != null && slot.isModule() && base.isModuleLocked(slot.moduleIndex())) {
                continue;
            }
            Optional<DrillUpgrade> upgrade = DrillItems.upgradeOf(entry.getValue());
            if (upgrade.isEmpty() || upgrade.get().slotType() != slot.type()) {
                continue;
            }
            if (base != null && !upgrade.get().allowedOn(base)) {
                continue;
            }
            result.add(upgrade.get());
        }
        return result;
    }

    /** Base stats + the stats of every active upgrade. */
    public static DrillStatSet totalStats(DrillData data) {
        DrillStatSet total = base(data).map(DrillBase::stats).orElse(DrillStatSet.EMPTY);
        for (DrillUpgrade upgrade : activeUpgrades(data)) {
            total = total.plus(upgrade.stats());
        }
        return total;
    }

    /** Highest of base level and head level; -1 if the drill cannot mine yet. */
    public static int miningLevel(DrillData data) {
        int level = base(data).map(DrillBase::baseMiningLevel).orElse(-1);
        for (DrillUpgrade upgrade : activeUpgrades(data)) {
            if (upgrade.slotType() == UpgradeSlotType.HEAD) {
                level = Math.max(level, upgrade.miningLevel());
            }
        }
        return level;
    }

    public static List<DrillEffect> effects(DrillData data) {
        List<DrillEffect> effects = new ArrayList<>();
        for (DrillUpgrade upgrade : activeUpgrades(data)) {
            effects.addAll(upgrade.effects());
        }
        return effects;
    }

    public static double maxFuel(DrillStatSet stats) {
        return Math.max(1, stats.get(DrillStatRegistry.MAX_FUEL));
    }

    /** 1 fuel per block, reduced by FUEL_EFFICIENCY (capped at -90%, minimum 0.1 per block). */
    public static double fuelCost(DrillStatSet stats) {
        double reduction = Math.min(90, stats.get(DrillStatRegistry.FUEL_EFFICIENCY));
        return Math.max(0.1, 1.0 - reduction / 100.0);
    }
}
