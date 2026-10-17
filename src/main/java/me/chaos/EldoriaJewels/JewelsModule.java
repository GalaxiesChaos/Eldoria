package me.chaos.EldoriaJewels;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataRegistry;

import java.util.List;

/** Einstiegspunkt: einmal aus Main#onEnable aufrufen (registriert Daten-Codec, Supplier und Befehl). */
public final class JewelsModule {

    private JewelsModule() {}

    public static void init(Main plugin) {
        PlayerDataRegistry.register(PlayerJewels.SAVE_KEY, PlayerJewels.CODEC);
        PlayerDataRegistry.register(PlayerJewels::new);

        plugin.registerCommand("juwelen", List.of("jewels"),
                new JewelsCommand(plugin.getHandler().getJewelService()));
    }
}
