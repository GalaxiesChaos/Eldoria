package net.eldoria.drill.stat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable, additive collection of stat values. */
public final class DrillStatSet {

    public static final DrillStatSet EMPTY = new DrillStatSet(new LinkedHashMap<>());

    private final Map<DrillStat, Double> values;

    private DrillStatSet(Map<DrillStat, Double> values) {
        this.values = values;
    }

    public double get(DrillStat stat) {
        return values.getOrDefault(stat, 0.0);
    }

    public Map<DrillStat, Double> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public DrillStatSet plus(DrillStatSet other) {
        Map<DrillStat, Double> merged = new LinkedHashMap<>(values);
        other.values.forEach((stat, value) -> merged.merge(stat, value, Double::sum));
        return new DrillStatSet(merged);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<DrillStat, Double> map = new LinkedHashMap<>();

        public Builder add(DrillStat stat, double value) {
            map.merge(stat, value, Double::sum);
            return this;
        }

        public DrillStatSet build() {
            return new DrillStatSet(new LinkedHashMap<>(map));
        }
    }
}
