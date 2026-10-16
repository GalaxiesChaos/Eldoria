package net.eldoria.drill.content;

import net.eldoria.drill.base.DrillBase;
import net.eldoria.drill.base.DrillBaseRegistry;
import net.eldoria.drill.effect.DropBoostEffect;
import net.eldoria.drill.effect.LootOddsBoostEffect;
import net.eldoria.drill.fuel.FuelType;
import net.eldoria.drill.fuel.FuelTypeRegistry;
import net.eldoria.drill.mining.DrillLevels;
import net.eldoria.drill.stat.DrillStatRegistry;
import net.eldoria.drill.upgrade.DrillUpgrade;
import net.eldoria.drill.upgrade.DrillUpgradeRegistry;
import net.eldoria.drill.upgrade.UpgradeSlotType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Example content so the system is testable out of the box.
 * Replace/extend with your real bases, upgrades, modules and fuels
 * (call DrillModule.init(plugin, false) to skip this class).
 */
public final class DefaultContent {

    private DefaultContent() {
    }

    public static void register() {
        registerFuels();
        registerBases();
        registerHeads();
        registerParts();
        registerModules();
    }

    private static void registerFuels() {
        FuelTypeRegistry.register(FuelType.ofMaterial("coal", "Kohle", Material.COAL, 25));
        FuelTypeRegistry.register(FuelType.ofMaterial("charcoal", "Holzkohle", Material.CHARCOAL, 20));
        FuelTypeRegistry.register(FuelType.ofMaterial("coal_block", "Kohleblock", Material.COAL_BLOCK, 225));
        FuelTypeRegistry.register(FuelType.ofMaterial("blaze_rod", "Lohenrute", Material.BLAZE_ROD, 80));
    }

    private static void registerBases() {
        // Cheap base: small tank, solid fuels only, one module slot.
        DrillBaseRegistry.register(DrillBase.builder("basic_drill", "Einfacher Bohrer")
                .stat(DrillStatRegistry.MAX_FUEL, 100)
                .lockModule(1).lockModule(2)
                .acceptFuels("coal", "charcoal", "coal_block")
                .build());
        // Better base: bigger tank, any fuel, two module slots.
        DrillBaseRegistry.register(DrillBase.builder("reinforced_drill", "Verstärkter Bohrer")
                .stat(DrillStatRegistry.MAX_FUEL, 250)
                .stat(DrillStatRegistry.FUEL_EFFICIENCY, 5)
                .lockModule(2)
                .build());
    }

    private static void registerHeads() {
        head("wood_head", "Hölzerner Drillkopf", Material.WOODEN_PICKAXE, DrillLevels.WOOD);
        head("stone_head", "Steinerner Drillkopf", Material.STONE_PICKAXE, DrillLevels.STONE);
        head("iron_head", "Eiserner Drillkopf", Material.IRON_PICKAXE, DrillLevels.IRON);
        head("diamond_head", "Diamant-Drillkopf", Material.DIAMOND_PICKAXE, DrillLevels.DIAMOND);
        head("netherite_head", "Netherit-Drillkopf", Material.NETHERITE_PICKAXE, DrillLevels.NETHERITE);
    }

    private static void head(String id, String name, Material icon, int level) {
        DrillUpgradeRegistry.register(DrillUpgrade.builder(id, name, UpgradeSlotType.HEAD)
                .icon(icon).miningLevel(level).build());
    }

    private static void registerParts() {
        DrillUpgradeRegistry.register(DrillUpgrade.builder("basic_engine", "Einfacher Motor", UpgradeSlotType.ENGINE)
                .icon(Material.FURNACE).stat(DrillStatRegistry.MINING_SPEED, 1).build());
        DrillUpgradeRegistry.register(DrillUpgrade.builder("turbo_engine", "Turbomotor", UpgradeSlotType.ENGINE)
                .icon(Material.BLAST_FURNACE).stat(DrillStatRegistry.MINING_SPEED, 2)
                .stat(DrillStatRegistry.FUEL_EFFICIENCY, -10).build());

        DrillUpgradeRegistry.register(DrillUpgrade.builder("small_tank", "Kleiner Extra-Tank", UpgradeSlotType.TANK)
                .icon(Material.BARREL).stat(DrillStatRegistry.MAX_FUEL, 100).build());
        DrillUpgradeRegistry.register(DrillUpgrade.builder("large_tank", "Großer Extra-Tank", UpgradeSlotType.TANK)
                .icon(Material.CAULDRON).stat(DrillStatRegistry.MAX_FUEL, 300).build());

        DrillUpgradeRegistry.register(DrillUpgrade.builder("iron_plates", "Eisenplatten", UpgradeSlotType.PLATES)
                .icon(Material.IRON_INGOT).stat(DrillStatRegistry.REINFORCEMENT, 5).build());
        DrillUpgradeRegistry.register(DrillUpgrade.builder("alloy_plates", "Legierungsplatten", UpgradeSlotType.PLATES)
                .icon(Material.NETHERITE_INGOT).stat(DrillStatRegistry.REINFORCEMENT, 10)
                .stat(DrillStatRegistry.FUEL_EFFICIENCY, 5).build());

        DrillUpgradeRegistry.register(DrillUpgrade.builder("efficient_injector", "Effizienter Injektor", UpgradeSlotType.MODIFIER)
                .icon(Material.REDSTONE).stat(DrillStatRegistry.FUEL_EFFICIENCY, 10).build());
        DrillUpgradeRegistry.register(DrillUpgrade.builder("lucky_bit", "Glücksmeißel", UpgradeSlotType.MODIFIER)
                .icon(Material.GLOWSTONE_DUST).stat(DrillStatRegistry.DROP_CHANCE, 5).build());
    }

    private static void registerModules() {
        DrillUpgradeRegistry.register(DrillUpgrade.builder("fuel_saver_module", "Spritsparmodul", UpgradeSlotType.MODULE)
                .icon(Material.AMETHYST_SHARD).stat(DrillStatRegistry.FUEL_EFFICIENCY, 10).build());
        // Needs BlockTags.setResolver(...) from the custom drop system to trigger.
        DrillUpgradeRegistry.register(DrillUpgrade.builder("gem_finder_module", "Edelsteinfinder-Modul", UpgradeSlotType.MODULE)
                .icon(Material.ECHO_SHARD)
                .effect(new DropBoostEffect("stone", new ItemStack(Material.DIAMOND), 0.01))
                .build());
        DrillUpgradeRegistry.register(DrillUpgrade.builder("loot_magnet_module", "Beutemagnet-Modul", UpgradeSlotType.MODULE)
                .icon(Material.HEART_OF_THE_SEA)
                .effect(new LootOddsBoostEffect("gem", 1.25))
                .build());
    }
}
