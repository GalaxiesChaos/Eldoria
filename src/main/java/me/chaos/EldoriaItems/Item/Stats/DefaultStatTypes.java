package me.chaos.EldoriaItems.Item.Stats;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Ready-to-use stats. Call {@link #registerDefaults()} once during startup
 * (done for you by EldoriaItemsModule). Register more of your own anywhere else - open-ended.
 * <p>
 * MINING_FORTUNE and MINING_SPREAD have functional counterparts in eldoriaBase
 * (see StatLoader) - the bridge is {@link StatKeyBridge}, applied by CustomItemService.
 */
public class DefaultStatTypes {
    public static StatType DROPCHANCE;
    public static StatType MINING_SPEED;
    public static StatType MINING_FORTUNE;
    public static StatType MINING_SPREAD;
    public static StatType DAMAGE;
    public static StatType STRENGTH;
    public static StatType DEFENSE;
    public static StatType SPEED;

    public static void registerDefaults() {
        DROPCHANCE = StatTypeRegistry.register(new StatType("dropchance", "Dropchance", "%", NamedTextColor.GREEN));
        MINING_SPEED = StatTypeRegistry.register(new StatType("mining_speed", "Abbaugeschwindigkeit", "", NamedTextColor.AQUA));
        MINING_FORTUNE = StatTypeRegistry.register(new StatType("mining_fortune", "Bergbaugl\u00fcck", "", NamedTextColor.GOLD));
        MINING_SPREAD = StatTypeRegistry.register(new StatType("mining_spread", "Abbaufl\u00e4che", "", NamedTextColor.YELLOW));
        DAMAGE = StatTypeRegistry.register(new StatType("damage", "Schaden", "", NamedTextColor.RED));
        STRENGTH = StatTypeRegistry.register(new StatType("strength", "St\u00e4rke", "", NamedTextColor.RED));
        DEFENSE = StatTypeRegistry.register(new StatType("defense", "Verteidigung", "", NamedTextColor.BLUE));
        SPEED = StatTypeRegistry.register(new StatType("speed", "Geschwindigkeit", "", NamedTextColor.WHITE));
    }
}
