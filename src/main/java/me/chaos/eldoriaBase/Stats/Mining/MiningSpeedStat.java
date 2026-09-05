package me.chaos.eldoriaBase.Stats.Mining;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.Stats.Stat;
import me.chaos.eldoriaBase.Stats.StatApplicability;
import me.chaos.eldoriaBase.Stats.StatValueType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.List;

/**
 * PER_UNIT stat: every point adds directly to the vanilla
 * {@link Attribute#BLOCK_BREAK_SPEED} attribute - but, per the applicability list,
 * only while the player is actually digging an ore or a raw/uncrafted stone block.
 * <p>
 * Since the vanilla attribute affects break speed for every block uniformly, the modifier
 * has to be toggled on/off rather than left applied permanently:
 * <ul>
 *     <li>{@link BlockDamageEvent} fires when the player starts damaging a (new) block ->
 *     apply the modifier if that block is a valid target, remove it otherwise.</li>
 *     <li>{@link BlockBreakEvent} fires once the block is actually broken -> remove the
 *     modifier again so it doesn't linger onto whatever the player breaks next.</li>
 * </ul>
 */
public class MiningSpeedStat extends Stat {

    public static final String ID = "mining_speed";

    private final Main main;
    private final NamespacedKey modifierKey;

    public MiningSpeedStat(Main main) {
        super(
                ID,
                "Abbaugeschwindigkeit",
                StatValueType.PER_UNIT,
                0.0,
                StatApplicability.builder().blocks(MiningCategories.ORES_AND_RAW_STONE).build(),
                List.of(BlockDamageEvent.class, BlockBreakEvent.class)
        );
        this.main = main;
        this.modifierKey = new NamespacedKey(main, "stat_" + ID);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof BlockDamageEvent damageEvent) {
            handleDamage(damageEvent.getPlayer(), damageEvent.getBlock().getType());
        } else if (event instanceof BlockBreakEvent breakEvent) {
            clearModifier(breakEvent.getPlayer());
        }
    }

    private void handleDamage(Player player, Material target) {
        AttributeInstance instance = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (instance == null) return;

        // Always clear the old value first - the player might have swapped gear, or be
        // targeting a different (non-applicable) block now.
        instance.removeModifier(modifierKey);

        if (!getApplicability().appliesToBlock(target)) {
            return;
        }

        double value = main.getHandler().getStatManager().getValue(player, this);
        if (value <= 0) return;

        instance.addTransientModifier(new AttributeModifier(modifierKey, value, AttributeModifier.Operation.ADD_NUMBER));
    }

    private void clearModifier(Player player) {
        AttributeInstance instance = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (instance != null) {
            instance.removeModifier(modifierKey);
        }
    }
}