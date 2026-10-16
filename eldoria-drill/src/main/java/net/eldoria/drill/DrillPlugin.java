package net.eldoria.drill;

import org.bukkit.plugin.java.JavaPlugin;

/** Standalone entry point. Not needed when the module is embedded in the main Eldoria plugin. */
public final class DrillPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        DrillModule.init(this);
    }
}
