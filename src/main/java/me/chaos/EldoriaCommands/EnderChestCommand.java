package me.chaos.EldoriaCommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** /ec öffnet die eigene Enderchest, /ec <spieler> (eldoria.ec.others) die eines anderen Online-Spielers. */
public class EnderChestCommand implements BasicCommand {

    private static final String OTHERS = "eldoria.ec.others";

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        if (args.length >= 1) {
            if (!player.hasPermission(OTHERS)) {
                player.sendMessage(Component.text("Keine Berechtigung.").color(NamedTextColor.RED));
                return;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(Component.text("Spieler nicht online.").color(NamedTextColor.RED));
                return;
            }
            player.openInventory(target.getEnderChest());
            return;
        }

        player.openInventory(player.getEnderChest());
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        if (args.length > 1 || !stack.getSender().hasPermission(OTHERS)) return List.of();
        String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(prefix))
                .toList();
    }

    @Override
    public String permission() {
        return "eldoria.ec";
    }
}
