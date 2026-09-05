package me.chaos.eldoriaBase.Stats;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.PlayerData.PlayerDataHolder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds stat bonuses granted to a player OUTSIDE of equipment - e.g. through a future
 * stat-allocation menu. Armor- and held-item-based bonuses are read live off the items
 * themselves (see {@link StatItemUtil}) and are intentionally NOT stored here, so gear
 * can be re-forged/traded without touching player data.
 */
public class PlayerStatData implements PlayerDataHolder {

    public static final String SAVE_KEY = "PlayerStats";

    private Map<String, Double> bonusStats = new HashMap<>();

    public PlayerStatData() {
    }

    public PlayerStatData(Main main, Player player) {
        append(main, player);
    }

    private PlayerStatData(Map<String, Double> bonusStats) {
        this.bonusStats = new HashMap<>(bonusStats);
    }

    public double getBonus(String statId) {
        return bonusStats.getOrDefault(statId, 0.0);
    }

    public double getBonus(Stat stat) {
        return getBonus(stat.getId());
    }

    /** Adds (or, with a negative amount, removes) a permanent bonus - e.g. from a future menu purchase. */
    public void addBonus(String statId, double amount) {
        bonusStats.merge(statId, amount, Double::sum);
    }

    public void addBonus(Stat stat, double amount) {
        addBonus(stat.getId(), amount);
    }

    private Map<String, Double> getBonusStats() {
        return bonusStats;
    }

    public static final Codec<PlayerStatData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("Stats").forGetter(PlayerStatData::getBonusStats)
    ).apply(instance, PlayerStatData::new));

    @Override
    public JsonElement getData() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow();
    }

    @Override
    public String getSaveKey() {
        return SAVE_KEY;
    }
}