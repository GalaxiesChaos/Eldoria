package me.chaos.EldoriaAntiDupe;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.chaos.EldoriaAntiDupe.Alert.DupeAlert;
import me.chaos.EldoriaAntiDupe.Audit.PaperConfigAuditor;
import me.chaos.EldoriaAntiDupe.Guards.GuiExploitListener;
import me.chaos.EldoriaAntiDupe.Guards.InventoryAbuseListener;
import me.chaos.EldoriaAntiDupe.Guards.ItemEventListener;
import me.chaos.EldoriaAntiDupe.Guards.PortalDupeListener;
import me.chaos.EldoriaAntiDupe.Serial.DupeScanner;
import me.chaos.EldoriaAntiDupe.Serial.ItemSerial;
import me.chaos.EldoriaAntiDupe.Serial.ProtectedItemPolicy;
import me.chaos.EldoriaAntiDupe.Serial.SerialRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Entry point of the anti-dupe system. Call {@link #init(JavaPlugin)} once at the end of
 * {@code Main#onEnable()} and {@link #shutdown()} from {@code Main#onDisable()}.
 */
public final class AntiDupeModule {

    private static AntiDupeModule instance;

    private final JavaPlugin plugin;
    private SerialRegistry registry;

    private AntiDupeModule(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void init(JavaPlugin plugin) {
        instance = new AntiDupeModule(plugin);
        instance.enable();
    }

    public static void shutdown() {
        if (instance != null && instance.registry != null) {
            instance.registry.saveNow();
        }
    }

    private void enable() {
        AntiDupeConfig config = new AntiDupeConfig(plugin);
        if (!config.enabled) {
            plugin.getLogger().info("[AntiDupe] deaktiviert (antidupe.yml).");
            return;
        }

        ItemSerial.init(plugin);

        registry = new SerialRegistry(plugin);
        registry.load();

        ProtectedItemPolicy policy = new ProtectedItemPolicy(config);
        DupeAlert alert = new DupeAlert(plugin, config);
        DupeScanner scanner = new DupeScanner(plugin, config, registry, policy, alert);

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new ItemEventListener(config, registry, policy, scanner, alert), plugin);
        pm.registerEvents(new GuiExploitListener(config), plugin);
        pm.registerEvents(new InventoryAbuseListener(plugin, config, alert), plugin);
        pm.registerEvents(new PortalDupeListener(config, alert), plugin);

        AntiDupeCommand command = new AntiDupeCommand(config, registry, scanner);
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register("antidupe", "Anti-Dupe Verwaltung", command));

        scanner.start();
        Bukkit.getScheduler().runTaskTimer(plugin, registry::saveAsync, 20L * 60, 20L * 60 * 5);

        if (config.auditPaperConfig) {
            new PaperConfigAuditor(plugin).run();
        }

        plugin.getLogger().info("[AntiDupe] aktiv (Modus: " + config.mode + ").");
    }
}
