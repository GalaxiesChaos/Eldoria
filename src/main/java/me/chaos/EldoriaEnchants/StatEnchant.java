package me.chaos.EldoriaEnchants;

import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * An enchant that does nothing but grant passive stat boosts (real vanilla
 * AttributeModifiers) while equipped - e.g. "+0.5 attack damage per level".
 * Implementors just describe which stats scale and by how much per level;
 * {@link ItemEnchantHandler} takes care of applying/removing the modifiers.
 */
public abstract class StatEnchant extends CustomEnchant {

    protected StatEnchant(String id, Component displayName, int maxLevel, int maxTableLevel, EnchantTarget target) {
        super(id, displayName, maxLevel, maxTableLevel, target);
    }

    /**
     * One entry per stat this enchant touches. Called every time the item's
     * enchant data changes, so it's fine to return a freshly built list each time.
     */
    public abstract List<StatDefinition> getStatDefinitions();
}
