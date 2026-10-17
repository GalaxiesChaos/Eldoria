package me.chaos.EldoriaItems.Sets;

import net.kyori.adventure.text.Component;

import java.util.List;

/** One "tier" of a set bonus, e.g. "2 pieces: +10 Strength", "4 pieces: chance to drop Steel". */
public record SetBonus(int requiredPieces, List<Component> descriptionLore, List<SetBonusEffect> effects) {
}
