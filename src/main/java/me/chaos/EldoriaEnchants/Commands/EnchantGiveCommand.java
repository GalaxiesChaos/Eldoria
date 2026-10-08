package me.chaos.EldoriaEnchants;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.EldoriaEnchants.CustomEnchant;
import me.chaos.EldoriaEnchants.EnchantBookFactory;
import me.chaos.EldoriaEnchants.EnchantRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /enchantgive &lt;enchantId&gt; &lt;level&gt;
 * <p>
 * Admin/testing command: hands the executor an Eldoria enchant book. In the
 * finished game these books should mainly come from recipes or rare drops - this
 * command exists so the system can be exercised end-to-end before either of
 * those is wired up.
 */
public class EnchantGiveCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) return;

        if (!player.hasPermission("*")) {
            player.sendMessage(Component.text("Keine Berechtigung.").color(NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Nutzung: /enchantgive <enchant> <level>").color(NamedTextColor.RED));
            return;
        }

        CustomEnchant enchant = EnchantRegistry.get(args[0]);
        if (enchant == null) {
            player.sendMessage(Component.text("Unbekannte Verzauberung: " + args[0]).color(NamedTextColor.RED));
            return;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Level muss eine Zahl sein.").color(NamedTextColor.RED));
            return;
        }

        player.getInventory().addItem(EnchantBookFactory.create(enchant, level));
        player.sendMessage(Component.text("Buch erhalten.").color(NamedTextColor.GREEN));
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack stack, String @NonNull [] args) {
        if (args.length <= 1) {
            return EnchantRegistry.getAll().stream().map(CustomEnchant::getId).collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("1", "2", "3", "4", "5");
        }
        return List.of();
    }
}
