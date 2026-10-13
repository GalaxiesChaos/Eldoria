package me.chaos.eldoriaBase.WarpCommand;

import me.chaos.eldoriaBase.Main;
import me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn.NotEnoughWarpsExeption;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarpHandler {
    // FIXED: keyed by Player the map leaked Player objects and accumulated stale
    // duplicate managers across rejoins. UUID keys + unregister on quit solve it.
    private final Map<UUID, WarpPlayerManager> warpMap = new HashMap<>();

    public void registerPlayer(Player player, List<WarpPoint> warps, Main main) {
        WarpPlayerManager manager = new WarpPlayerManager(player, main);
        for (WarpPoint warp : warps) {
            try {
                manager.addWarp(warp);
            } catch (NotEnoughWarpsExeption e) {
                player.sendMessage(e.getMessage());
            }
        }
        registerPlayer(player, manager);
    }

    public void registerPlayer(Player player, WarpPlayerManager manager) {
        warpMap.put(player.getUniqueId(), manager);
        loadPublic(player);
    }

    public void unregister(Player player) {
        warpMap.remove(player.getUniqueId());
    }

    public WarpPlayerManager getWarpmanager(Player player) {
        return warpMap.get(player.getUniqueId());
    }

    public boolean isEmpty(Player player) {
        WarpPlayerManager manager = warpMap.get(player.getUniqueId());
        return manager == null || manager.isEmpty();
    }

    public boolean containsWarp(String ID, Player player) {
        WarpPlayerManager manager = warpMap.get(player.getUniqueId());
        return manager != null && manager.hasWarp(ID);
    }

    public void loadPublic(Player player) {
        WarpPlayerManager manager = warpMap.get(player.getUniqueId());
        if (manager == null) return;
        // FIXED: filter out already-known ids, otherwise every rejoin duplicated
        // the public warps inside the player's saved warp list
        for (WarpPoint warp : new PublicWarp().getPublicWarps()) {
            if (!manager.hasWarp(warp.getId())) {
                manager.addPublicWarp(warp);
            }
        }
    }

    public void reloadPublic() {
        List<WarpPoint> publicWarps = new PublicWarp().getPublicWarps();
        for (WarpPlayerManager manager : warpMap.values()) {
            publicWarps.stream()
                    .filter(w -> !manager.hasWarp(w.getId()))
                    .forEach(manager::addPublicWarp);
        }
    }

    public List<String> getIds(Player player) {
        WarpPlayerManager manager = warpMap.get(player.getUniqueId());
        return manager == null ? List.of() : manager.getIds();
    }
}
