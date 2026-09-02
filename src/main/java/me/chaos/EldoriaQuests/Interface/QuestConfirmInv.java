package me.chaos.EldoriaQuests.Interface;

import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.eldoriaBase.Utils.GuiBuilder.GuiInterface;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class QuestConfirmInv implements InventoryHolder {

    public static final int ACCEPT_SLOT = 12;
    public static final int REROLL_SLOT = 14;

    private final Inventory inventory;
    private final Quest quest;
    private final QuestSelectionInv origin;
    private final int originSlot;

    public QuestConfirmInv(Quest quest, QuestSelectionInv origin, int originSlot) {
        this.quest = quest;
        this.origin = origin;
        this.originSlot = originSlot;

        this.inventory = Bukkit.createInventory(this, 27, Component.text("Quest bestätigen"));
        GuiInterface.fillInv(inventory);

        ItemStack accept = ItemStack.of(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta acceptMeta = accept.getItemMeta();
        acceptMeta.displayName(Component.text("Annehmen").color(NamedTextColor.GREEN));
        accept.setItemMeta(acceptMeta);

        ItemStack reroll = ItemStack.of(Material.RED_STAINED_GLASS_PANE);
        ItemMeta rerollMeta = reroll.getItemMeta();
        rerollMeta.displayName(Component.text("Neu würfeln").color(NamedTextColor.DARK_RED));
        reroll.setItemMeta(rerollMeta);

        inventory.setItem(ACCEPT_SLOT, accept);
        inventory.setItem(REROLL_SLOT, reroll);
    }

    public Quest getQuest() {
        return quest;
    }

    public QuestSelectionInv getOrigin() {
        return origin;
    }

    public int getOriginSlot() {
        return originSlot;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}