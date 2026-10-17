package me.chaos.EldoriaContent;

import me.chaos.EldoriaContent.drill.MiningDrillContent;
import me.chaos.EldoriaContent.drops.MiningDrops;
import me.chaos.EldoriaContent.forge.MiningForgeRecipes;
import me.chaos.EldoriaContent.items.ArmorItems;
import me.chaos.EldoriaContent.items.MaterialItems;
import me.chaos.EldoriaContent.items.ToolItems;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Registers all Eldoria mining content: custom materials, enchanted materials, tools,
 * armor sets (incl. set bonuses), drill parts/fuels/modules, forge recipes and bonus drops.
 * <p>
 * Call once from Main#onEnable AFTER EldoriaItemsModule, DropModule, DrillModule and ForgeModule:
 * <pre>{@code MiningContentModule.init(this);}</pre>
 */
public final class MiningContentModule {

    private MiningContentModule() {
    }

    public static void init(JavaPlugin plugin) {
        MaterialItems.register(plugin);
        ToolItems.register(plugin);
        ArmorItems.register(plugin);
        MiningDrillContent.register(plugin);
        MiningForgeRecipes.register();
        MiningDrops.register();
    }
}
