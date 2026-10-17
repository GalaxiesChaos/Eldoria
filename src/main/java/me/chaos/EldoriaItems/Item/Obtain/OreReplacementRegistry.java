package me.chaos.EldoriaItems.Item.Obtain;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class OreReplacementRegistry {
    private static final List<OreReplacementEntry> ENTRIES = new ArrayList<>();

    public static void register(OreReplacementEntry entry) {
        ENTRIES.add(entry);
    }

    public static List<OreReplacementEntry> getForBlock(Material material) {
        return ENTRIES.stream().filter(e -> e.sourceBlock() == material).toList();
    }
}
