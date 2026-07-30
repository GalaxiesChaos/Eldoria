package me.chaos.EldoriaCash;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import org.bukkit.entity.Player;


public class PlayerBank implements PlayerDataHolder, Money {
    private int cash;
    public static String SAVE_KEY = "Bank";


    private PlayerBank(int money){
        cash = money;
    }

    public PlayerBank(Main main, Player player, int money){
        append(main,player);
        cash = money;
    }

    public PlayerBank() {

    }

    @Override
    public int getMoney() {
        return cash;
    }

    @Override
    public void addMoney(int money) {
        cash += money;
    }

    @Override
    public boolean hasMoney(int money) {
        return cash >= money;
    }

    public final static Codec<PlayerBank> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("Money").forGetter(PlayerBank::getMoney)
    ).apply(instance,PlayerBank::new));

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}
