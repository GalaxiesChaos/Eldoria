package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.EnchantStat;
import me.chaos.EldoriaEnchants.EnchantTarget;
import me.chaos.EldoriaEnchants.StatDefinition;
import me.chaos.EldoriaEnchants.StatEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.AttributeModifier.Operation;

import java.util.List;

/** +2% movement speed per level, boots only. Fully obtainable from the table (cap == max). */
public class SwiftFeetEnchant extends StatEnchant {

    public SwiftFeetEnchant() {
        super("swift_feet", Component.text("Schnelle F\u00fc\u00dfe").color(NamedTextColor.AQUA), 3, 3, EnchantTarget.BOOTS);
    }

    @Override
    public List<StatDefinition> getStatDefinitions() {
        return List.of(StatDefinition.linear(EnchantStat.MOVEMENT_SPEED, Operation.ADD_SCALAR, 0.02));
    }
}
