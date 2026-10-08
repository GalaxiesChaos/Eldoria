package me.chaos.EldoriaEnchants;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.chaos.eldoriaBase.Utils.RomanNumeral;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reads and writes an item's Eldoria enchant data. Everything (levels + any
 * per-enchant scratch variables) is stored as a single JSON blob in the item's
 * PersistentDataContainer, using the same "Codec/Gson to JSON" approach the rest
 * of the plugin already uses for player data - just scoped to the item instead of
 * a player file, since enchant data needs to travel with the item itself.
 * <p>
 * Every write through this class also rebuilds the item's attribute modifiers and
 * enchant lore, so callers never touch ItemMeta attributes/lore directly for
 * anything enchant-related.
 */
public class ItemEnchantHandler {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, StoredEnchant>>() {}.getType();
    private static final NamespacedKey DATA_KEY = new NamespacedKey("eldoriaenchants", "data");

    private static final String LORE_START = "\u27EAVerzauberungen\u27EB";
    private static final String LORE_END = "\u27EA/Verzauberungen\u27EB";

    private ItemEnchantHandler() {}

    /** Plain serialization shape for the JSON blob - deliberately a bare class (not a record) so Gson never needs anything fancier than field reflection. */
    private static final class StoredEnchant {
        int level;
        Map<String, String> vars;

        StoredEnchant() {}

        StoredEnchant(int level, Map<String, String> vars) {
            this.level = level;
            this.vars = vars;
        }
    }

    public static Map<CustomEnchant, EnchantData> getEnchants(ItemStack item) {
        Map<CustomEnchant, EnchantData> result = new LinkedHashMap<>();
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) return result;

        ItemMeta meta = item.getItemMeta();
        String json = meta.getPersistentDataContainer().get(DATA_KEY, PersistentDataType.STRING);
        if (json == null || json.isBlank()) return result;

        Map<String, StoredEnchant> raw = GSON.fromJson(json, MAP_TYPE);
        if (raw == null) return result;

        for (Map.Entry<String, StoredEnchant> entry : raw.entrySet()) {
            CustomEnchant enchant = EnchantRegistry.get(entry.getKey());
            if (enchant == null) continue; // enchant was removed/renamed since this item was saved

            StoredEnchant stored = entry.getValue();
            Map<String, String> vars = stored.vars == null ? new HashMap<>() : new HashMap<>(stored.vars);
            result.put(enchant, new EnchantData(enchant, stored.level, new EnchantVariables(vars)));
        }
        return result;
    }

    public static int getLevel(ItemStack item, CustomEnchant enchant) {
        EnchantData data = getEnchants(item).get(enchant);
        return data == null ? 0 : data.level();
    }

    /** Adds (or removes, for a negative amount) levels relative to the current level, keeping existing variables. */
    public static void addLevel(ItemStack item, CustomEnchant enchant, int amount) {
        Map<CustomEnchant, EnchantData> current = getEnchants(item);
        EnchantData existing = current.get(enchant);
        int newLevel = (existing == null ? 0 : existing.level()) + amount;
        EnchantVariables vars = existing == null ? new EnchantVariables() : existing.variables();
        setEnchant(item, enchant, newLevel, vars);
    }

    public static void removeEnchant(ItemStack item, CustomEnchant enchant) {
        setEnchant(item, enchant, 0, new EnchantVariables());
    }

    /** Sets an enchant to an exact level (clamped to the enchant's absolute max), with the given variables. Level <= 0 removes it. */
    public static void setEnchant(ItemStack item, CustomEnchant enchant, int level, EnchantVariables variables) {
        Map<CustomEnchant, EnchantData> current = new LinkedHashMap<>(getEnchants(item));

        if (level <= 0) {
            current.remove(enchant);
        } else {
            int clamped = Math.min(level, enchant.getMaxLevel());
            current.put(enchant, new EnchantData(enchant, clamped, variables == null ? new EnchantVariables() : variables));
        }

        write(item, current);
    }

    private static void write(ItemStack item, Map<CustomEnchant, EnchantData> enchants) {
        ItemMeta meta = item.getItemMeta();

        Map<String, StoredEnchant> raw = new LinkedHashMap<>();
        for (EnchantData data : enchants.values()) {
            raw.put(data.enchant().getId(), new StoredEnchant(data.level(), data.variables().raw()));
        }

        if (raw.isEmpty()) {
            meta.getPersistentDataContainer().remove(DATA_KEY);
        } else {
            meta.getPersistentDataContainer().set(DATA_KEY, PersistentDataType.STRING, GSON.toJson(raw, MAP_TYPE));
        }

        applyAttributes(meta, enchants);
        applyLore(meta, enchants);

        item.setItemMeta(meta);
    }

    private static void applyAttributes(ItemMeta meta, Map<CustomEnchant, EnchantData> enchants) {
        // Strip every modifier we previously added (identified by our namespace) before reapplying,
        // so removing/lowering an enchant actually removes/lowers the stat too.
        for (EnchantStat stat : EnchantStat.values()) {
            Collection<AttributeModifier> existing = meta.getAttributeModifiers(stat.getAttribute());
            if (existing == null) continue;
            for (AttributeModifier modifier : List.copyOf(existing)) {
                if ("eldoriaenchants".equals(modifier.getKey().getNamespace())) {
                    meta.removeAttributeModifier(stat.getAttribute(), modifier);
                }
            }
        }

        for (EnchantData data : enchants.values()) {
            if (!(data.enchant() instanceof StatEnchant statEnchant)) continue;

            for (StatDefinition def : statEnchant.getStatDefinitions()) {
                double value = def.valueAtLevel(data.level());
                if (value == 0) continue;

                NamespacedKey key = new NamespacedKey("eldoriaenchants",
                        data.enchant().getId() + "_" + def.stat().name().toLowerCase(Locale.ROOT));
                AttributeModifier modifier = new AttributeModifier(key, value, def.operation(), def.stat().getSlotGroup());
                meta.addAttributeModifier(def.stat().getAttribute(), modifier);
            }
        }
    }

    private static void applyLore(ItemMeta meta, Map<CustomEnchant, EnchantData> enchants) {
        List<Component> lore = new ArrayList<>();
        List<Component> existingLore = meta.lore();

        // Keep any lore that isn't our own enchant block untouched.
        if (existingLore != null) {
            boolean skipping = false;
            for (Component line : existingLore) {
                String plain = PlainTextComponentSerializer.plainText().serialize(line);
                if (plain.equals(LORE_START)) { skipping = true; continue; }
                if (plain.equals(LORE_END)) { skipping = false; continue; }
                if (!skipping) lore.add(line);
            }
        }

        if (!enchants.isEmpty()) {
            lore.add(Component.text(LORE_START).color(NamedTextColor.DARK_GRAY));
            for (EnchantData data : enchants.values()) {
                lore.add(data.enchant().getDisplayName()
                        .append(Component.text(" " + RomanNumeral.toRoman(data.level())))
                        .color(NamedTextColor.GRAY));
            }
            lore.add(Component.text(LORE_END).color(NamedTextColor.DARK_GRAY));
        }

        meta.lore(lore.isEmpty() ? null : lore);
    }
}
