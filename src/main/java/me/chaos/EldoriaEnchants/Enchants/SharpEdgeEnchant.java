package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.EnchantStat;
import me.chaos.EldoriaEnchants.EnchantTarget;
import me.chaos.EldoriaEnchants.StatDefinition;
import me.chaos.EldoriaEnchants.StatEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.List;

/** +0.5 attack damage per level. Table cap 3, absolute max 5 (levels 4-5 need a book). */
public class SharpEdgeEnchant extends StatEnchant {

    public SharpEdgeEnchant() {
        super("sharp_edge", Component.text("Scharfe Klinge").color(NamedTextColor.YELLOW), 5, 3, EnchantTarget.SWORD);
    }

    @Override
    public List<StatDefinition> getStatDefinitions() {
        return List.of(StatDefinition.linear(EnchantStat.ATTACK_DAMAGE, Operation.ADD_NUMBER, 0.5));
    }
}
