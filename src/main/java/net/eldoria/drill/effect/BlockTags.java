package net.eldoria.drill.effect;

import org.bukkit.block.Block;

import java.util.Set;

/**
 * Hook for the custom drop / block tag system.
 * Default: no tags, so tag based module effects do nothing until a resolver is set:
 * {@code BlockTags.setResolver(block -> myTagService.tagsOf(block));}
 */
public final class BlockTags {

    private static volatile BlockTagResolver resolver = block -> Set.of();

    private BlockTags() {
    }

    public static void setResolver(BlockTagResolver newResolver) {
        resolver = newResolver;
    }

    public static Set<String> of(Block block) {
        return resolver.tagsOf(block);
    }
}
