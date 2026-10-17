package me.chaos.EldoriaNPC;

import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * Immutable definition of an interactable NPC, loaded from {@code npcs.yml}.
 *
 * @param id          unique definition id (config key), e.g. {@code quest_master}
 * @param displayName shown above the entity; supports legacy '&' color codes
 * @param entityType  bukkit entity type the NPC is spawned as (default VILLAGER)
 * @param interaction what happens on right-click:
 *                    DIALOGUE  -> sends {@link #lines} to the player
 *                    GUI       -> opens the menu registered under {@link #guiId}
 *                    TELEPORT  -> teleports the player ({@link #teleportWorld}, random or fixed)
 * @param guiId       id inside {@link GuiOpenerRegistry} (only for GUI)
 * @param lines       dialogue lines sent to the player; {@code %player%} is replaced by the
 *                    player's name (only for DIALOGUE)
 * @param teleportWorld multiverse/bukkit world name (only for TELEPORT)
 * @param teleportRandom if true, a random safe location inside {@link #teleportRadius} is chosen,
 *                    otherwise the world spawn is used (only for TELEPORT)
 * @param teleportRadius max distance from world spawn for random teleports (only for TELEPORT)
 */
public record NPCDefinition(
        String id,
        String displayName,
        EntityType entityType,
        InteractionType interaction,
        String guiId,
        List<String> lines,
        String teleportWorld,
        boolean teleportRandom,
        int teleportRadius) {

    public enum InteractionType {
        DIALOGUE,
        GUI,
        TELEPORT;

        public static InteractionType parse(String raw) {
            if (raw == null) return DIALOGUE;
            try {
                return valueOf(raw.trim().toUpperCase(java.util.Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return DIALOGUE;
            }
        }
    }
}
