package me.chaos.eldoriaBase.Stats;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Describes which blocks, items and/or entities a {@link Stat} is allowed to affect.
 * A concrete Stat decides itself which of the three lists it actually checks (e.g. a
 * mining stat only cares about {@link #appliesToBlock(Material)}, a combat stat only
 * about {@link #appliesToEntity(EntityType)}).
 * <p>
 * Build one with {@link #builder()} - this keeps adding new targets to a stat a one-liner.
 */
public class StatApplicability {

    public static final StatApplicability NONE = StatApplicability.builder().build();

    private final Set<Material> blocks;
    private final Set<Material> items;
    private final Set<EntityType> entities;

    private StatApplicability(Set<Material> blocks, Set<Material> items, Set<EntityType> entities) {
        this.blocks = blocks;
        this.items = items;
        this.entities = entities;
    }

    public Set<Material> getBlocks() {
        return Collections.unmodifiableSet(blocks);
    }

    public Set<Material> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public Set<EntityType> getEntities() {
        return Collections.unmodifiableSet(entities);
    }

    public boolean appliesToBlock(Material material) {
        return material != null && blocks.contains(material);
    }

    public boolean appliesToItem(Material material) {
        return material != null && items.contains(material);
    }

    public boolean appliesToEntity(EntityType type) {
        return type != null && entities.contains(type);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<Material> blocks = EnumSet.noneOf(Material.class);
        private final Set<Material> items = EnumSet.noneOf(Material.class);
        private final Set<EntityType> entities = EnumSet.noneOf(EntityType.class);

        public Builder blocks(Material... materials) {
            Collections.addAll(blocks, materials);
            return this;
        }

        public Builder blocks(Collection<Material> materials) {
            blocks.addAll(materials);
            return this;
        }

        public Builder items(Material... materials) {
            Collections.addAll(items, materials);
            return this;
        }

        public Builder items(Collection<Material> materials) {
            items.addAll(materials);
            return this;
        }

        public Builder entities(EntityType... types) {
            Collections.addAll(entities, types);
            return this;
        }

        public Builder entities(Collection<EntityType> types) {
            entities.addAll(types);
            return this;
        }

        public StatApplicability build() {
            return new StatApplicability(blocks, items, entities);
        }
    }
}