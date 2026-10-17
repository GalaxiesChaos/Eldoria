package me.chaos.EldoriaNPC;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Maps the {@code gui: <id>} value of an NPC definition to the code that opens that menu.
 * Register additional windows here (e.g. a shop menu) - the config file never references classes.
 */
public final class GuiOpenerRegistry {

    private static final Map<String, Consumer<Player>> OPENERS = new HashMap<>();

    private GuiOpenerRegistry() {}

    public static void register(String id, Consumer<Player> opener) {
        OPENERS.put(id.toLowerCase(java.util.Locale.ROOT), opener);
    }

    public static boolean open(Player player, String guiId) {
        if (guiId == null) return false;
        Consumer<Player> opener = OPENERS.get(guiId.toLowerCase(java.util.Locale.ROOT));
        if (opener == null) return false;
        opener.accept(player);
        return true;
    }

    public static Set<String> getGuiIds() {
        return Set.copyOf(OPENERS.keySet());
    }
}
