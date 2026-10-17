package me.chaos.eldoriaBase.WarpCommand;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.WarpNotFoundExeption;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WarpCommand implements BasicCommand {
    private static final String ADMIN_PERMISSION = "eldoria.warp.admin";

    private final WarpHandler handler;

    public WarpCommand(Main main) {
        handler = main.getHandler().getWarpHandler();
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        if (args.length < 2) {
            player.sendMessage("Falsche Command Länge");
            return;
        }

        String action = args[0];
        String value = args[1];

        // FIXED: case-insensitive matching instead of listing "to","To","TO","tO" by hand
        switch (action.toLowerCase(Locale.ROOT)) {
            case "to" -> {
                if (handler.containsWarp(value, player)) {
                    try {
                        player.teleport(handler.getWarpmanager(player).getWarp(value).getWarpLocation());
                    } catch (WarpNotFoundExeption e) {
                        player.sendMessage(e.getMessage());

                    }
                } else {
                    player.sendMessage(ChatColor.RED + value + " ist keine gültige ID");
                }
            }

            case "set" -> {
                try {
                    if (handler.containsWarp(value, player)) {
                        // FIXED: old code used ".formatted(ChatColor.RED)" with no format
                        // specifier, so the color was silently dropped
                        player.sendMessage(ChatColor.RED + "Dieser Warp existiert bereits");
                        return;
                    }
                    handler.getWarpmanager(player).addWarp(new WarpPoint(player, value));
                    player.sendMessage("Warp hinzugefügt");
                } catch (NotEnoughWarpsExeption e) {
                    player.sendMessage(e.getMessage());
                }
            }

            case "addpublic" -> {
                // FIXED: real permission node instead of hasPermission("*"), plus a
                // proper "no permission" message (old code just did nothing silently)
                if (!player.hasPermission(ADMIN_PERMISSION)) {
                    player.sendMessage(ChatColor.RED + "Keine Berechtigung.");
                    return;
                }
                PublicWarp publicWarp = new PublicWarp();
                if (handler.getWarpmanager(player).getIds().stream().anyMatch(value::equalsIgnoreCase)) {
                    player.sendMessage("Dieser Warp existiert bereits");
                    return;
                }
                publicWarp.add(new WarpPoint(player, value));
                publicWarp.SaveToFile();
                handler.reloadPublic();
            }

            default -> {
                if (player.hasPermission(ADMIN_PERMISSION)) {
                    player.sendMessage(Component.text(action + ", " + value));
                }
                player.sendMessage(ChatColor.RED + action + " ist keine valide Eingabe");
            }
        }
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) {
            return Collections.emptyList();
        }

        switch (args.length) {
            case 0, 1 -> {
                // FIXED: old code put subcommand suggestions in case 0 and read args[1]
                // in case 1, throwing ArrayIndexOutOfBoundsException on every tab-complete
                List<String> re = new ArrayList<>();
                if (commandSourceStack.getExecutor().hasPermission(ADMIN_PERMISSION)) {
                    re.add("addPublic");
                }
                re.add("set");
                re.add("to");
                return re;
            }

            case 2 -> {
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("addPublic")) {
                    return Collections.singleton("");
                }
                return handler.getIds(player);
            }

            default -> {
                return Collections.singleton("");
            }
        }
    }
}
