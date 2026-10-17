package me.chaos.EldoriaContent.items;

import me.chaos.EldoriaItems.Item.CustomItemBuilder;
import me.chaos.EldoriaItems.Item.CustomItemService;
import me.chaos.EldoriaItems.Item.DefaultItemTypes;
import me.chaos.EldoriaItems.Item.ItemRarity;
import me.chaos.EldoriaItems.Item.Obtain.ObtainMethod;
import me.chaos.EldoriaItems.Item.Stats.DefaultStatTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The five custom pickaxes. Their stats (Abbaugeschwindigkeit / Bergbaugl\u00fcck / Abbaufl\u00e4che)
 * are functional out of the box: CustomItemService mirrors them into the eldoriaBase stat PDC
 * (see StatKeyBridge), and the crafting recipes use ExactChoice on the custom ingots.
 */
public final class ToolItems {

    private ToolItems() {
    }

    public static void register(JavaPlugin plugin) {
        pickaxe(plugin, "bronze_pickaxe", "Bronzespitzhacke", ItemRarity.UNGEWOEHNLICH, "bronze", 25, 0, 5,
                "Bronze + St\u00f6cke");
        pickaxe(plugin, "silver_pickaxe", "Silberspitzhacke", ItemRarity.UNGEWOEHNLICH, "silver", 50, 0, 10,
                "Silber + St\u00f6cke");
        pickaxe(plugin, "steel_pickaxe", "Stahlspitzhacke", ItemRarity.SELTEN, "steel", 100, 50, 0,
                "Stahl + St\u00f6cke");
        pickaxe(plugin, "reinforced_steel_pickaxe", "Verst\u00e4rkte Stahlspitzhacke", ItemRarity.SELTEN,
                "reinforced_steel", 100, 75, 0, "Verst\u00e4rkter Stahl + St\u00f6cke");
        pickaxe(plugin, "darksteel_pickaxe", "Dunkelstahlspitzhacke", ItemRarity.EPISCH, "darksteel", 150, 100, 0,
                "Dunkelstahl + St\u00f6cke");
    }

    private static void pickaxe(JavaPlugin plugin, String id, String name, ItemRarity rarity,
                                String ingotId, double speed, double fortune, double spread, String obtainDetail) {
        CustomItemBuilder builder = new CustomItemBuilder(id, Material.IRON_PICKAXE)
                .displayName(name)
                .rarity(rarity)
                .type(DefaultItemTypes.PICKAXE)
                .obtain(ObtainMethod.CRAFTING, obtainDetail);
        if (speed > 0) builder.stat(DefaultStatTypes.MINING_SPEED, speed);
        if (fortune > 0) builder.stat(DefaultStatTypes.MINING_FORTUNE, fortune);
        if (spread > 0) builder.stat(DefaultStatTypes.MINING_SPREAD, spread);
        builder.unbreakable(true);
        builder.build();

        ItemStack result = CustomItemService.createItemStack(id);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, id), result);
        recipe.shape("MMM", " S ", " S ");
        recipe.setIngredient('M', RecipeChoice.exactChoice(CustomItemService.createItemStack(ingotId)));
        recipe.setIngredient('S', Material.STICK);
        try {
            plugin.getServer().addRecipe(recipe);
        } catch (IllegalStateException e) {
            plugin.getLogger().warning("Rezept bereits registriert: " + id);
        }
    }
}
