package me.chaos.EldoriaItems.Item;

import me.chaos.EldoriaItems.Item.Ability.ItemAbility;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantment;
import me.chaos.EldoriaItems.Item.Obtain.ObtainInfo;
import me.chaos.EldoriaItems.Item.Obtain.ObtainMethod;
import me.chaos.EldoriaItems.Item.Stats.StatType;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent constructor for custom items. Calling {@link #build()} automatically registers the
 * resulting {@link CustomItemTemplate} in {@link CustomItemRegistry} under {@code id}, ready
 * to be turned into an ItemStack via {@link CustomItemService#createItemStack(String)}.
 * <p>
 * Example:
 * <pre>{@code
 * CustomItemTemplate steelDrill = new CustomItemBuilder("steel_drill", Material.IRON_PICKAXE)
 *         .displayName("Stahlbohrer")
 *         .rarity(ItemRarity.SELTEN)
 *         .type(DefaultItemTypes.DRILL)
 *         .stat(DefaultStatTypes.MINING_SPEED, 15)
 *         .stat(DefaultStatTypes.DROPCHANCE, 10)
 *         .enchant(effizienzPlus, 3)
 *         .obtain(ObtainMethod.CRAFTING, "Amboss, 4x Stahlbarren")
 *         .build();
 * }</pre>
 */
public class CustomItemBuilder {
    private final String id;
    private final Material material;
    private String displayName;
    private ItemRarity rarity = ItemRarity.GEWOEHNLICH;
    private ItemType type;
    private final Map<StatType, Double> stats = new LinkedHashMap<>();
    private final Map<CustomEnchantment, Integer> enchants = new LinkedHashMap<>();
    private ItemAbility ability;
    private ObtainInfo obtainInfo;
    private boolean unbreakable = false;
    private String setId;
    private boolean hideObtainLore = false;

    public CustomItemBuilder(String id, Material material) {
        this.id = Objects.requireNonNull(id, "id");
        this.material = Objects.requireNonNull(material, "material");
        this.displayName = id;
    }

    public CustomItemBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /** Sets the item's base rarity. Defaults to {@link ItemRarity#GEWOEHNLICH} if never called. */
    public CustomItemBuilder rarity(ItemRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public CustomItemBuilder type(ItemType type) {
        this.type = type;
        return this;
    }

    /** Adds/overwrites one base stat, e.g. {@code .stat(DefaultStatTypes.DROPCHANCE, 10)}. */
    public CustomItemBuilder stat(StatType stat, double value) {
        stats.put(stat, value);
        return this;
    }

    public CustomItemBuilder enchant(CustomEnchantment enchant, int level) {
        enchants.put(enchant, level);
        return this;
    }

    public CustomItemBuilder ability(ItemAbility ability) {
        this.ability = ability;
        return this;
    }

    public CustomItemBuilder obtain(ObtainMethod method, String detail) {
        this.obtainInfo = new ObtainInfo(method, detail);
        return this;
    }

    /** Hides the "Erhalten durch: ..." line from the lore while still keeping it in the data. */
    public CustomItemBuilder hideObtainLore(boolean hide) {
        this.hideObtainLore = hide;
        return this;
    }

    public CustomItemBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    /** Marks this item as belonging to an {@link me.chaos.EldoriaItems.Sets.ItemSet}. */
    public CustomItemBuilder partOfSet(String setId) {
        this.setId = setId;
        return this;
    }

    public CustomItemTemplate build() {
        if (type == null) {
            throw new IllegalStateException("Item '" + id + "' has no ItemType set - call .type(...)");
        }
        if (obtainInfo == null) {
            throw new IllegalStateException("Item '" + id + "' has no ObtainInfo set - call .obtain(...)");
        }

        CustomItemTemplate template = new CustomItemTemplate(
                id, material, displayName, rarity, type, stats, enchants,
                ability, obtainInfo, unbreakable, setId, hideObtainLore
        );

        CustomItemRegistry.register(template);
        return template;
    }
}
