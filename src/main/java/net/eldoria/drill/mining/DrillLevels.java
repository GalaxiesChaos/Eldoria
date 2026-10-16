package net.eldoria.drill.mining;

/** Mining level constants. Add your own (e.g. MITHRIL = 5) and use them in heads/overrides. */
public final class DrillLevels {

    public static final int NONE = -1;
    public static final int WOOD = 0;
    public static final int STONE = 1;
    public static final int IRON = 2;
    public static final int DIAMOND = 3;
    public static final int NETHERITE = 4;

    private DrillLevels() {
    }

    public static String name(int level) {
        return switch (level) {
            case NONE -> "Keine";
            case WOOD -> "Holz";
            case STONE -> "Stein";
            case IRON -> "Eisen";
            case DIAMOND -> "Diamant";
            case NETHERITE -> "Netherit";
            default -> "Stufe " + level;
        };
    }
}
