package me.chaos.EldoriaAntiDupe.Audit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Many classic dupes (TNT/carpet/rail via pistons, headless pistons, end portal, ...) are patched
 * in Paper but can be re-enabled through "unsupported-settings". This warns at startup if any of
 * those switches is on. Missing files/keys are ignored, so a changed file layout can't break it.
 */
public final class PaperConfigAuditor {

    private static final List<String> EXPLOIT_FLAGS = List.of(
            "allow-piston-duplication",
            "allow-headless-pistons",
            "allow-permanent-block-break-exploits",
            "allow-unsafe-end-portal-teleportation",
            "allow-grindstone-overstacking",
            "allow-tripwire-disarming-exploits"
    );

    private final JavaPlugin plugin;

    public PaperConfigAuditor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run() {
        List<File> files = new ArrayList<>();
        files.add(new File("config/paper-global.yml"));
        files.add(new File("config/paper-world-defaults.yml"));
        for (World world : Bukkit.getWorlds()) {
            files.add(new File(world.getWorldFolder(), "paper-world.yml"));
        }

        List<String> findings = new ArrayList<>();
        for (File file : files) {
            if (!file.isFile()) continue;
            ConfigurationSection section = YamlConfiguration.loadConfiguration(file)
                    .getConfigurationSection("unsupported-settings");
            if (section == null) continue;

            for (String flag : EXPLOIT_FLAGS) {
                if (section.getBoolean(flag, false)) {
                    findings.add(file.getPath() + " -> unsupported-settings." + flag + ": true");
                }
            }
        }

        if (findings.isEmpty()) {
            plugin.getLogger().info("[AntiDupe] Paper-Config-Audit: keine aktivierten Dupe-Schalter gefunden.");
            return;
        }
        plugin.getLogger().warning("[AntiDupe] Paper-Config-Audit: Dupe-Schalter aktiv! Auf false setzen:");
        for (String finding : findings) {
            plugin.getLogger().warning("[AntiDupe]   " + finding);
        }
    }
}
