package me.chaos.EldoriaItems.Item.Obtain;

/** The general category an item is obtained through. */
public enum ObtainMethod {
    MOB_DROP("Mob-Drop"),
    BOSS_DROP("Boss-Drop"),
    CRAFTING("Craft"),
    ORE_REPLACEMENT("Abbau-Bonusdrop"),
    FORGE("Schmiede"),
    SHOP("H\u00e4ndler");

    private final String label;

    ObtainMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
