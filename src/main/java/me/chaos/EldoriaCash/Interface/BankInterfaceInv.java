package me.chaos.EldoriaCash.Interface;

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

import java.util.Collections;


public class BankInterfaceInv implements InventoryHolder {
    private final Inventory BankInterface;

    public BankInterfaceInv(){
        this.BankInterface = Bukkit.createInventory(this, 27, Component.text("Bank"));
        GuiInterface.fillInv(BankInterface);

        //Eingabe Block
        ItemStack Ein = ItemStack.of(Material.GOLD_BLOCK);
        ItemMeta meta = Ein.getItemMeta();
        meta.displayName(Component.text("Einzahlen").color(NamedTextColor.GREEN));
        Ein.setItemMeta(meta);

        //Ausgabe block
        ItemStack aus = ItemStack.of(Material.DISPENSER);
        ItemMeta meta1 = aus.getItemMeta();

        meta1.displayName(Component.text("Auszahlen").color(NamedTextColor.DARK_RED));
        aus.setItemMeta(meta1);

        //Zinsblock
        ItemStack zins = ItemStack.of(Material.GOLD_NUGGET);
        ItemMeta ZinsMeta = zins.getItemMeta();
        ZinsMeta.displayName(Component.text("Zinsen").color(NamedTextColor.GOLD));
        ZinsMeta.lore(Collections.singletonList(Component.text("WIP").color(NamedTextColor.DARK_PURPLE)));
        zins.setItemMeta(ZinsMeta);

        BankInterface.setItem(12,Ein);
        BankInterface.setItem(14,aus);
        BankInterface.setItem(26,zins);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return BankInterface;
    }
}
