package me.chaos.EldoriaForge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.chaos.EldoriaForge.data.PlayerForgeData;
import me.chaos.EldoriaForge.menu.ForgeMenu;
import me.chaos.EldoriaForge.util.Text;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * /forge                        opens the forge menu
 * /forge admin addslot <player> unlocks one additional forge slot (admin/testing)
 */
public final class ForgeCommand {

    private static final String ADMIN_PERMISSION = "eldoria.forge.admin";

    private ForgeCommand() {
    }

    public static void register(JavaPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register(build(), "Öffnet die Schmiede", List.of()));
    }

    private static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("forge")
                .requires(source -> source.getSender() instanceof Player)
                .executes(ctx -> {
                    ForgeMenu.open((Player) ctx.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("admin")
                        .requires(source -> source.getSender().hasPermission(ADMIN_PERMISSION))
                        .then(Commands.literal("addslot")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(ForgeCommand::addSlot))))
                .build();
    }

    private static int addSlot(CommandContext<CommandSourceStack> ctx) {
        Player target = Bukkit.getPlayerExact(StringArgumentType.getString(ctx, "player"));
        if (target == null) {
            ctx.getSource().getSender().sendMessage(Text.plain("Spieler nicht gefunden.", NamedTextColor.RED));
            return 0;
        }
        PlayerForgeData data = ForgeModule.forgeData(target);
        if (data == null) {
            ctx.getSource().getSender().sendMessage(Text.plain("Spielerdaten sind noch nicht geladen.", NamedTextColor.RED));
            return 0;
        }
        if (data.addSlot(target)) {
            ctx.getSource().getSender().sendMessage(Text.plain(
                    "Schmiedeplatz für " + target.getName() + " freigeschaltet ("
                            + data.unlockedSlots() + "/" + data.totalSlots() + ").", NamedTextColor.GREEN));
        } else {
            ctx.getSource().getSender().sendMessage(Text.plain(
                    target.getName() + " hat bereits alle Schmiedeplätze freigeschaltet.", NamedTextColor.RED));
        }
        return Command.SINGLE_SUCCESS;
    }
}
