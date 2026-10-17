package me.chaos.EldoriaItems.Examples;

import me.chaos.EldoriaItems.Item.CustomItemBuilder;
import me.chaos.EldoriaItems.Item.CustomItemTemplate;
import me.chaos.EldoriaItems.Item.DefaultItemTypes;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantment;
import me.chaos.EldoriaItems.Item.Enchant.CustomEnchantmentRegistry;
import me.chaos.EldoriaItems.Item.Enchant.SimpleCustomEnchantment;
import me.chaos.EldoriaItems.Item.ItemRarity;
import me.chaos.EldoriaItems.Item.Obtain.ObtainMethod;
import me.chaos.EldoriaItems.Item.Obtain.OreReplacementEntry;
import me.chaos.EldoriaItems.Item.Obtain.OreReplacementRegistry;
import me.chaos.EldoriaItems.Item.Stats.DefaultStatTypes;
import me.chaos.EldoriaItems.Sets.ItemSet;
import me.chaos.EldoriaItems.Sets.ItemSetRegistry;
import me.chaos.EldoriaItems.Sets.SetBonus;
import me.chaos.EldoriaItems.Sets.StatSetBonusEffect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

/**
 * Example registrations showing off every feature of the custom item system: a pure crafting
 * material with an ore-replacement obtain method, a custom tool type with stats + an enchant,
 * a boss-drop weapon and a mob-drop weapon that share a 2-piece set bonus.
 * <p>
 * Not required for the system to work - call {@link #load()} from EldoriaItemsModule (or your
 * own item-loader class, mirroring how QuestLoader works) and delete once you have real items.
 */
public class ExampleItemsLoader {

    public static void load() {
        // A pure crafting material - no stats, custom "Erz" type, obtained via ore replacement
        CustomItemTemplate steel = new CustomItemBuilder("steel_ingot", Material.IRON_INGOT)
                .displayName("Stahlbarren")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.ERZ)
                .obtain(ObtainMethod.ORE_REPLACEMENT, "Eisenerz, 2% Chance")
                .build();

        OreReplacementRegistry.register(new OreReplacementEntry(Material.IRON_ORE, steel.getId(), 2.0, false));

        // A demo custom enchant (swap for your real custom-enchant system's implementation)
        CustomEnchantment effizienzPlus = CustomEnchantmentRegistry.register(
                new SimpleCustomEnchantment("effizienz_plus", "Effizienz+", 5, NamedTextColor.AQUA));

        // A custom tool type ("Bohrer"/Drill) with stats + an enchant, obtained via crafting
        new CustomItemBuilder("steel_drill", Material.IRON_PICKAXE)
                .displayName("Stahlbohrer")
                .rarity(ItemRarity.SELTEN)
                .type(DefaultItemTypes.DRILL)
                .stat(DefaultStatTypes.MINING_SPEED, 15)
                .stat(DefaultStatTypes.DROPCHANCE, 10)
                .enchant(effizienzPlus, 3)
                .obtain(ObtainMethod.CRAFTING, "Amboss: 4x Stahlbarren, 1x Diamant-Spitzhacke")
                .build();

        // A boss-drop weapon, part of a set
        new CustomItemBuilder("shadow_katana", Material.NETHERITE_SWORD)
                .displayName("Schattenkatana")
                .rarity(ItemRarity.EPISCH)
                .type(DefaultItemTypes.KATANA)
                .stat(DefaultStatTypes.DAMAGE, 45)
                .stat(DefaultStatTypes.STRENGTH, 20)
                .obtain(ObtainMethod.BOSS_DROP, "Schattenwächter")
                .partOfSet("shadow_set")
                .build();

        // A mob-drop weapon, part of the same set
        new CustomItemBuilder("wolf_fang_shortbow", Material.BOW)
                .displayName("Wolfsfang")
                .rarity(ItemRarity.UNGEWOEHNLICH)
                .type(DefaultItemTypes.SHORTBOW)
                .stat(DefaultStatTypes.DAMAGE, 8)
                .obtain(ObtainMethod.MOB_DROP, "Wolf, 5% Chance")
                .partOfSet("shadow_set")
                .build();

        // A 2-piece set bonus tying the katana and shortbow together
        ItemSetRegistry.register(new ItemSet(
                "shadow_set",
                "Schattenset",
                List.of("shadow_katana", "wolf_fang_shortbow"),
                List.of(new SetBonus(
                        2,
                        List.of(Component.text("+10 Stärke, +5% Dropchance").color(NamedTextColor.LIGHT_PURPLE)),
                        List.of(new StatSetBonusEffect(Map.of(
                                DefaultStatTypes.STRENGTH, 10.0,
                                DefaultStatTypes.DROPCHANCE, 5.0
                        )))
                ))
        ));
    }
}
