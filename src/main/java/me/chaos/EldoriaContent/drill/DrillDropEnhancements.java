package me.chaos.EldoriaContent.drill;

import me.chaos.EldoriaDrill.data.DrillCalculator;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.effect.DrillEffect;
import me.chaos.EldoriaDrill.stat.DrillStatRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Applies two drill stats/effects to the blocks a drill actually breaks:
 * <ul>
 *   <li><b>Bergbaugl\u00fcck</b> ({@link DrillStatRegistry#MINING_FORTUNE}): multiplies drop stack
 *       sizes (100 = double, same roll math as the base stats).</li>
 *   <li><b>Auto-Schmelzerei</b> ({@link AutoSmeltEffect}): converts ore drops into their furnace
 *       product, unless the drill has Silk Touch.</li>
 * </ul>
 * Registered by {@link MiningDrillContent}.
 */
public final class DrillDropEnhancements implements Listener {

    /** Lazily built map of smeltable input -> furnace/blasting output. */
    private static volatile Map<Material, Material> SMELT_MAP;

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDrillDrops(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Optional<DrillData> loaded = DrillItems.read(hand);
        if (loaded.isEmpty()) {
            return;
        }

        DrillData data = loaded.get();
        double fortune = DrillCalculator.totalStats(data).get(DrillStatRegistry.MINING_FORTUNE);
        boolean autoSmelt = false;
        boolean silktouch = hasSilkTouch(hand);
        for (DrillEffect effect : DrillCalculator.effects(data)) {
            if (effect instanceof AutoSmeltEffect) {
                autoSmelt = true;
            }
        }
        if (fortune <= 0 && (!autoSmelt || silktouch)) {
            return;
        }

        for (Item drop : event.getItems()) {
            ItemStack stack = drop.getItemStack();
            if (autoSmelt && !silktouch) {
                Material smelted = smeltResult(stack.getType());
                if (smelted != null) {
                    stack = new ItemStack(smelted, stack.getAmount());
                }
            }
            if (fortune > 0) {
                int multiplier = 1 + (int) Math.floor(fortune / 100.0);
                int remainder = (int) Math.floor(fortune % 100.0);
                if (remainder > 0 && Math.random() * 100.0 < remainder) {
                    multiplier++;
                }
                stack.setAmount(Math.min(Math.max(1, stack.getMaxStackSize()), stack.getAmount() * multiplier));
            }
            drop.setItemStack(stack);
        }
    }

    private static boolean hasSilkTouch(ItemStack hand) {
        ItemMeta meta = hand.getItemMeta();
        return meta != null && meta.getEnchantLevel(Enchantment.SILK_TOUCH) > 0;
    }

    private static Material smeltResult(Material input) {
        Map<Material, Material> map = SMELT_MAP;
        if (map == null) {
            synchronized (DrillDropEnhancements.class) {
                map = SMELT_MAP;
                if (map == null) {
                    map = buildSmeltMap();
                    SMELT_MAP = map;
                }
            }
        }
        return map.get(input);
    }

    private static Map<Material, Material> buildSmeltMap() {
        Map<Material, Material> map = new EnumMap<>(Material.class);
        ItemStack probe = new ItemStack(Material.IRON_ORE); // representative input stack
        for (@NotNull Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            if (!(recipe instanceof org.bukkit.inventory.FurnaceRecipe furnace)) {
                continue;
            }
            org.bukkit.inventory.RecipeChoice choice = furnace.getInputChoice();
            if (choice == null) {
                continue;
            }
            for (Material material : Material.values()) {
                if (material.isAir() || map.containsKey(material)) {
                    continue;
                }
                probe.setType(material);
                if (choice.test(probe)) {
                    map.put(material, furnace.getResult().getType());
                }
            }
        }
        return map;
    }
}
