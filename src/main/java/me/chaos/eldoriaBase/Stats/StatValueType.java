package me.chaos.eldoriaBase.Stats;

public enum StatValueType {

    /**
     * The stat's value is applied directly / per unit (e.g. Staerke -> +Schaden pro Punkt).
     */
    PER_UNIT,

    /**
     * The stat's value represents a chance relative to a max value (e.g. Dropchance:
     * 100 = garantiert +1 Drop, 50 = 50% Chance auf +1 Drop). See {@link Stat#rollChanceMultiplier(double)}.
     */
    CHANCE
}

