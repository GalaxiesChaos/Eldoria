package me.chaos.EldoriaDrops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Single entry point of the drop system - call once from {@code Main#onEnable()},
 * after the stats have been loaded:
 * <pre>DropModule.init(this);</pre>
 */
public final class DropModule {

    private DropModule() {}

    public static void init(Plugin plugin) {
        BlockTagLoader.loadTags();   // 1) which block belongs to which tag
        // NEW: expose the drop tags to the drill system (e.g. LootOddsBoostEffect("metal"))
        me.chaos.EldoriaDrill.effect.BlockTags.setResolver(block -> BlockTagRegistry.getTags(block.getType())
                .stream().map(BlockTag::getId).collect(java.util.stream.Collectors.toUnmodifiableSet()));
        DropLoader.loadDrops();      // 2) which tag drops what
        Bukkit.getPluginManager().registerEvents(new DropListener(), plugin);
    }
}
