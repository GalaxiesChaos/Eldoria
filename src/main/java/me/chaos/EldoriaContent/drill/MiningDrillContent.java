package me.chaos.EldoriaContent.drill;

import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.base.DrillBaseRegistry;
import me.chaos.EldoriaDrill.effect.LootOddsBoostEffect;
import me.chaos.EldoriaDrill.fuel.FuelType;
import me.chaos.EldoriaDrill.fuel.FuelTypeRegistry;
import me.chaos.EldoriaDrill.mining.DrillLevels;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import me.chaos.EldoriaDrill.upgrade.DrillUpgrade;
import me.chaos.EldoriaDrill.upgrade.DrillUpgradeRegistry;
import me.chaos.EldoriaDrill.upgrade.UpgradeSlotType;
import me.chaos.EldoriaItems.Item.CustomItemService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Real drill content: the four bases, seven drill heads, four engines, four fuel tanks and the
 * two drill modules from the design doc, plus the three fuel items (Biokraftstoff / \u00d6lfass /
 * Lohenkraftstoff) as usable FuelTypes and the {@link DrillDropEnhancements} listener.
 * <p>
 * NOTE on MINING_SPEED values: the spec numbers (50-500) are aggregate "Abbaugeschwindigkeit"
 * points; MiningSpeedTask converts them to Haste I-V (50 points ~ Haste I, capped at V).
 * The "% Mining Speed" entries on drill heads are added as flat points as well (10% -> +10).
 * Drill plates stay WIP items (see MaterialItems) and are NOT registered as upgrades yet.
 */
public final class MiningDrillContent {

    private MiningDrillContent() {
    }

    public static void register(JavaPlugin plugin) {
        registerFuels();
        registerBases();
        registerHeads();
        registerEngines();
        registerTanks();
        registerModules();
        Bukkit.getPluginManager().registerEvents(new DrillDropEnhancements(), plugin);
    }

    // ---------------------------------------------------------------- fuels (custom item based)
    private static void registerFuels() {
        FuelTypeRegistry.register(new FuelType("bio_fuel", "Biokraftstoff",
                stack -> isTemplate(stack, "bio_fuel"), 2500));
        FuelTypeRegistry.register(new FuelType("oil_barrel", "\u00d6lfass",
                stack -> isTemplate(stack, "oil_barrel"), 5000));
        FuelTypeRegistry.register(new FuelType("blaze_fuel", "Lohenkraftstoff",
                stack -> isTemplate(stack, "blaze_fuel"), 10000));
    }

    private static boolean isTemplate(ItemStack stack, String templateId) {
        return templateId.equals(CustomItemService.getTemplateId(stack));
    }

    // ---------------------------------------------------------------- bases
    private static void registerBases() {
        DrillBaseRegistry.register(DrillBase.builder("diamond_drill_base", "Diamant-Bohrerbasis")
                .icon(Material.DIAMOND_PICKAXE)
                .stat(DrillStatRegistry.MINING_SPEED, 50)
                .stat(DrillStatRegistry.MINING_FORTUNE, 25)
                .stat(DrillStatRegistry.MINING_SPREAD, 10)
                .baseMiningLevel(DrillLevels.DIAMOND)
                .lockModule(0).lockModule(1).lockModule(2) // 0 upgrade slots unlocked
                .build());

        DrillBaseRegistry.register(DrillBase.builder("steel_drill_base", "Stahl-Bohrerbasis")
                .icon(Material.IRON_PICKAXE)
                .stat(DrillStatRegistry.MINING_SPEED, 100)
                .stat(DrillStatRegistry.MINING_FORTUNE, 50)
                .stat(DrillStatRegistry.MINING_SPREAD, 25)
                .baseMiningLevel(DrillLevels.DIAMOND)
                .lockModule(1).lockModule(2) // 1 upgrade slot unlocked
                .build());

        DrillBaseRegistry.register(DrillBase.builder("reinforced_steel_drill_base", "Verst\u00e4rkte Stahl-Bohrerbasis")
                .icon(Material.IRON_PICKAXE)
                .stat(DrillStatRegistry.MINING_SPEED, 200)
                .stat(DrillStatRegistry.MINING_FORTUNE, 100)
                .stat(DrillStatRegistry.MINING_SPREAD, 50)
                .baseMiningLevel(DrillLevels.NETHERITE)
                .lockModule(2) // 2 upgrade slots unlocked
                .build());

        DrillBaseRegistry.register(DrillBase.builder("darksteel_drill_base", "Dunkelstahl-Bohrerbasis")
                .icon(Material.NETHERITE_PICKAXE)
                .stat(DrillStatRegistry.MINING_SPEED, 500)
                .stat(DrillStatRegistry.MINING_FORTUNE, 250)
                .stat(DrillStatRegistry.MINING_SPREAD, 100)
                .baseMiningLevel(DrillLevels.NETHERITE) // all 3 slots open
                .build());
    }

    // ---------------------------------------------------------------- drill heads
    private static void registerHeads() {
        head("iron_drill_head", "Eisen-Drillkopf", Material.IRON_PICKAXE, DrillLevels.IRON, 10, 0, 0);
        head("reinforced_iron_drill_head", "Verst\u00e4rkter Eisen-Drillkopf", Material.IRON_PICKAXE,
                DrillLevels.IRON, 15, 10, 0);
        head("diamond_drill_head", "Diamant-Drillkopf", Material.DIAMOND_PICKAXE, DrillLevels.DIAMOND, 20, 0, 0);
        head("reinforced_diamond_drill_head", "Verst\u00e4rkter Diamant-Drillkopf", Material.DIAMOND_PICKAXE,
                DrillLevels.DIAMOND, 25, 10, 0);
        head("steel_drill_head", "Stahl-Drillkopf", Material.NETHERITE_PICKAXE, DrillLevels.NETHERITE, 50, 0, 0);
        head("reinforced_steel_drill_head", "Verst\u00e4rkter Stahl-Drillkopf", Material.NETHERITE_PICKAXE,
                DrillLevels.NETHERITE, 100, 15, 0);
        head("titanium_drill_head", "Titan-Drillkopf", Material.NETHERITE_PICKAXE, DrillLevels.NETHERITE,
                200, 25, 10);
    }

    private static void head(String id, String name, Material icon, int miningLevel,
                             double fortune, double speed, double spread) {
        DrillUpgrade.Builder builder = DrillUpgrade.builder(id, name, UpgradeSlotType.HEAD)
                .icon(icon)
                .miningLevel(miningLevel)
                .stat(DrillStatRegistry.MINING_FORTUNE, fortune);
        if (speed > 0) builder.stat(DrillStatRegistry.MINING_SPEED, speed);
        if (spread > 0) builder.stat(DrillStatRegistry.MINING_SPREAD, spread);
        DrillUpgradeRegistry.register(builder.build());
    }

    // ---------------------------------------------------------------- engines
    private static void registerEngines() {
        engine("silver_drill_engine", "Silber-Motor", Material.GOLD_BLOCK, 100, 25, 0);
        engine("diamond_drill_engine", "Diamant-Motor", Material.DIAMOND_BLOCK, 200, 50, 0);
        engine("steel_drill_engine", "Stahl-Motor", Material.IRON_BLOCK, 250, 75, 50);
        engine("reinforced_steel_drill_engine", "Verst\u00e4rkter Stahl-Motor", Material.NETHERITE_BLOCK,
                500, 100, 100);
    }

    private static void engine(String id, String name, Material icon,
                               double speed, double fortune, double spread) {
        DrillUpgrade.Builder builder = DrillUpgrade.builder(id, name, UpgradeSlotType.ENGINE)
                .icon(icon)
                .stat(DrillStatRegistry.MINING_SPEED, speed)
                .stat(DrillStatRegistry.MINING_FORTUNE, fortune);
        if (spread > 0) builder.stat(DrillStatRegistry.MINING_SPREAD, spread);
        DrillUpgradeRegistry.register(builder.build());
    }

    // ---------------------------------------------------------------- fuel tanks (capacity upgrades)
    private static void registerTanks() {
        tank("iron_fuel_tank", "Eisen-Treibstofftank", Material.IRON_BLOCK, 1000);
        tank("steel_plated_fuel_tank", "Stahl-Treibstofftank", Material.IRON_BLOCK, 5000);
        tank("reinforced_steel_plated_fuel_tank", "Verst\u00e4rkter Stahl-Treibstofftank", Material.NETHERITE_SCRAP, 10000);
        tank("titanium_fuel_tank", "Titan-Treibstofftank", Material.IRON_BLOCK, 50000);
    }

    private static void tank(String id, String name, Material icon, double capacity) {
        DrillUpgradeRegistry.register(DrillUpgrade.builder(id, name, UpgradeSlotType.TANK)
                .icon(icon)
                .stat(DrillStatRegistry.MAX_FUEL, capacity)
                .build());
    }

    // ---------------------------------------------------------------- modules
    private static void registerModules() {
        DrillUpgradeRegistry.register(DrillUpgrade.builder("lucky_metal_clover", "Gl\u00fccks-Klee", UpgradeSlotType.MODULE)
                .icon(Material.LIME_DYE)
                .description("Verdoppelt die Dropchance von Metall-Erzen.")
                .effect(new LootOddsBoostEffect("metal", 2.0))
                .build());

        DrillUpgradeRegistry.register(DrillUpgrade.builder("auto_smeltery", "Auto-Schmelzerei", UpgradeSlotType.MODULE)
                .icon(Material.BLAST_FURNACE)
                .description("Schmilzt abgebaute Erze automatisch.", "Wirkungslos bei Silk Touch.")
                .effect(AutoSmeltEffect.INSTANCE)
                .build());
    }
}
