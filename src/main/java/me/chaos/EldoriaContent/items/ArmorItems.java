package me.chaos.EldoriaContent.items;

import me.chaos.EldoriaItems.Item.CustomItemBuilder;
import me.chaos.EldoriaItems.Item.CustomItemService;
import me.chaos.EldoriaItems.Item.DefaultItemTypes;
import me.chaos.EldoriaItems.Item.ItemRarity;
import me.chaos.EldoriaItems.Item.Obtain.ObtainMethod;
import me.chaos.EldoriaItems.Item.Stats.DefaultStatTypes;
import me.chaos.EldoriaItems.Sets.ItemSet;
import me.chaos.EldoriaItems.Sets.ItemSetRegistry;
import me.chaos.EldoriaItems.Sets.PotionSetBonusEffect;
import me.chaos.EldoriaItems.Sets.SetBonus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * The six Schürfer (miner) armor tiers, four pieces each. Every piece carries functional
 * mining stats (written to the PDC by CustomItemService -> StatKeyBridge). The Stahl set grants
 * Nachtsicht I with all 4 pieces, the Verstärkter-Stahl set already with 2 pieces.
 * <p>
 * Recipe scheme: each tier is the previous tier's piece in the middle, surrounded by 8 of the
 * tier material (miners = classic bronze armor pattern).
 */
public final class ArmorItems {

    private static final String[] SLOTS = {"helmet", "chestplate", "leggings", "boots"};
    private static final String[] SLOT_NAMES = {"Helm", "Brustplatte", "Beinschutz", "Stiefel"};
    private static final Material[] SLOT_MATERIALS_BASE = {
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};

    private ArmorItems() {
    }

    public static void register(JavaPlugin plugin) {
        // tier 1: bronze pattern
        registerTier(plugin, "miners", "", "Schürfer", ItemRarity.GEWOEHNLICH,
                SLOT_MATERIALS_BASE, "bronze", null, 10, 0, 0);
        // tier 2-6: surround previous tier
        registerTier(plugin, "silver_miners", "Silber-", "Silber-Sch\u00fcrfer", ItemRarity.UNGEWOEHNLICH,
                chainmail(), "silver", "miners", 50, 10, 0);
        registerTier(plugin, "diamond_miners", "Diamant-", "Diamant-Sch\u00fcrfer", ItemRarity.UNGEWOEHNLICH,
                diamond(), "vanilla:diamond", "silver_miners", 75, 25, 0);
        registerTier(plugin, "steel_miners", "Stahl-", "Stahl-Sch\u00fcrfer", ItemRarity.SELTEN,
                iron(), "steel", "diamond_miners", 100, 25, 0);
        registerTier(plugin, "reinforced_steel_miners", "Verst\u00e4rkter Stahl-", "Verst. Stahl-Sch\u00fcrfer",
                ItemRarity.EPISCH, netherite(), "reinforced_steel_block", "steel_miners", 100, 50, 0);
        registerTier(plugin, "darksteel_miners", "Dunkelstahl-", "Dunkelstahl-Sch\u00fcrfer", ItemRarity.LEGENDAER,
                netherite(), "darksteel_block", "reinforced_steel_miners", 150, 75, 10);

        // set bonuses (Nachtsicht)
        ItemSetRegistry.register(new ItemSet("steel_miners_set", "Stahl-Sch\u00fcrferr\u00fcstung",
                memberIds("steel_miners"), List.of(nightVisionBonus(4))));
        ItemSetRegistry.register(new ItemSet("reinforced_steel_miners_set", "Verst. Stahl-Sch\u00fcrferr\u00fcstung",
                memberIds("reinforced_steel_miners"), List.of(nightVisionBonus(2))));
        // sets without bonuses, so the set header shows up in the lore
        ItemSetRegistry.register(new ItemSet("miners_set", "Sch\u00fcrferr\u00fcstung",
                memberIds("miners"), List.of()));
        ItemSetRegistry.register(new ItemSet("silver_miners_set", "Silber-Sch\u00fcrferr\u00fcstung",
                memberIds("silver_miners"), List.of()));
        ItemSetRegistry.register(new ItemSet("diamond_miners_set", "Diamant-Sch\u00fcrferr\u00fcstung",
                memberIds("diamond_miners"), List.of()));
        ItemSetRegistry.register(new ItemSet("darksteel_miners_set", "Dunkelstahl-Sch\u00fcrferr\u00fcstung",
                memberIds("darksteel_miners"), List.of()));
    }

    private static SetBonus nightVisionBonus(int pieces) {
        Component line = Component.text(pieces + " Teile: ", NamedTextColor.DARK_GRAY)
                .append(Component.text("Nachtsicht I", NamedTextColor.LIGHT_PURPLE));
        return new SetBonus(pieces, List.of(line), List.of(new PotionSetBonusEffect(PotionEffectType.NIGHT_VISION, 1)));
    }

    private static List<String> memberIds(String tierPrefix) {
        return List.of(tierPrefix + "_helmet", tierPrefix + "_chestplate",
                tierPrefix + "_leggings", tierPrefix + "_boots");
    }

    private static void registerTier(JavaPlugin plugin, String tierId, String namePrefix, String obtainName,
                                     ItemRarity rarity, Material[] pieceMaterials, String materialKey,
                                     String previousTierId, double speed, double fortune, double spread) {
        String setId = tierId + "_set";
        for (int i = 0; i < SLOTS.length; i++) {
            String id = tierId + "_" + SLOTS[i];
            CustomItemBuilder builder = new CustomItemBuilder(id, pieceMaterials[i])
                    .displayName(namePrefix + SLOT_NAMES[i])
                    .rarity(rarity)
                    .type(DefaultItemTypes.ARMOR)
                    .obtain(ObtainMethod.CRAFTING,
                            previousTierId == null
                                    ? obtainName + "r\u00fcstung (" + materialName(materialKey) + ")"
                                    : obtainName + "r\u00fcstung (" + materialName(materialKey) + " + vorheriges Teil)")
                    .partOfSet(setId)
                    .stat(DefaultStatTypes.MINING_SPEED, speed);
            if (fortune > 0) builder.stat(DefaultStatTypes.MINING_FORTUNE, fortune);
            if (spread > 0) builder.stat(DefaultStatTypes.MINING_SPREAD, spread);
            builder.build();

            ShapedRecipe recipe;
            if (previousTierId == null) {
                recipe = baseArmorRecipe(plugin, id, SLOTS[i], materialKey);
            } else {
                recipe = surroundRecipe(plugin, id, materialKey, previousTierId + "_" + SLOTS[i]);
            }
            try {
                plugin.getServer().addRecipe(recipe);
            } catch (IllegalStateException e) {
                plugin.getLogger().warning("Rezept bereits registriert: " + id);
            }
        }
    }

    private static String materialName(String materialKey) {
        return switch (materialKey) {
            case "vanilla:diamond" -> "Diamant";
            case "reinforced_steel_block" -> "Verstärkte Stahlblöcke";
            case "darksteel_block" -> "Dunkelstahlblöcke";
            default -> Character.toUpperCase(materialKey.charAt(0)) + materialKey.substring(1);
        };
    }

    private static RecipeChoice choiceFor(String materialKey) {
        if (materialKey.startsWith("vanilla:")) {
            return new RecipeChoice.MaterialChoice(Material.matchMaterial(materialKey.substring(8)));
        }

        return RecipeChoice.exactChoice(List.of(CustomItemService.createItemStack(materialKey)));
    }

    private static ShapedRecipe baseArmorRecipe(JavaPlugin plugin, String resultId, String slot, String materialKey) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, resultId),
                CustomItemService.createItemStack(resultId));
        String[] rows = switch (slot) {
            case "helmet" -> new String[]{"MMM", "M M"};
            case "chestplate" -> new String[]{"M M", "MMM", "MMM"};
            case "leggings" -> new String[]{"MMM", "M M", "M M"};
            default -> new String[]{"M M", "M M"};
        };
        recipe.shape(rows);
        recipe.setIngredient('M', choiceFor(materialKey));
        return recipe;
    }

    private static ShapedRecipe surroundRecipe(JavaPlugin plugin, String resultId, String materialKey,
                                               String centerItemId) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, resultId),
                CustomItemService.createItemStack(resultId));
        recipe.shape("MMM", "MCM", "MMM");
        recipe.setIngredient('M', choiceFor(materialKey));
        recipe.setIngredient('C', RecipeChoice.exactChoice(CustomItemService.createItemStack(centerItemId)));
        return recipe;
    }

    private static Material[] chainmail() {
        return new Material[]{Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS};
    }

    private static Material[] diamond() {
        return new Material[]{Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS};
    }

    private static Material[] iron() {
        return new Material[]{Material.IRON_HELMET, Material.IRON_CHESTPLATE,
                Material.IRON_LEGGINGS, Material.IRON_BOOTS};
    }

    private static Material[] netherite() {
        return new Material[]{Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};
    }
}
