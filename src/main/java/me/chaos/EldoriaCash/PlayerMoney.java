package me.chaos.EldoriaCash;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import org.bukkit.entity.Player;

public class PlayerMoney implements PlayerDataHolder, Money {
    private int money = 0;
    public static String SAVE_KEY = "PlayerMoney";

    public PlayerMoney(Main main, Player player){
        append(main, player);
    }

    public PlayerMoney(Main main, Player player, int cash){
        append(main, player);
        money = cash;
    }

    public PlayerMoney(Integer cash) {
        this.money = cash;
    }

    public PlayerMoney() {

    }

    @Override
    public int getMoney() {
        return money;
    }

    @Override
    public void addMoney(int money) {
        this.money += money;
    }

    @Override
    public boolean hasMoney(int money) {
        return this.money >= money;
    }



    public static Codec<PlayerMoney> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("Money").forGetter(PlayerMoney::getMoney)
    ).apply(instance, PlayerMoney::new));

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
