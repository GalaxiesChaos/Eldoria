package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.EnchantStat;
import me.chaos.EldoriaEnchants.EnchantTarget;
import me.chaos.EldoriaEnchants.StatDefinition;
import me.chaos.EldoriaEnchants.StatEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.List;

/** +1 max health per level, on any armor slot. Table cap 2, absolute max 4. */
public class VitalityEnchant extends StatEnchant {

    public VitalityEnchant() {
        super("vitality", Component.text("Vitalit\u00e4t").color(NamedTextColor.RED), 4, 2, EnchantTarget.ARMOR);
    }

    @Override
    public List<StatDefinition> getStatDefinitions() {
        return List.of(StatDefinition.linear(EnchantStat.MAX_HEALTH, Operation.ADD_NUMBER, 1.0));
    }
}
