package me.chaos.EldoriaDrill.base;

import me.chaos.EldoriaDrill.fuel.FuelType;
import me.chaos.EldoriaDrill.stat.DrillStat;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Static template of a drill base (the item the player holds).
 * <p>
 * Use a NETHERITE_PICKAXE (default) or DIAMOND_PICKAXE as icon: the addon enforces the mining level itself,
 * so the item must not limit vanilla drops.
 */
public final class DrillBase {

    public static final int MODULE_SLOTS = 3;

    private final String id;
    private final String displayName;
    private final Material icon;
    private final DrillStatSet stats;
    private final int baseMiningLevel;
    private final boolean[] lockedModules;
    private final Set<String> acceptedFuels;

    private DrillBase(Builder b) {
        this.id = b.id;
        this.displayName = b.displayName;
        this.icon = b.icon;
        this.stats = b.stats.build();
        this.baseMiningLevel = b.baseMiningLevel;
        this.lockedModules = b.lockedModules.clone();
        this.acceptedFuels = Set.copyOf(b.acceptedFuels);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public Material icon() {
        return icon;
    }

    public DrillStatSet stats() {
        return stats;
    }

    /** Mining level the base brings on its own (-1 = needs a drill head). */
    public int baseMiningLevel() {
        return baseMiningLevel;
    }

    public boolean isModuleLocked(int moduleIndex) {
        return moduleIndex < 0 || moduleIndex >= MODULE_SLOTS || lockedModules[moduleIndex];
    }

    /** Empty set = accepts every registered fuel. */
    public boolean acceptsFuel(FuelType fuel) {
        return acceptedFuels.isEmpty() || acceptedFuels.contains(fuel.id());
    }

    public static Builder builder(String id, String displayName) {
        return new Builder(id, displayName);
    }

    public static final class Builder {
        private final String id;
        private final String displayName;
        private Material icon = Material.NETHERITE_PICKAXE;
        private final DrillStatSet.Builder stats = DrillStatSet.builder();
        private int baseMiningLevel = -1;
        private final boolean[] lockedModules = new boolean[MODULE_SLOTS];
        private final Set<String> acceptedFuels = new HashSet<>();

        private Builder(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public Builder icon(Material icon) {
            this.icon = icon;
            return this;
        }

        public Builder stat(DrillStat stat, double value) {
            stats.add(stat, value);
            return this;
        }

        public Builder baseMiningLevel(int level) {
            this.baseMiningLevel = level;
            return this;
        }

        /** Locks module slot 0, 1 or 2 for this base. */
        public Builder lockModule(int index) {
            lockedModules[index] = true;
            return this;
        }

        /** Restricts the base to these fuel ids (default: all fuels). */
        public Builder acceptFuels(String... fuelIds) {
            acceptedFuels.addAll(Set.of(fuelIds));
            return this;
        }

        public DrillBase build() {
            return new DrillBase(this);
        }
    }
}
