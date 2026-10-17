package me.chaos.EldoriaRules;

import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Zeigt das Regelwerk als Buch. Seiten stehen in der config.yml (rules.pages, MiniMessage).
 * Ein NPC (egal welches System) muss nur {@link #show(Player)} aufrufen.
 */
public class RulesBook {

    private final Main plugin;

    public RulesBook(Main plugin) {
        this.plugin = plugin;
    }

    public void show(Player player) {
        List<String> pages = plugin.getConfig().getStringList("rules.pages");
        if (pages.isEmpty()) {
            player.sendMessage(Component.text("Es ist kein Regelwerk hinterlegt.").color(NamedTextColor.RED));
            return;
        }

        MiniMessage mm = MiniMessage.miniMessage();
        List<Component> components = pages.stream().map(mm::deserialize).toList();
        Component title = mm.deserialize(plugin.getConfig().getString("rules.title", "Regelwerk"));

        player.openBook(Book.book(title, Component.text("Eldoria"), components));
    }
}
