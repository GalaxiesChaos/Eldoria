package me.chaos.EldoriaForge.recipe;

import me.chaos.EldoriaDrill.data.DrillItems;
import me.chaos.EldoriaItems.Item.CustomItemService;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** One required input of a forge recipe: an item template plus the amount needed. */
public final class ForgeIngredient {
    private final String item;   // vanilla material name or "nexo:<id>"
    private final int amount;

    public ForgeIngredient(String item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public String item() {
        return item;
    }

    public int amount() {
        return amount;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        if (ForgeItems.isNexo(item)) {
            String id = ForgeItems.nexoId(stack);
            return id != null && id.equalsIgnoreCase(item.substring(ForgeItems.NEXO_PREFIX.length()));
        }
        if (ForgeItems.isEldoria(item)) {
            String id = CustomItemService.getTemplateId(stack);
            return id != null && id.equalsIgnoreCase(item.substring(ForgeItems.ELDR_PREFIX.length()));
        }
        if (ForgeItems.isDrill(item)) {
            return DrillItems.upgradeOf(stack)
                    .map(upgrade -> upgrade.id().equalsIgnoreCase(item.substring(ForgeItems.DRILL_PREFIX.length())))
                    .orElse(false);
        }
        if (ForgeItems.isDrillBase(item)) {
            return DrillItems.read(stack)
                    .map(data -> data.baseId().equalsIgnoreCase(item.substring(ForgeItems.DRILLBASE_PREFIX.length())))
                    .orElse(false);
        }
        return stack.getType().name().equalsIgnoreCase(item);
    }

    /** Total amount of matching items across all stacks in the inventory. */
    public int countIn(Inventory inventory) {
        int count = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (matches(stack)) {
                count += stack.getAmount();
            }
        }
        return count;
    }

    /** Removes exactly {@link #amount()} matching items. Only call after {@link #countIn} verified them. */
    public void removeFrom(Inventory inventory) {
        int remaining = amount;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (!matches(stack)) {
                continue;
            }
            int take = Math.min(stack.getAmount(), remaining);
            stack.setAmount(stack.getAmount() - take);
            remaining -= take;
            if (stack.getAmount() <= 0) {
                contents[i] = null;
            }
        }
    }
}
