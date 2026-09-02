package me.chaos.EldoriaQuests;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.chaos.EldoriaQuests.Interface.QuestSelectionInv;
import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class QuestCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) {
            return;
        }

        try {
            QuestSelectionInv inv = new QuestSelectionInv();
            player.openInventory(inv.getInventory());
        } catch (DefaultQuestExeption e) {
            player.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
        }
    }
}