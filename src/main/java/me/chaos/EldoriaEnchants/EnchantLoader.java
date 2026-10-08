package me.chaos.EldoriaEnchants;

import me.chaos.EldoriaEnchants.Enchants.RetaliationPoisonEnchant;
import me.chaos.EldoriaEnchants.Enchants.SharpEdgeEnchant;
import me.chaos.EldoriaEnchants.Enchants.SwiftFeetEnchant;
import me.chaos.EldoriaEnchants.Enchants.VenomousStrikeEnchant;
import me.chaos.EldoriaEnchants.Enchants.VitalityEnchant;

/** Call once on plugin startup, before anything tries to read/write enchant data. */
public class EnchantLoader {

    private EnchantLoader() {}

    public static void loadEnchants() {
        EnchantRegistry.register(new SharpEdgeEnchant());
        EnchantRegistry.register(new VitalityEnchant());
        EnchantRegistry.register(new SwiftFeetEnchant());
        EnchantRegistry.register(new RetaliationPoisonEnchant());
        EnchantRegistry.register(new VenomousStrikeEnchant());
    }
}
