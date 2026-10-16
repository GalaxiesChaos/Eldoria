package me.chaos.EldoriaAntiDupe;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Reads antidupe.yml. Fields are public + volatile so /antidupe reload can swap values live. */
public final class AntiDupeConfig {

    public enum Mode { ENFORCE, LOG_ONLY }

    private static final String FILE_NAME = "antidupe.yml";

    private final JavaPlugin plugin;
    private final File file;

    public volatile boolean enabled;
    public volatile Mode mode;
    public volatile long scanIntervalTicks;
    public volatile long scanMinIntervalMs;
    public volatile int maxRemovalsPerScan;
    public volatile boolean protectShulkers;
    public volatile boolean protectBundles;
    public volatile Set<Material> protectedMaterials = Set.of();
    public volatile List<String> protectedKeyPrefixes = List.of();
    public volatile boolean clickLimitEnabled;
    public volatile int maxClicksPerSecond;
    public volatile boolean portalBlock;
    public volatile boolean guiGuard;
    public volatile boolean creativeResetSerial;
    public volatile boolean auditPaperConfig;
    public volatile String alertPermission;
    public volatile String bypassPermission;

    public AntiDupeConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            plugin.saveResource(FILE_NAME, false);
        }
        reload();
    }

    public boolean enforce() {
        return mode == Mode.ENFORCE;
    }

    public void reload() {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        enabled = yaml.getBoolean("enabled", true);
        mode = parseMode(yaml.getString("mode", "ENFORCE"));

        scanIntervalTicks = Math.max(20L, yaml.getLong("scan.interval-ticks", 40L));
        scanMinIntervalMs = Math.max(250L, yaml.getLong("scan.min-interval-ms", 1000L));
        maxRemovalsPerScan = Math.max(1, yaml.getInt("scan.max-removals-per-scan", 20));

        protectShulkers = yaml.getBoolean("protected.shulker-boxes", true);
        protectBundles = yaml.getBoolean("protected.bundles", true);

        Set<Material> materials = EnumSet.noneOf(Material.class);
        for (String name : yaml.getStringList("protected.materials")) {
            Material material = Material.matchMaterial(name);
            if (material == null) {
                plugin.getLogger().warning("[AntiDupe] Unbekanntes Material in antidupe.yml: " + name);
            } else {
                materials.add(material);
            }
        }
        protectedMaterials = materials;

        List<String> prefixes = yaml.getStringList("protected.key-prefixes");
        protectedKeyPrefixes = prefixes.isEmpty()
                ? List.of("eldoriaenchants:", "eldoriabase:stat_", "eldoriabase:serial")
                : List.copyOf(prefixes);

        clickLimitEnabled = yaml.getBoolean("click-limit.enabled", true);
        maxClicksPerSecond = Math.max(5, yaml.getInt("click-limit.max-per-second", 30));
        portalBlock = yaml.getBoolean("portal.block-container-entities", true);
        guiGuard = yaml.getBoolean("gui-guard", true);
        creativeResetSerial = yaml.getBoolean("creative-reset-serial", true);
        auditPaperConfig = yaml.getBoolean("audit-paper-config", true);

        alertPermission = yaml.getString("permissions.alert", "eldoria.antidupe.alert");
        bypassPermission = yaml.getString("permissions.bypass-click-limit", "eldoria.antidupe.bypass");
    }

    private Mode parseMode(String raw) {
        try {
            return Mode.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("[AntiDupe] Ungueltiger Modus '" + raw + "' - nutze LOG_ONLY.");
            return Mode.LOG_ONLY;
        }
    }
}
