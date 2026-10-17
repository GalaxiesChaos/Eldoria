package me.chaos.EldoriaContent.forge;

import me.chaos.EldoriaForge.recipe.ForgeIngredient;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.recipe.ForgeRecipeRegistry;

import java.util.List;

/**
 * Every forge recipe of the mining content. Item templates understood by the forge:
 * "VANILLA_MATERIAL", "nexo:id", "eldoria:<custom item>", "drill:<upgrade id>",
 * "drillbase:<base id>" (the last three resolved via ForgeItems).
 * <p>
 * Times the design doc did not specify are chosen defaults and grouped as constants below
 * so they can be tuned in one place.
 */
public final class MiningForgeRecipes {

    // Doc-specified
    private static final int STEEL_TIME = 900;              // 15 min (per spec)
    // Chosen defaults for unspecified recipes
    private static final int ENCHANTED_TIME = 30;           // 90 -> 1 enchanted
    private static final int ENCHANTED_BLOCK_TIME = 60;     // 90 enchanted -> 1 enchanted block
    private static final int SHORT_TIME = 300;              // 5 min
    private static final int MEDIUM_TIME = 600;             // 10 min
    private static final int LONG_TIME = 1800;              // 30 min
    private static final int VERY_LONG_TIME = 3600;         // 1 h
    private static final int EXTREME_TIME = 7200;           // 2 h

    private MiningForgeRecipes() {
    }

    public static void register() {
        materials();
        enchantedCompression();
        fuelsAndComponents();
        drillParts();
    }

    // ---------------------------------------------------------------- base materials
    private static void materials() {
        recipe("steel", "Stahl", STEEL_TIME,
                List.of(vanilla("IRON_INGOT", 20), vanilla("COAL", 40)),
                "eldoria:steel", 10);

        recipe("reinforced_steel", "Verst\u00e4rkter Stahl", LONG_TIME,
                List.of(eldoria("steel", 1), eldoria("enchanted_iron", 10), eldoria("enchanted_coal", 40)),
                "eldoria:reinforced_steel", 1);

        recipe("darksteel", "Dunkelstahl", LONG_TIME,
                List.of(eldoria("reinforced_steel", 5), vanilla("WITHER_SKELETON_SKULL", 3),
                        vanilla("NETHERITE_INGOT", 1)),
                "eldoria:darksteel", 1);
    }

    // ---------------------------------------------------------------- enchanted compression (90:1)
    private static void enchantedCompression() {
        compression("enc_iron", "Verzaubertes Eisen", "IRON_INGOT", "enchanted_iron");
        compression("enc_coal", "Verzauberte Kohle", "COAL", "enchanted_coal");
        compression("enc_diamond", "Verzauberter Diamant", "DIAMOND", "enchanted_diamond");
        compression("enc_redstone", "Verzauberter Redstone", "REDSTONE", "enchanted_redstone");
        compression("enc_copper", "Verzaubertes Kupfer", "COPPER_INGOT", "enchanted_copper");
        compression("enc_blaze_rod", "Verzauberte Lohenrute", "BLAZE_ROD", "enchanted_blaze_rod");
        compression("enc_magma_cream", "Verzauberte Magmacreme", "MAGMA_CREAM", "enchanted_magma_cream");
        compression("enc_bronze", "Verzauberte Bronze", "eldoria:bronze", "enchanted_bronze");
        compression("enc_silver", "Verzaubertes Silber", "eldoria:silver", "enchanted_silver");
        compression("enc_steel", "Verzauberter Stahl", "eldoria:steel", "enchanted_steel");
        compression("enc_reinforced_steel", "Verz. Verst\u00e4rkter Stahl", "eldoria:reinforced_steel",
                "enchanted_reinforced_steel");
        compression("enc_titanium", "Verzaubertes Titan", "eldoria:titanium", "enchanted_titanium");
        compression("enc_darksteel", "Verzauberter Dunkelstahl", "eldoria:darksteel", "enchanted_darksteel");

        blockCompression("enc_iron_block", "Verzauberter Eisenblock", "enchanted_iron", "enchanted_iron_block");
        blockCompression("enc_coal_block", "Verzauberter Kohleblock", "enchanted_coal", "enchanted_coal_block");
        blockCompression("enc_diamond_block", "Verzauberter Diamantblock", "enchanted_diamond", "enchanted_diamond_block");
        blockCompression("enc_redstone_block", "Verzauberter Redstoneblock", "enchanted_redstone",
                "enchanted_redstone_block");
    }

    private static void compression(String id, String name, String inputTemplate, String outputId) {
        recipe(id, name, ENCHANTED_TIME, List.of(new ForgeIngredient(inputTemplate, 90)), "eldoria:" + outputId, 1);
    }

    private static void blockCompression(String id, String name, String inputTemplate, String outputId) {
        recipe(id, name, ENCHANTED_BLOCK_TIME, List.of(new ForgeIngredient(inputTemplate, 90)), "eldoria:" + outputId, 1);
    }

    // ---------------------------------------------------------------- fuels + components
    private static void fuelsAndComponents() {
        recipe("oil_barrel", "\u00d6lfass", MEDIUM_TIME,
                List.of(eldoria("enchanted_coal", 25), eldoria("enchanted_blaze_rod", 10)),
                "eldoria:oil_barrel", 1);

        recipe("blaze_fuel", "Lohenkraftstoff", STEEL_TIME,
                List.of(eldoria("enchanted_blaze_rod", 50), eldoria("enchanted_magma_cream", 25)),
                "eldoria:blaze_fuel", 1);

        recipe("fuel_tank", "Treibstofftank", SHORT_TIME,
                List.of(eldoria("enchanted_coal_block", 2)),
                "eldoria:fuel_tank", 1);

        recipe("drill_engine", "Bohrermotor", SHORT_TIME,
                List.of(eldoria("enchanted_redstone", 5), eldoria("enchanted_copper", 3), eldoria("steel", 1)),
                "eldoria:drill_engine", 1);
    }

    // ---------------------------------------------------------------- drill parts
    private static void drillParts() {
        // fuel tanks
        recipe("iron_fuel_tank", "Eisen-Treibstofftank", SHORT_TIME,
                List.of(eldoria("fuel_tank", 1), eldoria("enchanted_iron_block", 1)),
                "drill:iron_fuel_tank", 1);
        recipe("steel_plated_fuel_tank", "Stahl-Treibstofftank", MEDIUM_TIME,
                List.of(eldoria("fuel_tank", 4), eldoria("enchanted_steel", 5)),
                "drill:steel_plated_fuel_tank", 1);
        recipe("reinforced_steel_plated_fuel_tank", "Verst. Stahl-Treibstofftank", STEEL_TIME,
                List.of(eldoria("fuel_tank", 8), eldoria("enchanted_reinforced_steel", 5)),
                "drill:reinforced_steel_plated_fuel_tank", 1);
        recipe("titanium_fuel_tank", "Titan-Treibstofftank", LONG_TIME,
                List.of(eldoria("fuel_tank", 10), eldoria("enchanted_titanium", 3)),
                "drill:titanium_fuel_tank", 1);

        // bases
        recipe("diamond_drill_base", "Diamant-Bohrerbasis", LONG_TIME,
                List.of(eldoria("fuel_tank", 2), eldoria("enchanted_diamond_block", 1),
                        eldoria("enchanted_iron", 5), eldoria("enchanted_redstone_block", 1)),
                "drillbase:diamond_drill_base", 1);
        recipe("steel_drill_base", "Stahl-Bohrerbasis", VERY_LONG_TIME,
                List.of(eldoria("fuel_tank", 2), eldoria("enchanted_iron", 5),
                        eldoria("enchanted_redstone_block", 1), eldoria("enchanted_steel", 15)),
                "drillbase:steel_drill_base", 1);
        recipe("reinforced_steel_drill_base", "Verst. Stahl-Bohrerbasis", EXTREME_TIME,
                List.of(ingredient("drillbase:steel_drill_base", 1), eldoria("enchanted_redstone", 2),
                        eldoria("enchanted_reinforced_steel", 25)),
                "drillbase:reinforced_steel_drill_base", 1);
        recipe("darksteel_drill_base", "Dunkelstahl-Bohrerbasis", EXTREME_TIME,
                List.of(ingredient("drillbase:reinforced_steel_drill_base", 1),
                        eldoria("enchanted_diamond_block", 2), eldoria("darksteel", 50)),
                "drillbase:darksteel_drill_base", 1);

        // heads
        recipe("iron_drill_head", "Eisen-Drillkopf", MEDIUM_TIME,
                List.of(eldoria("enchanted_iron", 5), eldoria("enchanted_copper", 3), eldoria("enchanted_bronze", 2)),
                "drill:iron_drill_head", 1);
        recipe("reinforced_iron_drill_head", "Verst. Eisen-Drillkopf", STEEL_TIME,
                List.of(ingredient("drill:iron_drill_head", 1), eldoria("enchanted_silver", 3),
                        eldoria("enchanted_iron", 10)),
                "drill:reinforced_iron_drill_head", 1);
        recipe("diamond_drill_head", "Diamant-Drillkopf", STEEL_TIME,
                List.of(eldoria("enchanted_diamond", 5), eldoria("enchanted_iron", 15), eldoria("enchanted_redstone", 1)),
                "drill:diamond_drill_head", 1);
        recipe("reinforced_diamond_drill_head", "Verst. Diamant-Drillkopf", LONG_TIME,
                List.of(ingredient("drill:diamond_drill_head", 1), eldoria("steel", 1),
                        eldoria("enchanted_silver", 15)),
                "drill:reinforced_diamond_drill_head", 1);
        recipe("steel_drill_head", "Stahl-Drillkopf", LONG_TIME,
                List.of(eldoria("steel", 5), eldoria("enchanted_diamond", 15), eldoria("enchanted_iron", 5),
                        eldoria("enchanted_redstone", 1)),
                "drill:steel_drill_head", 1);
        recipe("reinforced_steel_drill_head", "Verst. Stahl-Drillkopf", VERY_LONG_TIME,
                List.of(eldoria("reinforced_steel", 5), eldoria("enchanted_steel", 15),
                        ingredient("drill:steel_drill_head", 1)),
                "drill:reinforced_steel_drill_head", 1);
        recipe("titanium_drill_head", "Titan-Drillkopf", EXTREME_TIME * 2,
                List.of(eldoria("enchanted_titanium", 15), eldoria("enchanted_reinforced_steel", 50),
                        eldoria("enchanted_darksteel", 1), ingredient("drill:reinforced_steel_drill_head", 1)),
                "drill:titanium_drill_head", 1);

        // engines
        recipe("silver_drill_engine", "Silber-Motor", STEEL_TIME,
                List.of(eldoria("drill_engine", 2), eldoria("enchanted_silver", 5), eldoria("enchanted_iron", 2)),
                "drill:silver_drill_engine", 1);
        recipe("diamond_drill_engine", "Diamant-Motor", LONG_TIME,
                List.of(ingredient("drill:silver_drill_engine", 1), eldoria("drill_engine", 2),
                        eldoria("enchanted_diamond", 15), eldoria("enchanted_redstone", 2)),
                "drill:diamond_drill_engine", 1);
        recipe("steel_drill_engine", "Stahl-Motor", VERY_LONG_TIME,
                List.of(ingredient("drill:diamond_drill_engine", 1), eldoria("drill_engine", 5),
                        eldoria("enchanted_steel", 5), eldoria("enchanted_diamond_block", 1),
                        eldoria("enchanted_redstone_block", 5)),
                "drill:steel_drill_engine", 1);
        recipe("reinforced_steel_drill_engine", "Verst. Stahl-Motor", EXTREME_TIME,
                List.of(ingredient("drill:steel_drill_engine", 1), eldoria("enchanted_reinforced_steel", 15),
                        eldoria("enchanted_steel", 50), eldoria("enchanted_diamond_block", 5)),
                "drill:reinforced_steel_drill_engine", 1);
    }

    // ---------------------------------------------------------------- helpers
    private static ForgeIngredient vanilla(String material, int amount) {
        return new ForgeIngredient(material, amount);
    }

    private static ForgeIngredient eldoria(String templateId, int amount) {
        return ingredient("eldoria:" + templateId, amount);
    }

    private static ForgeIngredient ingredient(String template, int amount) {
        return new ForgeIngredient(template, amount);
    }

    private static void recipe(String id, String name, int timeSeconds,
                               List<ForgeIngredient> ingredients, String outputTemplate, int outputAmount) {
        ForgeRecipeRegistry.register(new ForgeRecipe(id, name, timeSeconds, ingredients, outputTemplate, outputAmount));
    }
}
