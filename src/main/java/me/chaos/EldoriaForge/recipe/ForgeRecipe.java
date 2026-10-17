package me.chaos.EldoriaForge.recipe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** A forge recipe: takes a list of materials and produces a new item after a set time. */
public final class ForgeRecipe {
    private final String id;
    private final String name;
    private final int timeSeconds;
    private final List<ForgeIngredient> ingredients;
    private final String outputItem;   // vanilla material name or "nexo:<id>"
    private final int outputAmount;

    public ForgeRecipe(String id, String name, int timeSeconds, List<ForgeIngredient> ingredients,
                       String outputItem, int outputAmount) {
        this.id = id;
        this.name = name;
        this.timeSeconds = timeSeconds;
        this.ingredients = List.copyOf(ingredients);
        this.outputItem = outputItem;
        this.outputAmount = outputAmount;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    /** Forge duration in seconds. */
    public int timeSeconds() {
        return timeSeconds;
    }

    public List<ForgeIngredient> ingredients() {
        return ingredients;
    }

    public String outputItem() {
        return outputItem;
    }

    public int outputAmount() {
        return outputAmount;
    }

    /** The produced item as a fresh stack. */
    public ItemStack outputItemStack() {
        ItemStack item = ForgeItems.resolve(outputItem);
        item.setAmount(outputAmount);
        return item;
    }

    /** Ingredients the player does not carry in sufficient quantity. */
    public List<ForgeIngredient> missing(Player player) {
        List<ForgeIngredient> missing = new ArrayList<>();
        for (ForgeIngredient ingredient : ingredients) {
            if (ingredient.countIn(player.getInventory()) < ingredient.amount()) {
                missing.add(ingredient);
            }
        }
        return missing;
    }

    /** Consumes all ingredients. Returns false when materials are missing (nothing is taken). */
    public boolean consumeFrom(Player player) {
        if (!missing(player).isEmpty()) {
            return false;
        }
        for (ForgeIngredient ingredient : ingredients) {
            ingredient.removeFrom(player.getInventory());
        }
        return true;
    }
}
