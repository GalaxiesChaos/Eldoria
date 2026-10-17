package me.chaos.EldoriaItems.Sets;

import java.util.List;

/**
 * A group of item ids (armor pieces and/or, later, artifacts) that grant {@link SetBonus}es
 * once enough of them are equipped/held at the same time. Membership isn't restricted to
 * armor - anything with a matching set id on its {@code CustomItemTemplate} counts.
 */
public record ItemSet(String id, String displayName, List<String> memberItemIds, List<SetBonus> bonuses) {

    /** The single highest-tier bonus currently unlocked for the given piece count, or null. */
    public SetBonus getHighestActiveBonus(int equippedPieces) {
        SetBonus best = null;
        for (SetBonus bonus : bonuses) {
            if (equippedPieces >= bonus.requiredPieces()) {
                if (best == null || bonus.requiredPieces() > best.requiredPieces()) {
                    best = bonus;
                }
            }
        }
        return best;
    }

    /** Every bonus tier currently unlocked for the given piece count (e.g. both 2pc and 4pc). */
    public List<SetBonus> getAllActiveBonuses(int equippedPieces) {
        return bonuses.stream().filter(b -> equippedPieces >= b.requiredPieces()).toList();
    }
}
