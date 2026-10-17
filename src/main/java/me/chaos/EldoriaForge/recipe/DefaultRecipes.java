package me.chaos.EldoriaForge.recipe;

import java.util.List;

/**
 * Example forge recipes. Add new recipes here.
 *
 * Item templates:
 *   - vanilla: "IRON_INGOT", "COAL", ...
 *   - Nexo:    "nexo:<itemid>" (replace the example ids with your real Nexo item ids)
 */
public final class DefaultRecipes {

    private DefaultRecipes() {
    }

    public static void register() {
        ForgeRecipeRegistry.register(new ForgeRecipe(
                "forge_iron_block",
                "Eisenblock schmelzen",
                60, // seconds
                List.of(new ForgeIngredient("IRON_INGOT", 9)),
                "IRON_BLOCK",
                1
        ));

        ForgeRecipeRegistry.register(new ForgeRecipe(
                "forge_steel_ingot",
                "Stahlbarren",
                300, // 5 minutes
                List.of(
                        new ForgeIngredient("IRON_INGOT", 4),
                        new ForgeIngredient("COAL", 2)
                ),
                "nexo:steel_ingot", // TODO: replace with your real Nexo item id
                1
        ));

        ForgeRecipeRegistry.register(new ForgeRecipe(
                "forge_reinforced_alloy",
                "Verstärkte Legierung",
                900, // 15 minutes
                List.of(
                        new ForgeIngredient("IRON_BLOCK", 2),
                        new ForgeIngredient("DIAMOND", 1),
                        new ForgeIngredient("nexo:steel_ingot", 3) // TODO: replace with your real Nexo item id
                ),
                "nexo:reinforced_alloy", // TODO: replace with your real Nexo item id
                1
        ));
    }
}
