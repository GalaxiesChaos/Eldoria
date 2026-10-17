package me.chaos.EldoriaForge.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.util.Text;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-player forge stations, persisted via the shared PlayerData codec system.
 * Default: 16 slots; more can be unlocked later via {@link #addSlot(Player)} (e.g. upgrades).
 */
public final class PlayerForgeData implements PlayerDataHolder {
    public static final String SAVE_KEY = "PlayerForgeData";
    public static final int DEFAULT_SLOTS = 16;

    private final List<ForgeSlot> slots = new ArrayList<>();
    private int unlockedSlots;

    /** No-arg constructor used by PlayerDataRegistry suppliers. */
    public PlayerForgeData() {
        for (int i = 0; i < DEFAULT_SLOTS; i++) {
            slots.add(new ForgeSlot());
        }
        this.unlockedSlots = DEFAULT_SLOTS;
    }

    public PlayerForgeData(Main main, Player player) {
        this();
        append(main, player);
    }

    /** Codec constructor. */
    private PlayerForgeData(List<ForgeSlot> slots, int unlockedSlots) {
        this.slots.addAll(slots);
        this.unlockedSlots = unlockedSlots;
    }

    public static final Codec<PlayerForgeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeSlot.CODEC.listOf().fieldOf("Slots").forGetter(PlayerForgeData::slots),
            Codec.INT.optionalFieldOf("UnlockedSlots", DEFAULT_SLOTS).forGetter(PlayerForgeData::unlockedSlots)
    ).apply(instance, PlayerForgeData::new));

    private List<ForgeSlot> slots() {
        return slots;
    }

    /** Slot i, or null if out of range. Pads the list when the default slot count was raised later. */
    public ForgeSlot slot(int index) {
        while (slots.size() < DEFAULT_SLOTS) {
            slots.add(new ForgeSlot());
        }
        if (index < 0 || index >= slots.size()) {
            return null;
        }
        return slots.get(index);
    }

    public int totalSlots() {
        return slots.size();
    }

    public int unlockedSlots() {
        return unlockedSlots;
    }

    public boolean isUnlocked(int index) {
        return index >= 0 && index < unlockedSlots;
    }

    /** Unlocks one additional slot (up to the total). Used by future slot upgrades. */
    public boolean addSlot(Player player) {
        if (unlockedSlots >= slots.size()) {
            return false;
        }
        unlockedSlots++;
        player.sendMessage(Text.plain("Neuer Schmiedeplatz freigeschaltet! (" + unlockedSlots + "/" + slots.size() + ")", NamedTextColor.GREEN));
        return true;
    }

    /** Consumes the recipe materials and starts forging in the given slot. */
    public boolean startForging(int slotIndex, ForgeRecipe recipe, Player player) {
        ForgeSlot slot = slot(slotIndex);
        if (slot == null || slot.isActive() || !isUnlocked(slotIndex)) {
            return false;
        }
        if (!recipe.consumeFrom(player)) {
            return false;
        }
        slot.start(recipe);
        return true;
    }

    /**
     * Hands out the finished item and frees the slot.
     * Returns the produced stack, or null when nothing was ready (slot empty/still forging)
     * or the recipe no longer exists (the slot is cleaned up in that case).
     */
    public ItemStack collect(int slotIndex, Player player) {
        ForgeSlot slot = slot(slotIndex);
        if (slot == null || !slot.isActive()) {
            return null;
        }
        ForgeRecipe recipe = slot.recipe().orElse(null);
        if (recipe == null) {
            slot.clear(); // recipe was removed from the registry — avoid soft-locking the slot
            return null;
        }
        if (!slot.isDone(recipe.timeSeconds())) {
            return null;
        }
        ItemStack result = recipe.outputItemStack();
        slot.clear();
        return result;
    }

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
