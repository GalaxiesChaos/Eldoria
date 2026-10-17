package me.chaos.EldoriaJewels;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.EldoriaCash.Money;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;

/** Digitales Juwelen-Konto eines Spielers. Wird wie PlayerMoney/PlayerBank in der Spieler-JSON gespeichert. */
public class PlayerJewels implements PlayerDataHolder, Money {

    public static final String SAVE_KEY = "PlayerJewels";

    private int jewels = 0;

    /** Für den Supplier in der PlayerDataRegistry (neuer Spieler). */
    public PlayerJewels() {
    }

    private PlayerJewels(int jewels) {
        this.jewels = Math.max(0, jewels);
    }

    @Override
    public int getMoney() {
        return jewels;
    }

    /** Negative Werte ziehen ab. Das Ergebnis wird auf 0..Integer.MAX_VALUE begrenzt (kein Overflow). */
    @Override
    public void addMoney(int amount) {
        long result = (long) jewels + amount;
        jewels = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, result));
    }

    @Override
    public boolean hasMoney(int amount) {
        return jewels >= amount;
    }

    public void setMoney(int amount) {
        jewels = Math.max(0, amount);
    }

    public static final Codec<PlayerJewels> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("Jewels").forGetter(PlayerJewels::getMoney)
    ).apply(instance, PlayerJewels::new));

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
