package me.chaos.EldoriaItems.Item;

/**
 * Rough functional grouping for an {@link ItemType}. Mainly useful later for logic like
 * "can this go in an armor slot" or "is this a pure crafting material" - not shown in any lore.
 */
public enum ItemCategory {
    TOOL,
    WEAPON,
    ARMOR,
    MATERIAL,
    ARTIFACT,
    MISC
}
