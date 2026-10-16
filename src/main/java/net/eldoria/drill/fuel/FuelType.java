package net.eldoria.drill.fuel;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

/** A kind of fuel: how to recognise the item and how much fuel one item is worth. */
public final class FuelType {

    private final String id;
    private final String displayName;
    private final Predicate<ItemStack> matcher;
    private final double fuelValue;

    public FuelType(String id, String displayName, Predicate<ItemStack> matcher, double fuelValue) {
        this.id = id;
        this.displayName = displayName;
        this.matcher = matcher;
        this.fuelValue = fuelValue;
    }

    public static FuelType ofMaterial(String id, String displayName, Material material, double fuelValue) {
        return new FuelType(id, displayName, stack -> stack.getType() == material, fuelValue);
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public double fuelValue() {
        return fuelValue;
    }

    public boolean matches(ItemStack stack) {
        return matcher.test(stack);
    }
}
