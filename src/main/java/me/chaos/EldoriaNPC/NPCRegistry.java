package me.chaos.EldoriaNPC;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Loads and holds every NPC definition from {@code plugins/EldoriaBase/npcs.yml}.
 * Call {@link #reload(Plugin)} on startup and after {@code /npc reload}.
 */
public final class NPCRegistry {

    private static final String FILE_NAME = "npcs.yml";
    private static final Map<String, NPCDefinition> DEFINITIONS = new LinkedHashMap<>();

    private NPCRegistry() {}

    public static void reload(Plugin plugin) {
        DEFINITIONS.clear();

        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection defs = yaml.getConfigurationSection("definitions");
        if (defs == null) {
            plugin.getLogger().warning("[NPC] npcs.yml enthält keinen 'definitions'-Abschnitt.");
            return;
        }

        Logger log = plugin.getLogger();
        for (String id : defs.getKeys(false)) {
            ConfigurationSection section = defs.getConfigurationSection(id);
            if (section == null) continue;

            String name = section.getString("name", id);
            EntityType entityType = parseEntityType(section.getString("entity", "VILLAGER"), id, log);
            NPCDefinition.InteractionType interaction =
                    NPCDefinition.InteractionType.parse(section.getString("interaction"));

            String guiId = section.getString("gui", null);
            List<String> lines = section.getStringList("lines");

            // teleport sub-section
            ConfigurationSection tp = section.getConfigurationSection("teleport");
            String tpWorld = tp != null ? tp.getString("world", null) : null;
            boolean tpRandom = tp != null && tp.getBoolean("random", false);
            int tpRadius = tp != null ? tp.getInt("radius", 1000) : 1000;

            if (interaction == NPCDefinition.InteractionType.GUI && (guiId == null || guiId.isBlank())) {
                log.warning("[NPC] Definition '" + id + "' nutzt interaction GUI, aber kein 'gui:' gesetzt - übersprungen.");
                continue;
            }
            if (interaction == NPCDefinition.InteractionType.TELEPORT && (tpWorld == null || tpWorld.isBlank())) {
                log.warning("[NPC] Definition '" + id + "' nutzt interaction TELEPORT, aber kein 'teleport.world:' gesetzt - übersprungen.");
                continue;
            }

            DEFINITIONS.put(id, new NPCDefinition(id, name, entityType, interaction, guiId, lines,
                    tpWorld, tpRandom, tpRadius));
        }
        log.info("[NPC] " + DEFINITIONS.size() + " NPC-Definitionen geladen.");
    }

    private static EntityType parseEntityType(String raw, String id, Logger log) {
        try {
            return EntityType.valueOf(raw.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warning("[NPC] Unbekannter entity-Typ '" + raw + "' bei '" + id + "' - VILLAGER verwendet.");
            return EntityType.VILLAGER;
        }
    }

    public static Optional<NPCDefinition> get(String id) {
        return Optional.ofNullable(DEFINITIONS.get(id));
    }

    public static Collection<NPCDefinition> getAll() {
        return Collections.unmodifiableCollection(DEFINITIONS.values());
    }
}
