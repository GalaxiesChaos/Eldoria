package me.chaos.EldoriaQuests.Interface;

import me.chaos.EldoriaQuests.QuestExeptions.DefaultQuestExeption;
import me.chaos.EldoriaQuests.Quest.Quest;
import me.chaos.EldoriaQuests.QuestRegistry;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestSelectionInv implements InventoryHolder {

    public static final int[] QUEST_SLOTS = {11, 13, 15};

    private final Inventory inventory;
    private final Map<Integer, Quest> slotQuests = new HashMap<>();

    public QuestSelectionInv() throws DefaultQuestExeption {
        this.inventory = Bukkit.createInventory(this, 27, Component.text("Quests"));
        GuiInterface.fillInv(inventory);

        List<String> chosenIds = new ArrayList<>();
        for (int slot : QUEST_SLOTS) {
            Quest quest = QuestRegistry.getRandomQuest(chosenIds);
            chosenIds.add(quest.getId());
            placeQuest(slot, quest);
        }
    }

    public void placeQuest(int slot, Quest quest) {
        slotQuests.put(slot, quest);
        inventory.setItem(slot, buildBookItem(quest));
    }

    public void markAccepted(int slot, Quest quest) {
        slotQuests.remove(slot);
        inventory.setItem(slot, buildAcceptedItem(quest));
    }

    public Quest getQuest(int slot) {
        return slotQuests.get(slot);
    }

    public List<String> getCurrentQuestIds() {
        return slotQuests.values().stream().map(Quest::getId).toList();
    }

    private ItemStack buildBookItem(Quest quest) {
        ItemStack book = ItemStack.of(Material.WRITABLE_BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.displayName(Component.text(quest.getTitle()).color(NamedTextColor.YELLOW));
        meta.lore(quest.getDescription().stream()
                .map(line -> Component.text(line).color(NamedTextColor.GRAY))
                .toList());
        book.setItemMeta(meta);
        return book;
    }

    private ItemStack buildAcceptedItem(Quest quest) {
        ItemStack book = ItemStack.of(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        meta.displayName(Component.text(quest.getTitle()).color(NamedTextColor.LIGHT_PURPLE));
        meta.lore(quest.getDescription().stream()
                .map(line -> Component.text(line).color(NamedTextColor.GRAY))
                .toList());
        meta.setEnchantmentGlintOverride(true); // pure visual glint, no real enchant
        book.setItemMeta(meta);
        return book;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}