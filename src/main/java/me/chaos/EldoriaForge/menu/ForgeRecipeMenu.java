package me.chaos.EldoriaForge.menu;

import me.chaos.EldoriaAntiDupe.ProtectedGui;
import me.chaos.EldoriaForge.recipe.ForgeIngredient;
import me.chaos.EldoriaForge.recipe.ForgeItems;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.recipe.ForgeRecipeRegistry;
import me.chaos.EldoriaForge.util.Text;
import me.chaos.eldoriaBase.Utils.GuiBuilder.GuiInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Lists all available forge recipes; clicking one starts it in the originating station. */
public final class ForgeRecipeMenu implements ProtectedGui {
    public static final int SIZE = 54;
    public static final int BACK_SLOT = 49;
    private static final List<Integer> RECIPE_SLOTS = new ArrayList<>();

    static {
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 7; col++) {
                RECIPE_SLOTS.add(row * 9 + col);
            }
        }
    }

    private final int originSlot;
    private final Map<Integer, ForgeRecipe> slotRecipes = new HashMap<>();
    private final Inventory inventory;

    private ForgeRecipeMenu(int originSlot) {
        this.originSlot = originSlot;
        this.inventory = Bukkit.createInventory(this, SIZE, Component.text("Schmiederezepte", NamedTextColor.DARK_GRAY));
    }

    public static boolean open(Player player, int originSlot) {
        ForgeRecipeMenu menu = new ForgeRecipeMenu(originSlot);
        menu.render();
        player.openInventory(menu.inventory);
        return true;
    }

    public int originSlot() {
        return originSlot;
    }

    public ForgeRecipe recipe(int slot) {
        return slotRecipes.get(slot);
    }

    private void render() {
        GuiInterface.fillInv(inventory);
        List<ForgeRecipe> recipes = List.copyOf(ForgeRecipeRegistry.all());
        for (int i = 0; i < Math.min(recipes.size(), RECIPE_SLOTS.size()); i++) {
            ForgeRecipe recipe = recipes.get(i);
            slotRecipes.put(RECIPE_SLOTS.get(i), recipe);
            inventory.setItem(RECIPE_SLOTS.get(i), recipeIcon(recipe));
        }

        ItemStack back = ItemStack.of(Material.ARROW);
        ItemMeta meta = back.getItemMeta();
        meta.displayName(Text.plain("Zurück", NamedTextColor.WHITE));
        back.setItemMeta(meta);
        inventory.setItem(BACK_SLOT, back);
    }

    private ItemStack recipeIcon(ForgeRecipe recipe) {
        ItemStack item = recipe.outputItemStack();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Text.gray("Benötigt:"));
        for (ForgeIngredient ingredient : recipe.ingredients()) {
            lore.add(Text.plain("  " + ingredient.amount() + "x ", NamedTextColor.WHITE)
                    .append(ForgeItems.displayName(ingredient.item()).color(NamedTextColor.GRAY)));
        }
        lore.add(Text.plain("Dauer: " + Text.time(recipe.timeSeconds()), NamedTextColor.YELLOW));
        lore.add(Text.plain("Klicke zum Schmieden", NamedTextColor.GREEN));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
