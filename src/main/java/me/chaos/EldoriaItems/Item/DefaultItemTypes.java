package me.chaos.EldoriaItems.Item;

/**
 * Ready-to-use item types matching the design spec
 * (vanilla-ish tools/weapons + fully custom ones + a generic crafting-material type).
 * Call {@link #registerDefaults()} once during startup (done for you by EldoriaItemsModule).
 * <p>
 * Feel free to register more of your own anywhere else in the codebase - this class is just
 * a convenient starting point, not a closed list.
 */
public class DefaultItemTypes {
    public static ItemType PICKAXE;
    public static ItemType AXE;
    public static ItemType SWORD;
    public static ItemType DRILL;
    public static ItemType SHORTBOW;
    public static ItemType KATANA;
    public static ItemType ERZ; // generic "ore / raw metal" type for pure crafting materials
    public static ItemType MATERIAL;        // generic crafting material
    public static ItemType ARMOR;           // armor pieces
    public static ItemType FUEL;            // drill fuel items
    public static ItemType DRILL_PART;      // drill components (tanks, engines, plates, ...)
    public static ItemType DRILL_MODULE;    // installable drill modules

    public static void registerDefaults() {
        PICKAXE = ItemTypeRegistry.register(new ItemType("pickaxe", "Spitzhacke", ItemCategory.TOOL));
        AXE = ItemTypeRegistry.register(new ItemType("axe", "Axt", ItemCategory.TOOL));
        SWORD = ItemTypeRegistry.register(new ItemType("sword", "Schwert", ItemCategory.WEAPON));
        DRILL = ItemTypeRegistry.register(new ItemType("drill", "Bohrer", ItemCategory.TOOL));
        SHORTBOW = ItemTypeRegistry.register(new ItemType("shortbow", "Kurzbogen", ItemCategory.WEAPON));
        KATANA = ItemTypeRegistry.register(new ItemType("katana", "Katana", ItemCategory.WEAPON));
        ERZ = ItemTypeRegistry.register(new ItemType("erz", "Erz", ItemCategory.MATERIAL));
        MATERIAL = ItemTypeRegistry.register(new ItemType("material", "Material", ItemCategory.MATERIAL));
        ARMOR = ItemTypeRegistry.register(new ItemType("armor", "R\u00fcstung", ItemCategory.ARMOR));
        FUEL = ItemTypeRegistry.register(new ItemType("fuel", "Kraftstoff", ItemCategory.MISC));
        DRILL_PART = ItemTypeRegistry.register(new ItemType("drill_part", "Bohrerbauteil", ItemCategory.MISC));
        DRILL_MODULE = ItemTypeRegistry.register(new ItemType("drill_module", "Bohrmodul", ItemCategory.MISC));
    }
}
