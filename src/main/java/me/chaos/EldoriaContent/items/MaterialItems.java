package me.chaos.EldoriaContent.items;

import me.chaos.EldoriaItems.Item.CustomItemBuilder;
import me.chaos.EldoriaItems.Item.DefaultItemTypes;
import me.chaos.EldoriaItems.Item.ItemRarity;
import me.chaos.EldoriaItems.Item.Obtain.ObtainMethod;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base + enchanted crafting materials, drill fuels, the fuel tank component and the WIP
 * drill plates. All player-facing text is German. Enchanted materials always follow the
 * 90:1 rule (90 base items -> 1 enchanted item, 90 enchanted -> 1 enchanted block),
 * the actual conversion lives in {@link me.chaos.EldoriaContent.forge.MiningForgeRecipes}.
 */
public final class MaterialItems {

    private MaterialItems() {
    }

    public static void register(JavaPlugin plugin) {
        // ---------------------------------------------------------------- base materials
        new CustomItemBuilder("hardwood", Material.OAK_PLANKS)
                .displayName("Hartholz")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.ORE_REPLACEMENT, "Holzst\u00e4mme abbauen, 10% Chance")
                .build();

        new CustomItemBuilder("hardwood_stick", Material.STICK)
                .displayName("Hartholzst\u00f6cke")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.MATERIAL)
                .obtain(ObtainMethod.CRAFTING, "2 Hartholz -> 4 Hartholzst\u00f6cke")
                .build();

        new CustomItemBuilder("steel", Material.IRON_INGOT)
                .displayName("Stahl")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.FORGE, "20 Eisen + 40 Kohle, 15 Min.")
                .build();

        new CustomItemBuilder("reinforced_steel", Material.NETHERITE_SCRAP)
                .displayName("Verst\u00e4rkter Stahl")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.FORGE, "1 Stahl + 10 Verz. Eisen + 40 Verz. Kohle")
                .build();

        new CustomItemBuilder("bronze", Material.COPPER_INGOT)
                .displayName("Bronze")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.ORE_REPLACEMENT, "Metall-Erz, 10% Chance")
                .build();

        new CustomItemBuilder("silver", Material.IRON_INGOT)
                .displayName("Silber")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.ORE_REPLACEMENT, "Metall-Erz, 5% Chance")
                .build();

        new CustomItemBuilder("darksteel", Material.NETHERITE_INGOT)
                .displayName("Dunkelstahl")
                .rarity(ItemRarity.EPISCH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.FORGE, "5 Verst. Stahl + 3 Wither-Skelettsch\u00e4del + 1 Netherite")
                .build();

        new CustomItemBuilder("titanium", Material.IRON_INGOT)
                .displayName("Titan")
                .rarity(ItemRarity.EPISCH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.ORE_REPLACEMENT, "Metall-Erz, 0,1% Chance")
                .build();

        // storage blocks for the armor upgrade recipes ("surrounded by X blocks")
        new CustomItemBuilder("reinforced_steel_block", Material.IRON_BLOCK)
                .displayName("Verst\u00e4rkter Stahlblock")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.MATERIAL)
                .obtain(ObtainMethod.CRAFTING, "9 Verst\u00e4rkter Stahl")
                .build();

        new CustomItemBuilder("darksteel_block", Material.NETHERITE_BLOCK)
                .displayName("Dunkelstahlblock")
                .rarity(ItemRarity.EPISCH)
                .type(DefaultItemTypes.MATERIAL)
                .obtain(ObtainMethod.CRAFTING, "9 Dunkelstahl")
                .build();

        // ---------------------------------------------------------------- enchanted materials (90:1)
        enchanted("enchanted_iron", "Verzaubertes Eisen", Material.IRON_INGOT, "Eisen");
        enchanted("enchanted_coal", "Verzauberte Kohle", Material.COAL, "Kohle");
        enchanted("enchanted_diamond", "Verzauberter Diamant", Material.DIAMOND, "Diamant");
        enchanted("enchanted_redstone", "Verzauberter Redstone", Material.REDSTONE, "Redstone");
        enchanted("enchanted_copper", "Verzaubertes Kupfer", Material.COPPER_INGOT, "Kupfer");
        enchanted("enchanted_blaze_rod", "Verzauberte Lohenrute", Material.BLAZE_ROD, "Lohenruten");
        enchanted("enchanted_magma_cream", "Verzauberte Magmacreme", Material.MAGMA_CREAM, "Magmacreme");
        enchanted("enchanted_bronze", "Verzauberte Bronze", Material.COPPER_INGOT, "Bronze");
        enchanted("enchanted_silver", "Verzaubertes Silber", Material.IRON_INGOT, "Silber");
        enchanted("enchanted_steel", "Verzauberter Stahl", Material.IRON_INGOT, "Stahl");
        enchanted("enchanted_reinforced_steel", "Verzauberter Verst\u00e4rkter Stahl", Material.NETHERITE_SCRAP,
                "Verst\u00e4rkter Stahl");
        enchanted("enchanted_titanium", "Verzaubertes Titan", Material.IRON_INGOT, "Titan");
        enchanted("enchanted_darksteel", "Verzauberter Dunkelstahl", Material.NETHERITE_INGOT, "Dunkelstahl");
        enchantedBlock("enchanted_iron_block", "Verzauberter Eisenblock", Material.IRON_BLOCK, "Verzaubertes Eisen");
        enchantedBlock("enchanted_coal_block", "Verzauberter Kohleblock", Material.COAL_BLOCK, "Verzauberte Kohle");
        enchantedBlock("enchanted_diamond_block", "Verzauberter Diamantblock", Material.DIAMOND_BLOCK,
                "Verzauberter Diamant");
        enchantedBlock("enchanted_redstone_block", "Verzauberter Redstoneblock", Material.REDSTONE_BLOCK,
                "Verzauberter Redstone");

        // ---------------------------------------------------------------- drill fuels (items)
        new CustomItemBuilder("bio_fuel", Material.SLIME_BALL)
                .displayName("Biokraftstoff")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.FUEL)
                .obtain(ObtainMethod.SHOP, "NPC-H\u00e4ndler")
                .build();

        new CustomItemBuilder("oil_barrel", Material.BARREL)
                .displayName("\u00d6lfass")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.FUEL)
                .obtain(ObtainMethod.FORGE, "25 Verz. Kohle + 10 Verz. Lohenruten")
                .build();

        new CustomItemBuilder("blaze_fuel", Material.BLAZE_POWDER)
                .displayName("Lohenkraftstoff")
                .rarity(ItemRarity.SELTEN)
                .type(DefaultItemTypes.FUEL)
                .obtain(ObtainMethod.FORGE, "50 Verz. Lohenruten + 25 Verz. Magmacreme")
                .build();

        // ---------------------------------------------------------------- drill components
        new CustomItemBuilder("fuel_tank", Material.CAULDRON)
                .displayName("Treibstofftank")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.DRILL_PART)
                .obtain(ObtainMethod.FORGE, "2 Verzauberte Kohlebl\u00f6cke")
                .build();

        new CustomItemBuilder("drill_engine", Material.PISTON)
                .displayName("Bohrermotor")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.DRILL_PART)
                .obtain(ObtainMethod.FORGE, "5 Verz. Redstone + 3 Verz. Kupfer + 1 Stahl")
                .build();

        // ---------------------------------------------------------------- WIP drill plates
        new CustomItemBuilder("drill_plate_wip", Material.PAPER)
                .displayName("Bohrerplatte [WIP]")
                .rarity(ItemRarity.GEWOEHNLICH)
                .type(DefaultItemTypes.DRILL_PART)
                .obtain(ObtainMethod.CRAFTING, "IN ARBEIT - Funktion folgt in einem zuk\u00fcnftigen Update")
                .build();

        // ---------------------------------------------------------------- simple crafting recipes
        safeRecipe(plugin, sticksRecipe(plugin));
        safeRecipe(plugin, blockRecipe(plugin, "reinforced_steel_block", "reinforced_steel"));
        safeRecipe(plugin, blockRecipe(plugin, "darksteel_block", "darksteel"));
    }

    private static void enchanted(String id, String name, Material material, String baseName) {
        new CustomItemBuilder(id, material)
                .displayName(name)
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.FORGE, "90x " + baseName + " (Schmiede)")
                .build();
    }

    private static void enchantedBlock(String id, String name, Material material, String baseName) {
        new CustomItemBuilder(id, material)
                .displayName(name)
                .rarity(ItemRarity.SELTEN)
                .type(DefaultItemTypes.MATERIAL)
                .obtain(ObtainMethod.FORGE, "90x " + baseName + " (Schmiede)")
                .build();
    }

    private static ShapelessRecipe sticksRecipe(JavaPlugin plugin) {
        ItemStack result = me.chaos.EldoriaItems.Item.CustomItemService.createItemStack("hardwood_stick");
        result.setAmount(4);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(plugin, "hardwood_sticks"), result);
        recipe.addIngredient(2, new RecipeChoice.ExactChoice(
                me.chaos.EldoriaItems.Item.CustomItemService.createItemStack("hardwood")));
        return recipe;
    }

    private static ShapedRecipe blockRecipe(JavaPlugin plugin, String resultId, String ingredientId) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, resultId),
                me.chaos.EldoriaItems.Item.CustomItemService.createItemStack(resultId));
        recipe.shape("MMM", "MMM", "MMM");
        recipe.setIngredient('M', new RecipeChoice.ExactChoice(
                me.chaos.EldoriaItems.Item.CustomItemService.createItemStack(ingredientId)));
        return recipe;
    }

    private static void safeRecipe(JavaPlugin plugin, org.bukkit.inventory.Recipe recipe) {
        try {
            plugin.getServer().addRecipe(recipe);
        } catch (IllegalStateException e) {
            plugin.getLogger().warning("Rezept bereits registriert: " + e.getMessage());
        }
    }
}
