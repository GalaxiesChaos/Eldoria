package me.chaos.EldoriaRules;

import me.chaos.eldoriaBase.Main;
import org.bukkit.Bukkit;

import java.util.List;

/** Einstiegspunkt: einmal aus Main#onEnable aufrufen. */
public final class RulesModule {

    private RulesModule() {}

    public static void init(Main plugin) {
        RulesBook book = new RulesBook(plugin);
        Bukkit.getPluginManager().registerEvents(new RulesNpcListener(plugin, book), plugin);
        plugin.registerCommand("regelwerk", List.of("rules", "regeln"), new RulesCommand(plugin, book));
    }
}
