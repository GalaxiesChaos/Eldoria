package me.chaos.EldoriaCash.Interface;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public class BankCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        BankInterfaceInv Inv = new BankInterfaceInv();
        if (stack.getExecutor() instanceof Player player){
            player.openInventory(Inv.getInventory());
        }
    }
}
