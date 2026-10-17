package me.chaos.EldoriaJewels;

import me.chaos.eldoriaBase.Handlers;
import me.chaos.eldoriaBase.PlayerData.PlayerData;
import org.bukkit.entity.Player;

/**
 * Einzige Schnittstelle für Juwelen-Änderungen (Shops, Quests, Admin-Befehle ...).
 * Alle Methoden liefern false, wenn die Aktion nicht möglich war (ungültiger Betrag,
 * zu wenig Juwelen, Spielerdaten nicht geladen).
 * Zugriff: main.getHandler().getJewelService()
 */
public class JewelService {

    private final Handlers handlers;

    public JewelService(Handlers handlers) {
        this.handlers = handlers;
    }

    private PlayerJewels account(Player player) {
        PlayerData data = handlers.getPlayerDataHandler().getPlayerData(player);
        return data == null ? null : data.getJewels();
    }

    public int getBalance(Player player) {
        PlayerJewels account = account(player);
        return account == null ? 0 : account.getMoney();
    }

    public boolean has(Player player, int amount) {
        PlayerJewels account = account(player);
        return account != null && account.hasMoney(amount);
    }

    public boolean add(Player player, int amount) {
        PlayerJewels account = account(player);
        if (account == null || amount <= 0) return false;
        account.addMoney(amount);
        return true;
    }

    public boolean remove(Player player, int amount) {
        PlayerJewels account = account(player);
        if (account == null || amount <= 0 || !account.hasMoney(amount)) return false;
        account.addMoney(-amount);
        return true;
    }

    public boolean set(Player player, int amount) {
        PlayerJewels account = account(player);
        if (account == null || amount < 0) return false;
        account.setMoney(amount);
        return true;
    }
}
