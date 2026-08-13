package me.chaos.EldoriaCash;

import me.chaos.EldoriaCash.Interface.BankInterfaceInv;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Utils.SignInputListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class OnClickEvent implements Listener {
    Main main;
    SignInputListener inputListener;

    public OnClickEvent(Main main){
        this.main = main;
        this.inputListener = new SignInputListener(main);
    }


    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof BankInterfaceInv inv){
            switch (event.getSlot()){
                case 12 -> {
                    inputListener.promptForInteger(player, List.of("Bitte betrag eintragen"), value -> {
                        PlayerMoney money = main.getHandler().getPlayerDataHandler().getPlayerData(player).getMoney();
                        PlayerBank bank = main.getHandler().getPlayerDataHandler().getPlayerData(player).getBank();
                        if (money.hasMoney(value)){
                            if (value >= 0) {
                                money.addMoney(-value);
                                bank.addMoney(value);
                            } else {
                                player.sendMessage("Zahl muss positiv sein");
                            }
                        } else {
                         player.sendMessage("Nicht genug geld");
                        }
                    });
                    event.setCancelled(true);
                }

                case 14 -> {
                    inputListener.promptForInteger(player, List.of("Bitte betrag eintragen"), value -> {
                        PlayerMoney money = main.getHandler().getPlayerDataHandler().getPlayerData(player).getMoney();
                        PlayerBank bank = main.getHandler().getPlayerDataHandler().getPlayerData(player).getBank();
                        if (bank.hasMoney(value)){
                            if (value > 0) {
                                bank.addMoney(-value);
                                money.addMoney(value);
                            } else {
                                player.sendMessage("Zahl muss positiv sein");
                            }
                        } else {
                            player.sendMessage("Nicht genug geld");
                        }
                    });
                    event.setCancelled(true);
                }

                default -> event.setCancelled(true);
            }
        }
    }

}
