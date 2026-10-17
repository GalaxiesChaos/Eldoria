package me.chaos.EldoriaDrill.upgrade;

import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.stat.DrillStat;
import me.chaos.EldoriaDrill.stat.DrillStatSet;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Static template of an upgrade item (head, engine, tank, plates, modifier or module). */
public final class DrillUpgrade {

    private final String id;
    private final String displayName;
    private final UpgradeSlotType slotType;
    private final Material icon;
    private final DrillStatSet stats;
    private final int miningLevel;
    private final Set<String> allowedBases;
    private final List<DrillEffect> effects;
    private final List<String> description;

    private DrillUpgrade(Builder b) {
        this.id = b.id;
        this.displayName = b.displayName;
        this.slotType = b.slotType;
        this.icon = b.icon;
        this.stats = b.stats.build();
        this.miningLevel = b.miningLevel;
        this.allowedBases = Set.copyOf(b.allowedBases);
        this.effects = List.copyOf(b.effects);
        this.description = List.copyOf(b.description);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public UpgradeSlotType slotType() {
        return slotType;
    }

    public Material icon() {
        return icon;
    }

    public DrillStatSet stats() {
        return stats;
    }

    /** Only meaningful for heads. */
    public int miningLevel() {
        return miningLevel;
    }

    public List<DrillEffect> effects() {
        return effects;
    }

    public List<String> description() {
        return description;
    }

    /** Empty restriction set = fits every base. */
    public boolean allowedOn(DrillBase base) {
        return allowedBases.isEmpty() || allowedBases.contains(base.id());
    }

    public static Builder builder(String id, String displayName, UpgradeSlotType slotType) {
        return new Builder(id, displayName, slotType);
    }

    public static final class Builder {
        private final String id;
        private final String displayName;
        private final UpgradeSlotType slotType;
        private Material icon = Material.PAPER;
        private final DrillStatSet.Builder stats = DrillStatSet.builder();
        private int miningLevel = -1;
        private final Set<String> allowedBases = new HashSet<>();
        private final List<DrillEffect> effects = new ArrayList<>();
        private final List<String> description = new ArrayList<>();

        private Builder(String id, String displayName, UpgradeSlotType slotType) {
            this.id = id;
            this.displayName = displayName;
            this.slotType = slotType;
        }

        public Builder icon(Material icon) {
            this.icon = icon;
            return this;
        }

        public Builder stat(DrillStat stat, double value) {
            stats.add(stat, value);
            return this;
        }

        public Builder miningLevel(int level) {
            this.miningLevel = level;
            return this;
        }

        public Builder restrictTo(String... baseIds) {
            allowedBases.addAll(Set.of(baseIds));
            return this;
        }

        public Builder effect(DrillEffect effect) {
            effects.add(effect);
            return this;
        }

        public Builder description(String... lines) {
            description.addAll(List.of(lines));
            return this;
        }

        public DrillUpgrade build() {
            return new DrillUpgrade(this);
        }
    }
}
