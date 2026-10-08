package me.chaos.EldoriaEnchants;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * What kind of item an enchant is allowed to sit on.
 * <p>
 * Vanilla materials are matched directly by Material. For anything that ISN'T a
 * vanilla item - e.g. the planned "Drill" tool line - a custom item can tag its own
 * type by writing its EnchantTarget name into {@link #CUSTOM_TYPE_KEY} on its own
 * PDC, and it will match here automatically without this enum ever needing to know
 * that item class exists. That's the hook for "efficiency can go on drills, will be
 * implemented later": add a DRILL-tagged item later, DRILL target already works.
 */
public enum EnchantTarget {

    SWORD(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),

    AXE(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE),

    PICKAXE(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE),

    SHOVEL(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL),

    HOE(Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE),

    BOW(Material.BOW),
    CROSSBOW(Material.CROSSBOW),
    TRIDENT(Material.TRIDENT),

    HELMET(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET,
            Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET),

    CHESTPLATE(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE, Material.ELYTRA),

    LEGGINGS(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS),

    BOOTS(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS),

    /** Matches any of the four armor pieces above. */
    ARMOR(),

    /** Placeholder for the future custom "Drill" line. Only matches via the PDC tag. */
    DRILL(),

    /** Matches any non-air item. */
    ANY();

    /** Custom items tag themselves with this key + one of these enum names (as a string) to opt into a target. */
    public static final NamespacedKey CUSTOM_TYPE_KEY = new NamespacedKey("eldoriaenchants", "item_type");

    private final Set<Material> materials;

    EnchantTarget(Material... materials) {
        this.materials = materials.length == 0 ? EnumSet.noneOf(Material.class) : EnumSet.copyOf(List.of(materials));
    }

    public boolean matches(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        if (this == ANY) return true;
        if (this == ARMOR) {
            return HELMET.matches(item) || CHESTPLATE.matches(item) || LEGGINGS.matches(item) || BOOTS.matches(item);
        }
        if (materials.contains(item.getType())) return true;

        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        String customType = meta.getPersistentDataContainer().get(CUSTOM_TYPE_KEY, PersistentDataType.STRING);
        return customType != null && customType.equalsIgnoreCase(this.name());
    }
}
