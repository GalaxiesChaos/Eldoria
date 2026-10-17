package me.chaos.EldoriaForge.recipe;

import com.nexomc.nexo.api.NexoItems;
import me.chaos.EldoriaDrill.base.DrillBaseRegistry;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.upgrade.DrillUpgradeRegistry;
import me.chaos.EldoriaItems.Item.CustomItemService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves forge item templates into real ItemStacks.
 * A template is either a vanilla material ("IRON_INGOT") or a Nexo item ("nexo:<id>").
 * Resolution is lazy + cached: Nexo loads its items asynchronously after server start,
 * so an unresolvable Nexo item is NOT cached and retried on the next access.
 */
public final class ForgeItems {
    public static final String NEXO_PREFIX = "nexo:";
    public static final String ELDR_PREFIX = "eldoria:";
    public static final String DRILL_PREFIX = "drill:";
    public static final String DRILLBASE_PREFIX = "drillbase:";
    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    private ForgeItems() {
    }

    public static boolean isNexo(String template) {
        return template != null && template.toLowerCase().startsWith(NEXO_PREFIX);
    }

    public static boolean isEldoria(String template) {
        return template != null && template.toLowerCase().startsWith(ELDR_PREFIX);
    }

    public static boolean isDrill(String template) {
        return template != null && template.toLowerCase().startsWith(DRILL_PREFIX);
    }

    public static boolean isDrillBase(String template) {
        return template != null && template.toLowerCase().startsWith(DRILLBASE_PREFIX);
    }

    /** Fresh copy of the resolved item; BARRIER as fallback (never null). */
    public static ItemStack resolve(String template) {
        if (template == null || template.isBlank()) {
            return ItemStack.of(Material.BARRIER);
        }
        String key = template.toLowerCase();
        ItemStack cached = CACHE.get(key);
        if (cached != null) {
            return cached.clone();
        }
        ItemStack built = build(key);
        if (built == null) {
            return ItemStack.of(Material.BARRIER);
        }
        CACHE.put(key, built);
        return built.clone();
    }

    /** Display name of the template, used in lores/messages. */
    public static Component displayName(String template) {
        ItemStack stack = resolve(template);
        ItemMeta meta = stack.getItemMeta();
        Component name = meta != null ? meta.displayName() : null;
        return name != null ? name : Component.translatable(stack.translationKey());
    }

    /** Nexo item id of a stack, or null when it is not a Nexo item (or Nexo is absent). */
    public static String nexoId(ItemStack stack) {
        if (Bukkit.getPluginManager().getPlugin("Nexo") == null) {
            return null;
        }
        return NexoItems.idFromItem(stack);
    }

    private static ItemStack build(String template) {
        if (isNexo(template)) {
            if (Bukkit.getPluginManager().getPlugin("Nexo") == null) {
                Bukkit.getLogger().warning("[EldoriaForge]: Nexo-Item '" + template + "' kann nicht aufgelöst werden, Nexo fehlt!");
                return null;
            }
            String id = template.substring(NEXO_PREFIX.length());
            if (NexoItems.itemFromId(id) == null) {
                Bukkit.getLogger().warning("[EldoriaForge]: Nexo-Item '" + id + "' ist (noch) nicht registriert.");
                return null; // not cached -> retried once Nexo has loaded its items
            }
            return NexoItems.itemFromId(id).build();
        }

        if (isEldoria(template)) {
            ItemStack stack = CustomItemService.createItemStack(template.substring(ELDR_PREFIX.length()));
            if (stack == null) {
                Bukkit.getLogger().warning("[EldoriaForge]: Eldoria-Item '" + template + "' ist nicht registriert.");
                return ItemStack.of(Material.BARRIER);
            }
            return stack;
        }
        if (isDrill(template)) {
            return DrillUpgradeRegistry.get(template.substring(DRILL_PREFIX.length()))
                    .map(DrillItems::createUpgrade)
                    .orElseGet(() -> {
                        Bukkit.getLogger().warning("[EldoriaForge]: Bohrupgrade '" + template + "' ist nicht registriert.");
                        return ItemStack.of(Material.BARRIER);
                    });
        }
        if (isDrillBase(template)) {
            return DrillBaseRegistry.get(template.substring(DRILLBASE_PREFIX.length()))
                    .map(DrillItems::createBase)
                    .orElseGet(() -> {
                        Bukkit.getLogger().warning("[EldoriaForge]: Bohrerbasis '" + template + "' ist nicht registriert.");
                        return ItemStack.of(Material.BARRIER);
                    });
        }

        Material material = Material.matchMaterial(template.toUpperCase());
        if (material == null) {
            Bukkit.getLogger().warning("[EldoriaForge]: Unbekanntes Material: " + template);
            return ItemStack.of(Material.BARRIER);
        }
        return ItemStack.of(material);
    }
}
