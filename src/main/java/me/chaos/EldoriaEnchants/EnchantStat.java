package me.chaos.EldoriaEnchants;

import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;

/**
 * Every stat a StatEnchant is allowed to boost, mapped to the real vanilla Attribute
 * it drives and the slot group that attribute should apply from (mainhand weapons vs.
 * worn armor). Add a new constant here to expose a new stat to enchants.
 */
public enum EnchantStat {
    ATTACK_DAMAGE(Attribute.ATTACK_DAMAGE, EquipmentSlotGroup.MAINHAND),
    ATTACK_SPEED(Attribute.ATTACK_SPEED, EquipmentSlotGroup.MAINHAND),
    MAX_HEALTH(Attribute.MAX_HEALTH, EquipmentSlotGroup.ARMOR),
    MOVEMENT_SPEED(Attribute.MOVEMENT_SPEED, EquipmentSlotGroup.ARMOR),
    ARMOR(Attribute.ARMOR, EquipmentSlotGroup.ARMOR),
    ARMOR_TOUGHNESS(Attribute.ARMOR_TOUGHNESS, EquipmentSlotGroup.ARMOR),
    KNOCKBACK_RESISTANCE(Attribute.KNOCKBACK_RESISTANCE, EquipmentSlotGroup.ARMOR),
    LUCK(Attribute.LUCK, EquipmentSlotGroup.ANY);

    private final Attribute attribute;
    private final EquipmentSlotGroup slotGroup;

    EnchantStat(Attribute attribute, EquipmentSlotGroup slotGroup) {
        this.attribute = attribute;
        this.slotGroup = slotGroup;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public EquipmentSlotGroup getSlotGroup() {
        return slotGroup;
    }
}
