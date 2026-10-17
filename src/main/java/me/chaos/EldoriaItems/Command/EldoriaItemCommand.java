package me.chaos.EldoriaItems.Command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.EldoriaItems.Item.CustomItemRegistry;
import me.chaos.EldoriaItems.Item.CustomItemService;
import me.chaos.EldoriaItems.Item.CustomItemTemplate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Small testing/admin command for the custom item system - not required, but handy for
 * trying out items in-game without a real drop table yet.
 * Usage:
 *   /eitem give <player> <itemId>
 *   /eitem list
 *   /eitem upgrade   (upgrades the item held in your main hand by one rarity tier)
 */
public class EldoriaItemCommand implements BasicCommand {

    @Override
    public String permission() {
        return "eldoria.item.admin";
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (args.length == 0) {
            stack.getSender().sendMessage(Component.text("/eitem <give|list|upgrade> ...").color(NamedTextColor.GRAY));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                for (CustomItemTemplate template : CustomItemRegistry.getAll()) {
                    stack.getSender().sendMessage(Component.text("- " + template.getId()).color(NamedTextColor.YELLOW));
                }
            }

            case "give" -> {
                if (args.length < 3) {
                    stack.getSender().sendMessage(Component.text("Nutzung: /eitem give <spieler> <itemId>").color(NamedTextColor.RED));
                    return;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    stack.getSender().sendMessage(Component.text("Spieler nicht gefunden.").color(NamedTextColor.RED));
                    return;
                }
                ItemStack item = CustomItemService.createItemStack(args[2]);
                if (item == null) {
                    stack.getSender().sendMessage(Component.text("Unbekannte Item-ID: " + args[2]).color(NamedTextColor.RED));
                    return;
                }
                target.getInventory().addItem(item);
                stack.getSender().sendMessage(Component.text("Item gegeben.").color(NamedTextColor.GREEN));
            }

            case "upgrade" -> {
                if (!(stack.getExecutor() instanceof Player player)) return;
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (!CustomItemService.isCustomItem(hand)) {
                    player.sendMessage(Component.text("Halte ein Custom-Item in der Hand.").color(NamedTextColor.RED));
                    return;
                }
                ItemStack upgraded = CustomItemService.upgradeRarity(hand);
                player.getInventory().setItemInMainHand(upgraded);
                player.sendMessage(Component.text("Seltenheit hochgestuft.").color(NamedTextColor.GREEN));
            }

            default -> stack.getSender().sendMessage(Component.text("Unbekannter Subcommand.").color(NamedTextColor.RED));
        }
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        if (args.length == 1) {
            return List.of("give", "list", "upgrade");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return CustomItemRegistry.getAll().stream().map(CustomItemTemplate::getId).toList();
        }
        return Collections.emptyList();
    }
}
