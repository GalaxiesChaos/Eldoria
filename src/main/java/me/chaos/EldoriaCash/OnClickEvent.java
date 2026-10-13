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
    private final Main main;
    // FIXED: previously this created its OWN SignInputListener that was never
    // registered with the PluginManager, so sign input never reached the callback
    // and deposits/withdrawals silently did nothing. Now Main passes in the one
    // shared, registered instance.
    private final SignInputListener inputListener;

    public OnClickEvent(Main main, SignInputListener inputListener) {
        this.main = main;
        this.inputListener = inputListener;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // FIXED: cancel EVERY click while the bank GUI is open. The old code only
        // cancelled clicks inside the top inventory, so shift-clicks from the player
        // inventory moved items into the decorative GUI and voided them.
        if (!(event.getView().getTopInventory().getHolder() instanceof BankInterfaceInv)) return;
        event.setCancelled(true);

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        switch (event.getSlot()) {
            case 12 -> prompt(player, true);   // Einzahlen
            case 14 -> prompt(player, false);  // Auszahlen
            default -> { /* decorative slot */ }
        }
    }

    private void prompt(Player player, boolean deposit) {
        inputListener.promptForInteger(player, List.of("Bitte Betrag eintragen"), value -> {
            if (value <= 0) {
                player.sendMessage("Zahl muss positiv sein");
                return;
            }
            var data = main.getHandler().getPlayerDataHandler().getPlayerData(player);
            if (data == null) return;
            PlayerMoney money = data.getMoney();
            PlayerBank bank = data.getBank();
            if (money == null || bank == null) return;

            Money from = deposit ? money : bank;
            Money to = deposit ? bank : money;

            if (!from.hasMoney(value)) {
                player.sendMessage("Nicht genug Geld");
                return;
            }
            from.addMoney(-value);
            to.addMoney(value);
        });
    }
}
