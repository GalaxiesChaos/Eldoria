package me.chaos.EldoriaDrill.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.chaos.EldoriaDrill.base.DrillBase;
import me.chaos.EldoriaDrill.base.DrillBaseRegistry;
import me.chaos.EldoriaDrill.data.DrillCalculator;
import me.chaos.EldoriaDrill.data.DrillData;
import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaDrill.menu.DrillMenu;
import me.chaos.EldoriaDrill.menu.RefuelMenu;
import me.chaos.EldoriaDrill.upgrade.DrillUpgrade;
import me.chaos.EldoriaDrill.upgrade.DrillUpgradeRegistry;
import me.chaos.EldoriaDrill.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * /drill              opens the drill menu (drill in main hand)
 * /drill fuel         opens the refuel menu
 * /drill admin givebase <id> | giveupgrade <id> | setfuel <amount>
 */
public final class DrillCommand {

    private static final String ADMIN_PERMISSION = "eldoria.drill.admin";

    private DrillCommand() {
    }

    public static void register(JavaPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register(build(), "Öffnet das Bohrer-Menü", List.of()));
    }

    private static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("drill")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    if (!DrillMenu.open(player)) {
                        notHolding(player);
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("fuel").executes(ctx -> {
                    Player player = (Player) ctx.getSource().getSender();
                    if (!RefuelMenu.open(player)) {
                        notHolding(player);
                    }
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("admin")
                        .requires(source -> source.getSender().hasPermission(ADMIN_PERMISSION))
                        .then(Commands.literal("givebase")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(DrillCommand::suggestBases)
                                        .executes(DrillCommand::giveBase)))
                        .then(Commands.literal("giveupgrade")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .suggests(DrillCommand::suggestUpgrades)
                                        .executes(DrillCommand::giveUpgrade)))
                        .then(Commands.literal("setfuel")
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(DrillCommand::setFuel))))
                .build();
    }

    private static int giveBase(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String id = StringArgumentType.getString(ctx, "id");
        Optional<DrillBase> base = DrillBaseRegistry.get(id);
        if (base.isEmpty()) {
            player.sendMessage(Text.plain("Unbekannte Bohrerbasis: " + id, NamedTextColor.RED));
            return 0;
        }
        player.getInventory().addItem(DrillItems.createBase(base.get()));
        player.sendMessage(Text.plain("Erhalten: " + base.get().displayName() + ".", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int giveUpgrade(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String id = StringArgumentType.getString(ctx, "id");
        Optional<DrillUpgrade> upgrade = DrillUpgradeRegistry.get(id);
        if (upgrade.isEmpty()) {
            player.sendMessage(Text.plain("Unbekanntes Upgrade: " + id, NamedTextColor.RED));
            return 0;
        }
        player.getInventory().addItem(DrillItems.createUpgrade(upgrade.get()));
        player.sendMessage(Text.plain("Erhalten: " + upgrade.get().displayName() + ".", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int setFuel(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Optional<DrillData> data = DrillItems.read(hand);
        if (data.isEmpty()) {
            notHolding(player);
            return 0;
        }
        double max = DrillCalculator.maxFuel(DrillCalculator.totalStats(data.get()));
        data.get().setFuel(Math.min(max, DoubleArgumentType.getDouble(ctx, "amount")));
        DrillItems.write(hand, data.get());
        player.getInventory().setItemInMainHand(hand);
        player.sendMessage(Text.plain("Treibstoff gesetzt auf " + Text.num(data.get().fuel()) + ".", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestBases(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        DrillBaseRegistry.ids().forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestUpgrades(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        DrillUpgradeRegistry.ids().forEach(builder::suggest);
        return builder.buildFuture();
    }

    private static void notHolding(Player player) {
        player.sendMessage(Text.plain("Halte zuerst einen Bohrer in der Haupthand.", NamedTextColor.RED));
    }
}
