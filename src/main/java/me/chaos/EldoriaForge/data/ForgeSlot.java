package me.chaos.EldoriaForge.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.EldoriaForge.recipe.ForgeRecipe;
import me.chaos.EldoriaForge.recipe.ForgeRecipeRegistry;

import java.util.Optional;

/** One forge station slot of a player. Empty when recipeId is blank. */
public final class ForgeSlot {
    private String recipeId = "";
    private long startTime = 0L;

    public static final Codec<ForgeSlot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("recipeId", "").forGetter(ForgeSlot::recipeId),
            Codec.LONG.optionalFieldOf("startTime", 0L).forGetter(ForgeSlot::startTime)
    ).apply(instance, ForgeSlot::new));

    public ForgeSlot() {
    }

    private ForgeSlot(String recipeId, long startTime) {
        this.recipeId = recipeId;
        this.startTime = startTime;
    }

    public String recipeId() {
        return recipeId;
    }

    public long startTime() {
        return startTime;
    }

    public boolean isActive() {
        return !recipeId.isBlank();
    }

    public Optional<ForgeRecipe> recipe() {
        return isActive() ? ForgeRecipeRegistry.get(recipeId) : Optional.empty();
    }

    public void start(ForgeRecipe recipe) {
        this.recipeId = recipe.id();
        this.startTime = System.currentTimeMillis();
    }

    public void clear() {
        this.recipeId = "";
        this.startTime = 0L;
    }

    /** Progress survives relog/restart: measured from the persisted start timestamp. */
    public long elapsedSeconds() {
        if (!isActive()) {
            return 0;
        }
        return (System.currentTimeMillis() - startTime) / 1000L;
    }

    public boolean isDone(int timeSeconds) {
        return elapsedSeconds() >= timeSeconds;
    }

    public long remainingSeconds(int timeSeconds) {
        return Math.max(0, timeSeconds - elapsedSeconds());
    }
}
