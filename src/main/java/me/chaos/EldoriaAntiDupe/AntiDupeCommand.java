package me.chaos.EldoriaAntiDupe;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.EldoriaAntiDupe.Serial.DupeScanner;
import me.chaos.EldoriaAntiDupe.Serial.ItemSerial;
import me.chaos.EldoriaAntiDupe.Serial.SerialRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** /antidupe status | scan | reload | quarantine | restore &lt;id&gt; */
public final class AntiDupeCommand implements BasicCommand {

    private static final String PERMISSION = "eldoria.antidupe.admin";
    private static final List<String> SUBCOMMANDS = List.of("status", "scan", "reload", "quarantine", "restore");

    private final AntiDupeConfig config;
    private final SerialRegistry registry;
    private final DupeScanner scanner;

    public AntiDupeCommand(AntiDupeConfig config, SerialRegistry registry, DupeScanner scanner) {
        this.config = config;
        this.registry = registry;
        this.scanner = scanner;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (args.length == 0) {
            usage(sender);
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "status" -> status(sender);
            case "scan" -> {
                DupeScanner.Result r = scanner.scanNow();
                msg(sender, "Scan fertig in " + r.millis() + " ms: " + r.items() + " Items, "
                        + r.duplicates() + " Duplikat-Gruppen, " + r.revokedRemoved() + " widerrufene entfernt, "
                        + r.stamped() + " neu nummeriert.");
            }
            case "reload" -> {
                config.reload();
                msg(sender, "Konfiguration neu geladen (Scan-Intervall erst nach Neustart).");
            }
            case "quarantine" -> quarantine(sender);
            case "restore" -> restore(stack, args);
            default -> usage(sender);
        }
    }

    private void status(CommandSender sender) {
        msg(sender, "Modus: " + config.mode + " | widerrufene Seriennummern: " + registry.revokedCount()
                + " | Quarantaene: " + registry.getQuarantine().size());
        DupeScanner.Result r = scanner.getLastResult();
        if (r == null) {
            msg(sender, "Noch kein Scan gelaufen.");
        } else {
            msg(sender, "Letzter Scan: " + r.millis() + " ms, " + r.items() + " Items, "
                    + r.duplicates() + " Duplikat-Gruppen.");
        }
    }

    private void quarantine(CommandSender sender) {
        List<SerialRegistry.QuarantineEntry> entries = registry.getQuarantine();
        if (entries.isEmpty()) {
            msg(sender, "Quarantaene ist leer.");
            return;
        }
        int from = Math.max(0, entries.size() - 10);
        long now = System.currentTimeMillis();
        for (int i = entries.size() - 1; i >= from; i--) {
            SerialRegistry.QuarantineEntry e = entries.get(i);
            long minutes = (now - e.time()) / 60_000L;
            msg(sender, "#" + e.id() + " " + e.type() + " - " + e.label() + " (vor " + minutes + " min)");
        }
        msg(sender, "Wiederherstellen mit /antidupe restore <id>");
    }

    private void restore(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();
        if (!(stack.getExecutor() instanceof Player player)) {
            msg(sender, "Nur als Spieler nutzbar.");
            return;
        }
        if (args.length < 2) {
            msg(sender, "Nutzung: /antidupe restore <id>");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            msg(sender, "Die ID muss eine Zahl sein.");
            return;
        }

        ItemStack item = registry.takeQuarantine(id);
        if (item == null) {
            msg(sender, "Kein Quarantaene-Eintrag mit der ID " + id + ".");
            return;
        }

        // The old serial is revoked - without stripping it the scanner would delete the item again.
        ItemSerial.strip(item);
        player.getInventory().addItem(item).values()
                .forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
        msg(sender, "Gegenstand #" + id + " wiederhergestellt (bekommt beim naechsten Scan eine neue Seriennummer).");
    }

    private void usage(CommandSender sender) {
        msg(sender, "Nutzung: /antidupe <status|scan|reload|quarantine|restore <id>>");
    }

    private void msg(CommandSender sender, String text) {
        sender.sendMessage(Component.text("[AntiDupe] " + text, NamedTextColor.GRAY));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> result = new ArrayList<>();
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(prefix)) result.add(sub);
            }
            return result;
        }
        return List.of();
    }

    @Override
    public String permission() {
        return PERMISSION;
    }
}
