package me.chaos.EldoriaNPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Spawns, tracks and persists placed NPCs.
 * <p>
 * Spawned NPCs are tagged with a PDC key, so on startup they are first "adopted" from the
 * worlds (they survive restarts because they are persistent + invulnerable) and only missing
 * ones are re-spawned from {@code npcs-data.yml}. This avoids duplicates even after a crash.
 */
public final class NPCManager {

    private static final String DATA_FILE = "npcs-data.yml";

    private record SpawnedNPC(UUID entityId, String definitionId, Location location) {}

    private final Plugin plugin;
    private final NamespacedKey npcKey;
    private final Map<UUID, SpawnedNPC> spawned = new HashMap<>();

    public NPCManager(Plugin plugin) {
        this.plugin = plugin;
        this.npcKey = new NamespacedKey(plugin, "npc_id");
    }

    /** Adopt already-existing NPC entities from the worlds and respawn everything missing. Call delayed after enable. */
    public void restoreSpawned() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                String defId = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
                if (defId != null && NPCRegistry.get(defId).isPresent()) {
                    spawned.put(entity.getUniqueId(), new SpawnedNPC(entity.getUniqueId(), defId, entity.getLocation()));
                }
            }
        }

        File file = new File(plugin.getDataFolder(), DATA_FILE);
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        for (Map<?, ?> entry : yaml.getMapList("spawned")) {
            try {
                String defId = (String) entry.get("definition");
                World world = Bukkit.getWorld(UUID.fromString((String) entry.get("world")));
                if (world == null || NPCRegistry.get(defId).isEmpty()) continue;

                double x = ((Number) entry.get("x")).doubleValue();
                double y = ((Number) entry.get("y")).doubleValue();
                double z = ((Number) entry.get("z")).doubleValue();
                float yaw = ((Number) entry.get("yaw")).floatValue();

                Location loc = new Location(world, x, y, z, yaw, 0);
                boolean alreadyThere = spawned.values().stream()
                        .anyMatch(s -> s.location().getWorld() == world && s.location().distanceSquared(loc) < 1);
                if (!alreadyThere) {
                    NPCRegistry.get(defId).ifPresent(def -> spawn(def, loc));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[NPC] Konnte NPC-Eintrag nicht wiederherstellen: " + e.getMessage());
            }
        }
    }

    public Entity spawn(NPCDefinition definition, Location location) {
        Entity entity = location.getWorld().spawnEntity(location, definition.entityType());
        entity.getPersistentDataContainer().set(npcKey, PersistentDataType.STRING, definition.id());
        entity.setPersistent(true);
        entity.customName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacyAmpersand().deserialize(definition.displayName()));
        entity.setCustomNameVisible(true);

        if (entity instanceof LivingEntity living) {
            living.setAI(false);
            living.setSilent(true);
            living.setInvulnerable(true);
            living.setRemoveWhenFarAway(false);
            living.setCollidable(false);
        }

        spawned.put(entity.getUniqueId(), new SpawnedNPC(entity.getUniqueId(), definition.id(), entity.getLocation()));
        return entity;
    }

    /** Removes the nearest spawned entity of the given definition to {@code near}. 0 if none. */
    public int removeNearest(String definitionId, Location near) {
        SpawnedNPC nearest = null;
        double best = Double.MAX_VALUE;
        for (SpawnedNPC s : spawned.values()) {
            if (!s.definitionId().equals(definitionId)) continue;
            double dist = s.location().getWorld() == near.getWorld()
                    ? s.location().distanceSquared(near) : Double.MAX_VALUE;
            if (dist < best) { best = dist; nearest = s; }
        }
        if (nearest == null) return 0;

        Entity entity = Bukkit.getEntity(nearest.entityId());
        if (entity != null) entity.remove();
        spawned.remove(nearest.entityId());
        return 1;
    }

    public int removeAll(String definitionId) {
        int count = 0;
        Iterator<Map.Entry<UUID, SpawnedNPC>> it = spawned.entrySet().iterator();
        while (it.hasNext()) {
            SpawnedNPC s = it.next().getValue();
            if (!s.definitionId().equals(definitionId)) continue;
            Entity entity = Bukkit.getEntity(s.entityId());
            if (entity != null) { entity.remove(); count++; }
            it.remove();
        }
        return count;
    }

    public Map<String, Integer> getSpawnCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (SpawnedNPC s : spawned.values()) {
            counts.merge(s.definitionId(), 1, Integer::sum);
        }
        return counts;
    }

    public Optional<NPCDefinition> getDefinitionOf(Entity entity) {
        String defId = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
        return defId == null ? Optional.empty() : NPCRegistry.get(defId);
    }

    public void save() {
        File file = new File(plugin.getDataFolder(), DATA_FILE);
        YamlConfiguration yaml = new YamlConfiguration();
        List<Map<String, Object>> entries = new ArrayList<>();
        for (SpawnedNPC s : spawned.values()) {
            Location loc = s.location();
            if (loc.getWorld() == null) continue;
            Map<String, Object> entry = new HashMap<>();
            entry.put("definition", s.definitionId());
            entry.put("world", loc.getWorld().getUID().toString());
            entry.put("x", loc.getX());
            entry.put("y", loc.getY());
            entry.put("z", loc.getZ());
            entry.put("yaw", loc.getYaw());
            entries.add(entry);
        }
        yaml.set("spawned", entries);
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("[NPC] npcs-data.yml konnte nicht gespeichert werden: " + e.getMessage());
        }
    }
}
