package me.chaos.EldoriaItems.Item;

import me.chaos.EldoriaItems.Item.Ability.ItemAbility;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantment;
import me.chaos.EldoriaItems.Item.Obtain.ObtainInfo;
import me.chaos.EldoriaItems.Item.Stats.StatType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable blueprint for a custom item, produced by {@link CustomItemBuilder} and kept in
 * {@link CustomItemRegistry}. Use {@link CustomItemService} to turn a template into an actual,
 * giveable {@link org.bukkit.inventory.ItemStack}, or to read one back out of a stack.
 */
public class CustomItemTemplate {
    private final String id;
    private final Material material;
    private final String displayName;
    private final ItemRarity baseRarity;
    private final ItemType type;
    private final Map<StatType, Double> baseStats;
    private final Map<CustomEnchantment, Integer> baseEnchants;
    private final ItemAbility baseAbility; // nullable
    private final ObtainInfo obtainInfo;
    private final boolean unbreakable;
    private final String setId; // nullable, links this item to an ItemSet
    private final boolean hideObtainLore;

    CustomItemTemplate(String id, Material material, String displayName, ItemRarity baseRarity, ItemType type,
                        Map<StatType, Double> baseStats, Map<CustomEnchantment, Integer> baseEnchants,
                        ItemAbility baseAbility, ObtainInfo obtainInfo, boolean unbreakable,
                        String setId, boolean hideObtainLore) {
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.baseRarity = baseRarity;
        this.type = type;
        this.baseStats = new LinkedHashMap<>(baseStats);
        this.baseEnchants = new LinkedHashMap<>(baseEnchants);
        this.baseAbility = baseAbility;
        this.obtainInfo = obtainInfo;
        this.unbreakable = unbreakable;
        this.setId = setId;
        this.hideObtainLore = hideObtainLore;
    }

    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemRarity getBaseRarity() {
        return baseRarity;
    }

    public ItemType getType() {
        return type;
    }

    public Map<StatType, Double> getBaseStats() {
        return baseStats;
    }

    public Map<CustomEnchantment, Integer> getBaseEnchants() {
        return baseEnchants;
    }

    public ItemAbility getBaseAbility() {
        return baseAbility;
    }

    public ObtainInfo getObtainInfo() {
        return obtainInfo;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public String getSetId() {
        return setId;
    }

    public boolean isHideObtainLore() {
        return hideObtainLore;
    }
}
