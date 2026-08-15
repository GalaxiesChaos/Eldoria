package me.chaos.eldoriaBase;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import org.jspecify.annotations.NonNull;

public class MainLoader implements PluginLoader {

    @Override
    public void classloader (final @NonNull PluginClasspathBuilder builder) {
        // Add dynamically loaded libraries here
    }
}
