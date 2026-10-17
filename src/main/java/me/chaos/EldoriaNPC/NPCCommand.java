package me.chaos.EldoriaNPC;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.EldoriaCash.Interface.BankInterfaceInv;
import me.chaos.EldoriaDrill.menu.DrillMenu;
import me.chaos.EldoriaQuests.Interface.QuestSelectionInv;
import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Admin command: {@code /npc spawn <id|all>} | {@code /npc remove <id|all>} | {@code /npc list} | {@code /npc reload}.
 * Permission {@code eldoria.npc.admin} (default: op).
 */
public final class NPCCommand implements BasicCommand {

    private final Plugin plugin;
    private final NPCManager manager;

    public NPCCommand(Plugin plugin, NPCManager manager) {
        this.plugin = plugin;
        this.manager = manager;

        // Built-in windows an NPC can open. Register more via GuiOpenerRegistry#register.
        GuiOpenerRegistry.register("quests", player -> {
            try {
                player.openInventory(new QuestSelectionInv().getInventory());
            } catch (DefaultQuestExeption e) {
                player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
            }
        });
        GuiOpenerRegistry.register("bank", player ->
                player.openInventory(new BankInterfaceInv().getInventory()));
        GuiOpenerRegistry.register("drill", player -> {
            if (!DrillMenu.open(player)) {
                player.sendMessage(Component.text("Du musst einen Bohrer in der Haupthand halten, um das Menü zu öffnen.")
                        .color(NamedTextColor.RED));
            }
        });
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        CommandSender sender = stack.getSender();

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        switch (args[0].toLowerCase(java.util.Locale.ROOT)) {
            case "spawn" -> handleSpawn(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "list" -> handleList(sender);
            case "reload" -> handleReload(sender);
            default -> sendUsage(sender);
        }
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /npc spawn <id|all>").color(NamedTextColor.RED));
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Dieser Befehl kann nur von einem Spieler ausgeführt werden.").color(NamedTextColor.RED));
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            for (NPCDefinition def : NPCRegistry.getAll()) {
                manager.spawn(def, player.getLocation());
            }
            sender.sendMessage(Component.text("Alle NPCs wurden bei dir gespawnt.").color(NamedTextColor.GREEN));
            return;
        }

        NPCRegistry.get(args[1]).ifPresentOrElse(
                def -> {
                    manager.spawn(def, player.getLocation());
                    sender.sendMessage(Component.text("NPC '" + def.id() + "' gespawnt.").color(NamedTextColor.GREEN));
                },
                () -> sender.sendMessage(Component.text("Unbekannte NPC-Definition '" + args[1] + "'. Siehe /npc list.").color(NamedTextColor.RED)));
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /npc remove <id|all>").color(NamedTextColor.RED));
            return;
        }

        if (args[1].equalsIgnoreCase("all")) {
            int removed = 0;
            for (NPCDefinition def : NPCRegistry.getAll()) {
                removed += manager.removeAll(def.id());
            }
            sender.sendMessage(Component.text(removed + " NPCs entfernt.").color(NamedTextColor.GREEN));
            return;
        }

        NPCDefinition def = NPCRegistry.get(args[1]).orElse(null);
        if (def == null) {
            sender.sendMessage(Component.text("Unbekannte NPC-Definition '" + args[1] + "'.").color(NamedTextColor.RED));
            return;
        }

        int removed = sender instanceof Player player
                ? manager.removeNearest(def.id(), player.getLocation())
                : manager.removeAll(def.id());
        sender.sendMessage(Component.text(removed > 0 ? removed + " '" + def.id() + "' NPC(s) entfernt."
                : "Kein gespawnter '" + def.id() + "' NPC gefunden.").color(removed > 0 ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    private void handleList(CommandSender sender) {
        Collection<NPCDefinition> defs = NPCRegistry.getAll();
        if (defs.isEmpty()) {
            sender.sendMessage(Component.text("Keine NPC-Definitionen vorhanden.").color(NamedTextColor.YELLOW));
            return;
        }
        Map<String, Integer> counts = manager.getSpawnCounts();
        sender.sendMessage(Component.text("NPC-Definitionen:", NamedTextColor.GOLD));
        for (NPCDefinition def : defs) {
            int count = counts.getOrDefault(def.id(), 0);
            String action = switch (def.interaction()) {
                case GUI -> "GUI:" + def.guiId();
                case TELEPORT -> "Teleport -> " + def.teleportWorld() + (def.teleportRandom() ? " (zufällig)" : "");
                case DIALOGUE -> def.lines().size() + " Dialogzeile(n)";
            };
            sender.sendMessage(Component.text("  - " + def.id() + " (", NamedTextColor.GRAY)
                    .append(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                            .legacyAmpersand().deserialize(def.displayName()))
                    .append(Component.text(") [" + def.entityType() + ", " + action + "] - " + count + " gespawnt", NamedTextColor.GRAY)));
        }
    }

    private void handleReload(CommandSender sender) {
        NPCRegistry.reload(plugin);
        sender.sendMessage(Component.text("npcs.yml neu geladen.").color(NamedTextColor.GREEN));
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("Usage: /npc <spawn|remove|list|reload> [id|all]", NamedTextColor.YELLOW));
    }

    @Override
    public List<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length == 1) return List.of("spawn", "remove", "list", "reload");
        if (args.length == 2 && (args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("remove"))) {
            return NPCRegistry.getAll().stream().map(NPCDefinition::id).toList();
        }
        return List.of();
    }

    @Override
    public String permission() {
        return "eldoria.npc.admin";
    }
}
