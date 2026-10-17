package me.chaos.EldoriaForge.recipe;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ForgeRecipeRegistry {
    private static final Map<String, ForgeRecipe> RECIPES = new LinkedHashMap<>();

    private ForgeRecipeRegistry() {
    }

    public static void register(ForgeRecipe recipe) {
        RECIPES.put(recipe.id(), recipe);
    }

    public static Optional<ForgeRecipe> get(String id) {
        return Optional.ofNullable(RECIPES.get(id));
    }

    public static Collection<ForgeRecipe> all() {
        return RECIPES.values();
    }
}
