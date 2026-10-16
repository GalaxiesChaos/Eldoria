package net.eldoria.drill.upgrade;

public enum UpgradeSlotType {
    HEAD("Drillkopf"),
    ENGINE("Motor"),
    TANK("Extra-Tank"),
    PLATES("Verstärkte Platten"),
    MODIFIER("Modifikator"),
    MODULE("Modul");

    private final String displayName;

    UpgradeSlotType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
