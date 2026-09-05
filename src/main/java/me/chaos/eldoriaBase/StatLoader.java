package me.chaos.eldoriaBase;

import me.chaos.eldoriaBase.Stats.Mining.MiningDropchanceStat;
import me.chaos.eldoriaBase.Stats.Mining.MiningSpeedStat;
import me.chaos.eldoriaBase.Stats.StatRegistry;

public class StatLoader {
    public static void loadStats(Main main) {
        StatRegistry.register(new MiningSpeedStat(main));
        StatRegistry.register(new MiningDropchanceStat(main));

        // Add new stats here - that's the whole registration step.
    }
}