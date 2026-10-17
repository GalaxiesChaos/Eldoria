package me.chaos.EldoriaRules;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.eldoriaBase.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** /regelwerk zeigt das Regelwerk. Admin: /regelwerk npc (spawnt NPC), /regelwerk remove (entfernt nächsten NPC). */
public class RulesCommand implements BasicCommand {

    private static final String ADMIN = "eldoria.rules.admin";

    private final Main plugin;
    private final RulesBook book;

    public RulesCommand(Main plugin, RulesBook book) {
        this.plugin = plugin;
        this.book = book;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        if (args.length == 0) {
            book.show(player);
            return;
        }

        if (!player.hasPermission(ADMIN)) {
            player.sendMessage(Component.text("Keine Berechtigung.").color(NamedTextColor.RED));
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "npc" -> {
                RulesNpc.spawn(plugin, player.getLocation());
                player.sendMessage(Component.text("Regelwerk-NPC platziert.").color(NamedTextColor.GREEN));
            }
            case "remove" -> {
                boolean removed = RulesNpc.removeNearest(plugin, player.getLocation(), 5);
                player.sendMessage(removed
                        ? Component.text("Regelwerk-NPC entfernt.").color(NamedTextColor.GREEN)
                        : Component.text("Kein Regelwerk-NPC im Umkreis von 5 Blöcken.").color(NamedTextColor.RED));
            }
            default -> player.sendMessage(Component.text("Nutzung: /regelwerk [npc|remove]").color(NamedTextColor.RED));
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length > 1 || !stack.getSender().hasPermission(ADMIN)) return List.of();
        String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
        return List.of("npc", "remove").stream().filter(s -> s.startsWith(prefix)).toList();
    }

    @Override
    public String permission() {
        return "eldoria.rules";
    }
}
