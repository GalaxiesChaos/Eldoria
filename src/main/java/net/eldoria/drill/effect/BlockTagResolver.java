package net.eldoria.drill.effect;

import org.bukkit.block.Block;

import java.util.Set;

/** Maps a block to its custom block tag ids (stone, wood, metal, gem, mithril, ...). */
@FunctionalInterface
public interface BlockTagResolver {

    Set<String> tagsOf(Block block);
}
