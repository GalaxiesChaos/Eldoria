package me.chaos.EldoriaJewels;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** /juwelen [balance] | /juwelen <give|take|set> <spieler> <menge> (eldoria.jewels.admin) */
public class JewelsCommand implements BasicCommand {

    private static final String ADMIN = "eldoria.jewels.admin";
    private static final List<String> ADMIN_SUBS = List.of("give", "take", "set");

    private final JewelService service;

    public JewelsCommand(JewelService service) {
        this.service = service;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0 || args[0].equalsIgnoreCase("balance")) {
            if (!(stack.getExecutor() instanceof Player player)) {
                sender.sendMessage(Component.text("Nur für Spieler.").color(NamedTextColor.RED));
                return;
            }
            sender.sendMessage(Component.text("Juwelen: ").color(NamedTextColor.GRAY)
                    .append(Component.text(service.getBalance(player)).color(NamedTextColor.LIGHT_PURPLE)));
            return;
        }

        if (!sender.hasPermission(ADMIN)) {
            sender.sendMessage(Component.text("Keine Berechtigung.").color(NamedTextColor.RED));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(Component.text("Nutzung: /juwelen <give|take|set> <spieler> <menge>")
                    .color(NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Spieler nicht online.").color(NamedTextColor.RED));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Menge muss eine ganze Zahl sein.").color(NamedTextColor.RED));
            return;
        }

        boolean ok = switch (args[0].toLowerCase(Locale.ROOT)) {
            case "give" -> service.add(target, amount);
            case "take" -> service.remove(target, amount);
            case "set" -> service.set(target, amount);
            default -> {
                sender.sendMessage(Component.text("Unbekannte Aktion: " + args[0]).color(NamedTextColor.RED));
                yield true; // Meldung ist bereits raus
            }
        };

        if (!ok) {
            sender.sendMessage(Component.text("Aktion nicht möglich (Betrag ungültig oder zu wenig Juwelen).")
                    .color(NamedTextColor.RED));
        } else if (ADMIN_SUBS.contains(args[0].toLowerCase(Locale.ROOT))) {
            sender.sendMessage(Component.text(target.getName() + " hat jetzt " + service.getBalance(target) + " Juwelen.")
                    .color(NamedTextColor.GREEN));
        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        boolean admin = stack.getSender().hasPermission(ADMIN);

        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> subs = new java.util.ArrayList<>(List.of("balance"));
            if (admin) subs.addAll(ADMIN_SUBS);
            return subs.stream().filter(s -> s.startsWith(prefix)).toList();
        }
        if (!admin) return List.of();

        if (args.length == 2) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .toList();
        }
        if (args.length == 3) return List.of("1", "10", "100");
        return List.of();
    }

    @Override
    public String permission() {
        return "eldoria.jewels";
    }
}
