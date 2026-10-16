package me.chaos.EldoriaAntiDupe.Serial;

import me.chaos.EldoriaAntiDupe.Alert.DupeAlert;
import me.chaos.EldoriaAntiDupe.AntiDupeConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Heart of the anti-dupe system. Every scan runs inside ONE tick on the main thread, so the world
 * is in a consistent state: an item with a given serial number can only legitimately exist once.
 * <p>
 * What a scan does:
 * <ol>
 *     <li>Stamps protected items without a serial (see {@link ProtectedItemPolicy}).</li>
 *     <li>Deletes items whose serial was revoked earlier (leftover copies of a past dupe).</li>
 *     <li>Finds serials that appear more than once. The copy in the most "trusted" place is kept and
 *     gets a NEW serial, all other copies are removed (quarantined), and the old serial is revoked -
 *     so copies hidden in unloaded chests / offline inventories die as soon as they show up.</li>
 * </ol>
 * Scanned: online players (inventory, ender chest, cursor), containers currently opened by players,
 * and all loaded item entities. Shulker boxes and bundles are searched recursively.
 */
public final class DupeScanner {

    private static final int MAX_NEST_DEPTH = 3;
    private static final long ALERT_COOLDOWN_MS = 5 * 60_000L;

    public enum Source {
        PLAYER(0), CURSOR(0), ENDER_CHEST(1), CONTAINER(2), ENTITY(3);

        private final int rank;

        Source(int rank) {
            this.rank = rank;
        }
    }

    public record Result(int items, int duplicates, int revokedRemoved, int stamped, long millis) {}

    private enum Verdict { KEEP, STAMPED, REMOVE, REISSUE }

    private record SlotRef(Supplier<ItemStack> getter, Consumer<ItemStack> setter) {}

    private record Top(String key, SlotRef ref, Source source, String holderKey, String label) {}

    private record Observation(UUID serial, String path, String type, Top top) {}

    private record Decision(UUID serial, Verdict verdict, String label) {}

    private record Planned(UUID serial, Observation keeper, List<Observation> copies, List<Observation> all) {}

    @FunctionalInterface
    private interface Visitor {
        Verdict visit(ItemStack item, String path);
    }

    private final JavaPlugin plugin;
    private final AntiDupeConfig config;
    private final SerialRegistry registry;
    private final ProtectedItemPolicy policy;
    private final DupeAlert alert;

    private final Map<UUID, Long> alertedAt = new HashMap<>();
    private long lastScanMs;
    private boolean scanQueued;
    private Result lastResult;

    public DupeScanner(JavaPlugin plugin, AntiDupeConfig config, SerialRegistry registry,
                       ProtectedItemPolicy policy, DupeAlert alert) {
        this.plugin = plugin;
        this.config = config;
        this.registry = registry;
        this.policy = policy;
        this.alert = alert;
    }

    // ------------------------------------------------------------------ scheduling

    public void start() {
        long interval = Math.max(20L, config.scanIntervalTicks);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                scanNow();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "[AntiDupe] Scan fehlgeschlagen", e);
            }
        }, interval, interval);
    }

    /** Queues a scan for 2 ticks from now (throttled by scan.min-interval-ms). Safe to call often. */
    public void requestScan() {
        if (scanQueued) return;
        scanQueued = true;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            scanQueued = false;
            if (System.currentTimeMillis() - lastScanMs < config.scanMinIntervalMs) return;
            try {
                scanNow();
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "[AntiDupe] Scan fehlgeschlagen", e);
            }
        }, 2L);
    }

    public Result getLastResult() {
        return lastResult;
    }

    /** Stamps the protected items of one player right now (used before death drops are computed). */
    public void stampPlayer(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType().isAir()) continue;
            if (ItemSerial.read(item) == null && policy.isProtected(item)) {
                ItemSerial.assign(item);
                inv.setItem(i, item);
            }
        }
    }

    /** Runs a full scan immediately. Must be called on the main thread. */
    public Result scanNow() {
        long start = System.nanoTime();
        lastScanMs = System.currentTimeMillis();
        pruneAlerts();

        Pass pass = new Pass();
        for (Player player : Bukkit.getOnlinePlayers()) {
            pass.addPlayer(player);
        }
        for (World world : Bukkit.getWorlds()) {
            for (Item item : world.getEntitiesByClass(Item.class)) {
                pass.addItemEntity(item);
            }
        }
        lastResult = pass.finish(start);
        return lastResult;
    }

    private void pruneAlerts() {
        if (alertedAt.size() < 500) return;
        long now = System.currentTimeMillis();
        alertedAt.values().removeIf(time -> now - time > ALERT_COOLDOWN_MS);
    }

    // ------------------------------------------------------------------ recursive walker

    /**
     * Walks an item and everything nested in it (shulker boxes, bundles). Returns the item that
     * should be put back (null = remove it). {@code changed[0]} is set when anything was altered.
     * Paths are deterministic ("topKey/slot/slot"), so a second walk can address the same items.
     */
    private ItemStack rewrite(ItemStack item, String path, int depth, Visitor visitor, boolean[] changed) {
        if (item == null || item.getType().isAir()) return item;

        Verdict verdict = visitor.visit(item, path);
        switch (verdict) {
            case REMOVE -> {
                changed[0] = true;
                return null;
            }
            case REISSUE -> {
                ItemSerial.assign(item);
                changed[0] = true;
            }
            case STAMPED -> changed[0] = true;
            case KEEP -> {
                // untouched
            }
        }

        if (depth >= MAX_NEST_DEPTH || !item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta bsm && bsm.hasBlockState()) {
            if (bsm.getBlockState() instanceof Container container) {
                Inventory inv = container.getSnapshotInventory();
                boolean dirty = false;
                for (int i = 0; i < inv.getSize(); i++) {
                    ItemStack inner = inv.getItem(i);
                    if (inner == null || inner.getType().isAir()) continue;
                    boolean[] nested = {false};
                    ItemStack result = rewrite(inner, path + "/" + i, depth + 1, visitor, nested);
                    if (nested[0]) {
                        inv.setItem(i, result);
                        dirty = true;
                    }
                }
                if (dirty) {
                    bsm.setBlockState(container);
                    item.setItemMeta(bsm);
                    changed[0] = true;
                }
            }
        } else if (meta instanceof BundleMeta bundle && bundle.hasItems()) {
            List<ItemStack> items = new ArrayList<>(bundle.getItems());
            boolean dirty = false;
            for (int i = items.size() - 1; i >= 0; i--) { // reverse: removals don't shift lower indexes
                boolean[] nested = {false};
                ItemStack result = rewrite(items.get(i), path + "/" + i, depth + 1, visitor, nested);
                if (nested[0]) {
                    dirty = true;
                    if (result == null) {
                        items.remove(i);
                    } else {
                        items.set(i, result);
                    }
                }
            }
            if (dirty) {
                bundle.setItems(items);
                item.setItemMeta(bundle);
                changed[0] = true;
            }
        }
        return item;
    }

    // ------------------------------------------------------------------ helpers

    private static boolean isScannable(ItemStack item) {
        return item != null && !item.getType().isAir() && item.getAmount() == 1;
    }

    private static boolean isScannableContainer(Inventory inv) {
        return switch (inv.getType()) {
            case CHEST, BARREL, SHULKER_BOX, HOPPER, DROPPER, DISPENSER -> true;
            default -> false;
        };
    }

    /** Stable identity of a physical container, or null if it can't be identified safely (then it's skipped). */
    private static String containerKey(Inventory inv) {
        InventoryHolder holder = inv.getHolder(false);
        if (holder instanceof Entity entity) {
            return "entity:" + entity.getUniqueId();
        }

        Location loc;
        if (holder instanceof DoubleChest chest) {
            loc = chest.getLocation();
        } else if (holder instanceof BlockState state) {
            loc = state.getLocation();
        } else {
            loc = inv.getLocation();
        }
        if (loc == null || loc.getWorld() == null) return null;

        return (loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ())
                .replace('/', '_');
    }

    private static int depthOf(String path) {
        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') count++;
        }
        return count;
    }

    private static int minDepth(List<Observation> list) {
        int min = Integer.MAX_VALUE;
        for (Observation o : list) min = Math.min(min, depthOf(o.path()));
        return min;
    }

    private static boolean isUnderAny(String path, Set<String> removedPaths) {
        for (String removed : removedPaths) {
            if (path.startsWith(removed + "/")) return true;
        }
        return false;
    }

    private static String format(Location loc) {
        if (loc.getWorld() == null) return "?";
        return loc.getWorld().getName() + " " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    // ------------------------------------------------------------------ one scan

    private final class Pass {

        private final Map<UUID, List<Observation>> observations = new HashMap<>();
        private final Set<String> containersSeen = new HashSet<>();
        private final Visitor collector = this::collect;

        private int topItems;
        private int stamped;
        private int revokedRemoved;
        private Top current;

        private void addPlayer(Player player) {
            UUID id = player.getUniqueId();
            String holder = "player:" + id;
            String name = "Spieler " + player.getName();

            addInventory(player.getInventory(), "p:" + id + ":", Source.PLAYER, holder, name + " (Inventar)");
            addInventory(player.getEnderChest(), "e:" + id + ":", Source.ENDER_CHEST, holder, name + " (Endertruhe)");

            // Creative cursors are skipped: a creative clone sits on the cursor for a moment before
            // InventoryAbuseListener resets its serial.
            ItemStack cursor = player.getItemOnCursor();
            if (player.getGameMode() != GameMode.CREATIVE && isScannable(cursor)) {
                visitTop(cursor, new Top("c:" + id,
                        new SlotRef(player::getItemOnCursor, player::setItemOnCursor),
                        Source.CURSOR, holder, name + " (Cursor)"));
            }

            Inventory open = player.getOpenInventory().getTopInventory();
            if (isScannableContainer(open)) {
                String key = containerKey(open);
                if (key != null && containersSeen.add(key)) { // two viewers of one chest = one holder
                    addInventory(open, "b:" + key + ":", Source.CONTAINER, "container:" + key, "Container " + key);
                }
            }
        }

        private void addItemEntity(Item entity) {
            if (!entity.isValid()) return;
            ItemStack stack = entity.getItemStack();
            if (!isScannable(stack)) return;

            Top top = new Top("i:" + entity.getUniqueId(),
                    new SlotRef(entity::getItemStack, replacement -> {
                        if (replacement == null || replacement.getType().isAir()) {
                            entity.remove();
                        } else {
                            entity.setItemStack(replacement);
                        }
                    }),
                    Source.ENTITY, "entity:" + entity.getUniqueId(), "Item-Entity bei " + format(entity.getLocation()));
            visitTop(stack, top);
        }

        private void addInventory(Inventory inv, String keyPrefix, Source source, String holderKey, String label) {
            ItemStack[] contents = inv.getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (!isScannable(item)) continue;
                final int slot = i;
                visitTop(item, new Top(keyPrefix + i,
                        new SlotRef(() -> inv.getItem(slot), replacement -> inv.setItem(slot, replacement)),
                        source, holderKey, label));
            }
        }

        private void visitTop(ItemStack item, Top top) {
            topItems++;
            current = top;
            boolean[] changed = {false};
            ItemStack result = rewrite(item, top.key(), 0, collector, changed);
            if (changed[0]) {
                top.ref().setter().accept(result);
            }
        }

        /** Phase 1: stamp unmarked items, delete revoked ones, record every serial we see. */
        private Verdict collect(ItemStack item, String path) {
            UUID serial = ItemSerial.read(item);

            if (serial == null) {
                if (policy.isProtected(item)) {
                    ItemSerial.assign(item);
                    stamped++;
                    return Verdict.STAMPED;
                }
                return Verdict.KEEP;
            }

            if (registry.isRevoked(serial)) {
                if (!config.enforce()) return Verdict.KEEP;
                registry.quarantine(item, serial, current.label() + " (widerrufene Seriennummer)");
                alert.revokedRemoved(serial, item.getType().name(), current.label());
                revokedRemoved++;
                return Verdict.REMOVE;
            }

            observations.computeIfAbsent(serial, k -> new ArrayList<>(2))
                    .add(new Observation(serial, path, item.getType().name(), current));
            return Verdict.KEEP;
        }

        /** Phase 2 + 3: decide who keeps the item, then apply. */
        private Result finish(long startNanos) {
            // --- decide (outermost items first, so nested copies of a removed shulker vanish with it)
            List<Map.Entry<UUID, List<Observation>>> groups = new ArrayList<>();
            for (Map.Entry<UUID, List<Observation>> entry : observations.entrySet()) {
                if (entry.getValue().size() > 1) groups.add(entry);
            }
            groups.sort(Comparator.comparingInt(e -> minDepth(e.getValue())));

            List<Planned> planned = new ArrayList<>();
            Set<String> removedPaths = new HashSet<>();
            int removals = 0;

            for (Map.Entry<UUID, List<Observation>> group : groups) {
                List<Observation> live = new ArrayList<>();
                for (Observation o : group.getValue()) {
                    if (!isUnderAny(o.path(), removedPaths)) live.add(o);
                }
                if (live.size() < 2) continue;

                Observation keeper = chooseKeeper(group.getKey(), live);
                List<Observation> copies = new ArrayList<>();
                for (Observation o : live) {
                    if (o != keeper) {
                        copies.add(o);
                        removedPaths.add(o.path());
                    }
                }
                removals += copies.size();
                planned.add(new Planned(group.getKey(), keeper, copies, live));
            }

            // --- circuit breaker: a mass-removal is far more likely a false positive than a real dupe wave
            boolean enforce = config.enforce();
            if (enforce && removals > config.maxRemovalsPerScan) {
                alert.circuitBreaker(removals, config.maxRemovalsPerScan);
                enforce = false;
            }

            Map<String, Decision> decisions = new HashMap<>();
            Map<String, Top> affected = new LinkedHashMap<>();
            for (Planned p : planned) {
                report(p, enforce);
                if (!enforce) continue;

                registry.revoke(p.serial());
                decisions.put(p.keeper().path(),
                        new Decision(p.serial(), Verdict.REISSUE, p.keeper().top().label()));
                affected.put(p.keeper().top().key(), p.keeper().top());
                for (Observation copy : p.copies()) {
                    decisions.put(copy.path(), new Decision(p.serial(), Verdict.REMOVE, copy.top().label()));
                    affected.put(copy.top().key(), copy.top());
                }
            }

            // --- apply
            for (Top top : affected.values()) {
                ItemStack item = top.ref().getter().get();
                if (item == null || item.getType().isAir()) continue;

                boolean[] changed = {false};
                ItemStack result = rewrite(item, top.key(), 0, (visited, path) -> {
                    Decision decision = decisions.get(path);
                    // serial check guards against stale paths (e.g. bundle indexes that shifted)
                    if (decision == null || !decision.serial().equals(ItemSerial.read(visited))) {
                        return Verdict.KEEP;
                    }
                    if (decision.verdict() == Verdict.REMOVE) {
                        registry.quarantine(visited, decision.serial(), decision.label());
                    }
                    return decision.verdict();
                }, changed);

                if (changed[0]) {
                    top.ref().setter().accept(result);
                }
            }

            // --- remember where everything is (used as a tie-breaker next time)
            for (Map.Entry<UUID, List<Observation>> entry : observations.entrySet()) {
                if (entry.getValue().size() == 1) {
                    registry.noteHolder(entry.getKey(), entry.getValue().get(0).top().holderKey());
                }
            }

            return new Result(topItems, planned.size(), revokedRemoved, stamped,
                    (System.nanoTime() - startNanos) / 1_000_000L);
        }

        /**
         * Which copy is the original cannot be known. Trust order: player-held > ender chest >
         * container > dropped item. On a tie, prefer the holder we saw it with last time.
         */
        private Observation chooseKeeper(UUID serial, List<Observation> live) {
            String last = registry.lastHolder(serial);
            Observation best = null;
            for (Observation o : live) {
                if (best == null) {
                    best = o;
                    continue;
                }
                int cmp = Integer.compare(o.top().source().rank, best.top().source().rank);
                if (cmp < 0) {
                    best = o;
                } else if (cmp == 0 && last != null
                        && o.top().holderKey().equals(last) && !best.top().holderKey().equals(last)) {
                    best = o;
                }
            }
            return best;
        }

        private void report(Planned planned, boolean enforced) {
            long now = System.currentTimeMillis();
            Long last = alertedAt.get(planned.serial());
            if (last != null && now - last < ALERT_COOLDOWN_MS) return;
            alertedAt.put(planned.serial(), now);

            Set<String> labels = new LinkedHashSet<>();
            for (Observation o : planned.all()) labels.add(o.top().label());
            alert.dupeFound(planned.serial(), planned.keeper().type(), String.join(" | ", labels), enforced);
        }
    }
}
