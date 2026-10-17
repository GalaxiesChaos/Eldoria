package me.chaos.EldoriaItems.Item;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import me.chaos.EldoriaItems.Item.Ability.ItemAbility;
import me.chaos.EldoriaItems.Item.Ability.ItemAbilityRegistry;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantment;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantmentRegistry;
import me.chaos.EldoriaItems.Item.Stats.StatKeyBridge;
import me.chaos.EldoriaItems.Item.Stats.StatType;
import me.chaos.EldoriaItems.Item.Stats.StatTypeRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * All logic that turns a {@link CustomItemTemplate} + its persisted {@link CustomItemData}
 * into an actual {@link ItemStack} (display name, lore, PersistentDataContainer) and back again.
 * <p>
 * Lore is always built in this exact order, per the design spec:
 * <ol>
 *     <li>Stats (e.g. "Dropchance: 10")</li>
 *     <li>Type line, colored by current rarity (e.g. "SELTEN BOHRER")</li>
 *     <li>Custom enchants</li>
 *     <li>Active ability / special modifier (if any)</li>
 *     <li>Obtain method (e.g. "Erhalten durch: Boss-Drop (Enderdrache)")</li>
 * </ol>
 */
public class CustomItemService {

    // ---------------------------------------------------------------- creation

    public static ItemStack createItemStack(String templateId) {
        CustomItemTemplate template = CustomItemRegistry.get(templateId);
        if (template == null) return null;
        return createItemStack(template);
    }

    public static ItemStack createItemStack(CustomItemTemplate template) {
        ItemStack stack = ItemStack.of(template.getMaterial());
        CustomItemData data = CustomItemData.fresh(template.getId());
        writeData(stack, data);
        return render(stack, template, data);
    }

    // ---------------------------------------------------------------- loading / reading

    public static boolean isCustomItem(ItemStack stack) {
        return readData(stack) != null;
    }

    public static String getTemplateId(ItemStack stack) {
        CustomItemData data = readData(stack);
        return data == null ? null : data.getTemplateId();
    }

    public static CustomItemTemplate getTemplate(ItemStack stack) {
        CustomItemData data = readData(stack);
        if (data == null) return null;
        return CustomItemRegistry.get(data.getTemplateId());
    }

    public static ItemRarity getCurrentRarity(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return null;
        return ItemRarity.byKeyOrDefault(data.getRarityOverride(), template.getBaseRarity());
    }

    /** Combines the template's base stats with any bonuses gained from upgrades. */
    public static Map<StatType, Double> getEffectiveStats(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        Map<StatType, Double> result = new LinkedHashMap<>();
        if (template == null || data == null) return result;

        result.putAll(template.getBaseStats());
        for (Map.Entry<String, Double> bonus : data.getStatBonuses().entrySet()) {
            StatType stat = StatTypeRegistry.get(bonus.getKey());
            if (stat == null) continue;
            result.merge(stat, bonus.getValue(), Double::sum);
        }
        return result;
    }

    /** Combines the template's base enchants with any level overrides from upgrades. */
    public static Map<CustomEnchantment, Integer> getEffectiveEnchants(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        Map<CustomEnchantment, Integer> result = new LinkedHashMap<>();
        if (template == null || data == null) return result;

        result.putAll(template.getBaseEnchants());
        for (Map.Entry<String, Integer> override : data.getEnchantLevels().entrySet()) {
            CustomEnchantment enchant = CustomEnchantmentRegistry.get(override.getKey());
            if (enchant == null) continue;
            result.put(enchant, override.getValue());
        }
        return result;
    }

    public static ItemAbility getEffectiveAbility(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return null;
        if (!data.getAbilityId().isEmpty()) {
            return ItemAbilityRegistry.get(data.getAbilityId());
        }
        return template.getBaseAbility();
    }

    // ---------------------------------------------------------------- upgrades

    /** Moves the item's current rarity one tier up (no-op if already Mythisch) and re-renders it. */
    public static ItemStack upgradeRarity(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return stack;

        ItemRarity current = ItemRarity.byKeyOrDefault(data.getRarityOverride(), template.getBaseRarity());
        ItemRarity next = current.next();
        data.setRarityOverride(next.name());

        writeData(stack, data);
        return render(stack, template, data);
    }

    /** Adds a flat, permanent bonus to one stat (on top of the template's base value) and re-renders. */
    public static ItemStack addStatBonus(ItemStack stack, StatType stat, double amount) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return stack;

        data.getStatBonuses().merge(stat.getKey(), amount, Double::sum);
        writeData(stack, data);
        return render(stack, template, data);
    }

    /** Sets (adds or upgrades) one custom enchant to the given level and re-renders. */
    public static ItemStack setEnchantLevel(ItemStack stack, CustomEnchantment enchant, int level) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return stack;

        data.getEnchantLevels().put(enchant.getId(), level);
        writeData(stack, data);
        return render(stack, template, data);
    }

    /** Assigns (or clears, with {@code null}) an active ability on this specific item instance. */
    public static ItemStack setAbility(ItemStack stack, ItemAbility ability) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return stack;

        data.setAbilityId(ability == null ? CustomItemData.NONE : ability.getId());
        writeData(stack, data);
        return render(stack, template, data);
    }

    /** Re-renders name/lore from the currently persisted data - useful after manually editing PDC. */
    public static ItemStack refresh(ItemStack stack) {
        CustomItemTemplate template = getTemplate(stack);
        CustomItemData data = readData(stack);
        if (template == null || data == null) return stack;
        return render(stack, template, data);
    }

    // ---------------------------------------------------------------- internals

    private static CustomItemData readData(ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null) return null;
        PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
        String json = pdc.get(EldoriaItemKeys.dataKey(), PersistentDataType.STRING);
        if (json == null) return null;

        try {
            return CustomItemData.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow();
        } catch (Exception e) {
            return null;
        }
    }

    private static void writeData(ItemStack stack, CustomItemData data) {
        ItemMeta meta = stack.getItemMeta();
        String json = CustomItemData.CODEC.encodeStart(JsonOps.INSTANCE, data).getOrThrow().toString();
        meta.getPersistentDataContainer().set(EldoriaItemKeys.dataKey(), PersistentDataType.STRING, json);
        stack.setItemMeta(meta);
    }

    private static ItemStack render(ItemStack stack, CustomItemTemplate template, CustomItemData data) {
        ItemMeta meta = stack.getItemMeta();

        ItemRarity rarity = ItemRarity.byKeyOrDefault(data.getRarityOverride(), template.getBaseRarity());

        meta.displayName(Component.text(template.getDisplayName())
                .color(rarity.getColor())
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(buildLore(template, data, rarity));

        if (template.isUnbreakable()) {
            meta.setUnbreakable(true);
        }

        stack.setItemMeta(meta);

        // Functional bridge: mirror the (base + bonus) stats into the eldoriaBase PDC stat
        // keys so StatManager actually applies them while the item is held/worn.
        StatKeyBridge.applyFunctionalStats(stack, mergeStats(template, data));

        return stack;
    }

    /** Template base stats merged with the instance's upgrade bonuses. */
    private static Map<StatType, Double> mergeStats(CustomItemTemplate template, CustomItemData data) {
        Map<StatType, Double> merged = new LinkedHashMap<>(template.getBaseStats());
        for (Map.Entry<String, Double> bonus : data.getStatBonuses().entrySet()) {
            StatType stat = StatTypeRegistry.get(bonus.getKey());
            if (stat == null) continue;
            merged.merge(stat, bonus.getValue(), Double::sum);
        }
        return merged;
    }


    /**
     * Recipe ingredient that accepts ANY instance of the given custom item template,
     * regardless of upgrades (rarity override, stat bonuses, enchant levels, ...).
     */
    public static RecipeChoice templateChoice(String templateId) {
        ItemStack example = createItemStack(templateId);
        if (example == null) {
            throw new IllegalArgumentException("Unknown custom item template: " + templateId);
        }
        return RecipeChoice.predicateChoice(
                stack -> templateId.equals(getTemplateId(stack)),
                example
        );
    }

    private static List<Component> buildLore(CustomItemTemplate template, CustomItemData data, ItemRarity rarity) {
        List<Component> lore = new ArrayList<>();

        // 1) Stats
        Map<StatType, Double> stats = new LinkedHashMap<>(template.getBaseStats());
        for (Map.Entry<String, Double> bonus : data.getStatBonuses().entrySet()) {
            StatType stat = StatTypeRegistry.get(bonus.getKey());
            if (stat == null) continue;
            stats.merge(stat, bonus.getValue(), Double::sum);
        }
        for (Map.Entry<StatType, Double> entry : stats.entrySet()) {
            lore.add(entry.getKey().formatLine(entry.getValue()));
        }

        // 2) Type - last line of the "header" block, colored + bold in the rarity color
        lore.add(Component.text(rarity.getDisplayName().toUpperCase() + " " + template.getType().getDisplayName().toUpperCase())
                .color(rarity.getColor())
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        // 3) Enchants
        Map<CustomEnchantment, Integer> enchants = new LinkedHashMap<>(template.getBaseEnchants());
        for (Map.Entry<String, Integer> override : data.getEnchantLevels().entrySet()) {
            CustomEnchantment enchant = CustomEnchantmentRegistry.get(override.getKey());
            if (enchant == null) continue;
            enchants.put(enchant, override.getValue());
        }
        if (!enchants.isEmpty()) {
            lore.add(Component.empty());
            for (Map.Entry<CustomEnchantment, Integer> entry : enchants.entrySet()) {
                lore.add(entry.getKey().getLoreLine(entry.getValue()));
            }
        }

        // 4) Active ability / special modifier (if any)
        ItemAbility ability = data.getAbilityId().isEmpty()
                ? template.getBaseAbility()
                : ItemAbilityRegistry.get(data.getAbilityId());
        if (ability != null) {
            lore.add(Component.empty());
            lore.add(Component.text("Fähigkeit: ")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(ability.getDisplayName()));
            lore.addAll(ability.getDescriptionLines());
        }

        // 5) Obtain method
        if (!template.isHideObtainLore()) {
            lore.add(Component.empty());
            lore.add(template.getObtainInfo().toLoreLine());
        }

        return lore;
    }
}
