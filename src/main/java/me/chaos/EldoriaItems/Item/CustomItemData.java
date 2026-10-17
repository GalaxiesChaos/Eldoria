package me.chaos.EldoriaItems.Item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Everything about a *specific* item instance that can change after creation: its current
 * rarity (if upgraded past the template's base rarity), any stat bonuses gained from upgrades,
 * extra/raised enchant levels, and an assigned ability. This is the only thing stored in the
 * item's PersistentDataContainer - everything else (material, base stats, base type, obtain
 * info, ...) lives on the {@link CustomItemTemplate} and is looked up again via
 * {@link CustomItemRegistry} whenever the item is loaded.
 */
public class CustomItemData {
    public static final String NONE = ""; // sentinel for "no override" on string fields

    private final String templateId;
    private String rarityOverride;                   // "" = use template's base rarity
    private final Map<String, Double> statBonuses;    // statKey -> extra flat value from upgrades
    private final Map<String, Integer> enchantLevels; // enchantId -> current level (overrides template base)
    private String abilityId;                         // "" = use template's base ability (or none)

    public CustomItemData(String templateId, String rarityOverride, Map<String, Double> statBonuses,
                           Map<String, Integer> enchantLevels, String abilityId) {
        this.templateId = templateId;
        this.rarityOverride = rarityOverride == null ? NONE : rarityOverride;
        this.statBonuses = new HashMap<>(statBonuses);
        this.enchantLevels = new HashMap<>(enchantLevels);
        this.abilityId = abilityId == null ? NONE : abilityId;
    }

    public static CustomItemData fresh(String templateId) {
        return new CustomItemData(templateId, NONE, new HashMap<>(), new HashMap<>(), NONE);
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getRarityOverride() {
        return rarityOverride;
    }

    public void setRarityOverride(String rarityOverride) {
        this.rarityOverride = rarityOverride;
    }

    public Map<String, Double> getStatBonuses() {
        return statBonuses;
    }

    public Map<String, Integer> getEnchantLevels() {
        return enchantLevels;
    }

    public String getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(String abilityId) {
        this.abilityId = abilityId;
    }

    public static final Codec<CustomItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("TemplateId").forGetter(CustomItemData::getTemplateId),
            Codec.STRING.fieldOf("RarityOverride").forGetter(CustomItemData::getRarityOverride),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("StatBonuses").forGetter(CustomItemData::getStatBonuses),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("EnchantLevels").forGetter(CustomItemData::getEnchantLevels),
            Codec.STRING.fieldOf("AbilityId").forGetter(CustomItemData::getAbilityId)
    ).apply(instance, CustomItemData::new));
}
